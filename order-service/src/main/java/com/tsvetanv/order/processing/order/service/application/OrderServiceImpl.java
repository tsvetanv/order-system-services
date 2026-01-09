package com.tsvetanv.order.processing.order.service.application;

import com.tsvetanv.order.processing.integration.accounting.AccountingRecord;
import com.tsvetanv.order.processing.integration.accounting.AccountingService;
import com.tsvetanv.order.processing.integration.inventory.InventoryCheckRequest;
import com.tsvetanv.order.processing.integration.inventory.InventoryService;
import com.tsvetanv.order.processing.integration.notification.NotificationService;
import com.tsvetanv.order.processing.integration.notification.OrderNotification;
import com.tsvetanv.order.processing.integration.payment.PaymentFailedException;
import com.tsvetanv.order.processing.integration.payment.PaymentRequest;
import com.tsvetanv.order.processing.integration.payment.PaymentResult;
import com.tsvetanv.order.processing.integration.payment.PaymentService;
import com.tsvetanv.order.processing.integration.payment.PaymentStatus;
import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.entity.OrderItemEntity;
import com.tsvetanv.order.processing.order.database.repository.OrderRepository;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.application.money.Money;
import com.tsvetanv.order.processing.order.service.application.pricing.PricingService;
import com.tsvetanv.order.processing.order.service.application.pricing.ProductPricingService;
import com.tsvetanv.order.processing.order.service.exception.OrderCancellationNotAllowedException;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import com.tsvetanv.order.processing.order.service.mapping.integration.InventoryIntegrationMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Getter
@Setter
@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private PricingService pricingService;

  @Autowired
  private ProductPricingService productPricingService;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private InventoryService inventoryService;

  @Autowired
  private InventoryIntegrationMapper inventoryIntegrationMapper;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private AccountingService accountingService;

  @Override
  @Transactional(readOnly = true)
  public OrderEntity getOrderById(UUID orderId) {
    log.debug("Fetching order with id={}", orderId);

    return orderRepository.findById(orderId)
      .orElseThrow(() -> {
        log.warn("Order with id={} not found", orderId);
        return new OrderNotFoundException(orderId);
      });
  }

  @Override
  @Transactional
  public UUID createOrder(CreateOrderDto dto) {
    log.info("Creating order for customerId={}", dto.getCustomerId());

    // Create order aggregate
    OrderEntity order = OrderEntity.builder()
      .id(UUID.randomUUID())
      .customerId(dto.getCustomerId())
      .status(OrderStatus.CREATED)
      .createdAt(Instant.now())
      .build();

    // Resolve pricing & build order items
    dto.getItems().forEach(item -> {

      Money unitPrice = productPricingService.getUnitPrice(
        item.getProductId(),
        dto.getCurrency()
      );

      order.getItems().add(
        OrderItemEntity.builder()
          .id(UUID.randomUUID())
          .order(order)
          .productId(item.getProductId())
          .quantity(item.getQuantity())
          .unitAmount(unitPrice.amount())
          .currency(unitPrice.currency())
          .build()
      );
    });

    // Check inventory availability (integration boundary)
    InventoryCheckRequest inventoryRequest =
      inventoryIntegrationMapper.toInventoryCheckRequest(order);
    inventoryService.checkAvailability(inventoryRequest);

    // Persist initial state before payment (important for auditability)
    orderRepository.save(order);

    // Calculate total and request payment
    Money totalAmount = pricingService.calculateOrderTotal(order);

    PaymentResult paymentResult = paymentService.authorizePayment(
      new PaymentRequest(order.getId(), order.getCustomerId(),
        totalAmount.currency(), totalAmount.amount().toPlainString())
    );

    if (paymentResult.status() != PaymentStatus.AUTHORIZED) {
      log.warn("Payment failed | orderId={} | status={}", order.getId(), paymentResult.status());
      throw new PaymentFailedException(order.getId(), paymentResult.status().name());
    }

    // Payment successful → confirm order
    order.setStatus(OrderStatus.CONFIRMED);
    order.setUpdatedAt(Instant.now());
    orderRepository.save(order);

    // Notification after confirmation
    notificationService.send(
      new OrderNotification(order.getId(), order.getCustomerId(), "ORDER_CONFIRMED", Instant.now())
    );

    // Accounting (non-blocking, best effort)
    accountingService.report(
      new AccountingRecord(
        order.getId(),
        order.getCustomerId(),
        totalAmount.currency(),
        totalAmount.amount().toPlainString(),
        Instant.now()
      )
    );

    log.info("Order created and confirmed | orderId={}", order.getId());
    return order.getId();
  }

  @Override
  @Transactional
  public void cancelOrder(UUID orderId) {
    log.info("Cancelling order | orderId={}", orderId);

    OrderEntity order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    OrderStatus currentStatus = order.getStatus();
    if (currentStatus == OrderStatus.CANCELLED) {
      log.info("Order already cancelled | orderId={}", orderId);
      return; // idempotent
    }
    if (!OrderStatusTransitionPolicy.canCancel(currentStatus)) {
      throw new OrderCancellationNotAllowedException(orderId, currentStatus);
    }
    order.setStatus(OrderStatus.CANCELLED);
    order.setUpdatedAt(Instant.now());
    orderRepository.save(order);
    log.info("Order cancelled | orderId={}", orderId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<OrderEntity> listOrders(
    int limit,
    int offset,
    OrderStatus status,
    UUID customerId,
    String sort
  ) {
    log.debug("Listing orders | limit={} | offset={} | status={} | customerId={} | sort={}",
      limit, offset, status, customerId, sort);

    // 1. Safe Pagination Calculation
    int pageNumber = offset / limit;

    // 2. Robust Sorting (Handles malformed strings gracefully)
    Sort sortSpec = parseSort(sort);

    Pageable pageable = PageRequest.of(pageNumber, limit, sortSpec);

    // 3. Dynamic Filtering using Specifications
    Specification<OrderEntity> spec = Specification.where(null);

    if (status != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    if (customerId != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("customerId"), customerId));
    }

    return orderRepository.findAll(spec, pageable);
  }

  private Sort parseSort(String sort) {
    if (sort == null || !sort.contains(",")) {
      return Sort.by(Sort.Direction.DESC, "createdAt"); // Default safe sort
    }
    String[] parts = sort.split(",");
    try {
      return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
    } catch (Exception e) {
      return Sort.by(Sort.Direction.DESC, "createdAt");
    }
  }

}
