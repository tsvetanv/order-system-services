package com.tsvetanv.order.processing.order.service.application;

import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import com.tsvetanv.order.processing.order.database.entity.OrderEntity;
import com.tsvetanv.order.processing.order.database.entity.OrderItemEntity;
import com.tsvetanv.order.processing.order.database.repository.OrderRepository;
import com.tsvetanv.order.processing.order.service.application.dto.CreateOrderDto;
import com.tsvetanv.order.processing.order.service.exception.OrderCancellationNotAllowedException;
import com.tsvetanv.order.processing.order.service.exception.OrderNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

  private static final BigDecimal INITIAL_UNIT_AMOUNT = BigDecimal.ZERO;

  @Autowired
  private OrderRepository orderRepository;

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

    OrderEntity order = OrderEntity.builder()
      .id(UUID.randomUUID())
      .customerId(dto.getCustomerId())
      .status("CREATED")
      .createdAt(Instant.now())
      .build();

    dto.getItems().forEach(item ->
      order.getItems().add(
        OrderItemEntity.builder()
          .id(UUID.randomUUID())
          .order(order)
          .productId(item.getProductId())
          .quantity(item.getQuantity())
          // TODO: Pricing is not part of order creation yet
          .unitAmount(INITIAL_UNIT_AMOUNT)
          .currency(dto.getCurrency())
          .build()
      )
    );

    orderRepository.save(order);

    log.info("Order created with id={}", order.getId());
    return order.getId();
  }

  /**
   * Cancels an existing order.
   *
   * <p><strong>Business rules:</strong></p>
   * <ul>
   *   <li>An order can be cancelled only if its status is {@link OrderStatus#CREATED} or {@link OrderStatus#CONFIRMED}.</li>
   *   <li>An order cannot be cancelled if its status is {@link OrderStatus#PAID} or {@link OrderStatus#SHIPPED}.</li>
   *   <li>Calling cancel on an already {@link OrderStatus#CANCELLED} order is idempotent and has no effect.</li>
   * </ul>
   *
   * <p><strong>Behavior:</strong></p>
   * <ul>
   *   <li>If the order does not exist, {@link OrderNotFoundException} is thrown.</li>
   *   <li>If cancellation is not allowed due to the current order status,
   *       {@link OrderCancellationNotAllowedException}
   *       is thrown.</li>
   *   <li>On successful cancellation, the order status is set to {@link OrderStatus#CANCELLED}.</li>
   * </ul>
   *
   * @param orderId the identifier of the order to cancel
   */
  @Override
  @Transactional
  public void cancelOrder(UUID orderId) {
    log.info("Cancelling order | orderId={}", orderId);

    OrderEntity order = orderRepository.findById(orderId)
      .orElseThrow(() -> new OrderNotFoundException(orderId));

    OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());
    if (currentStatus == OrderStatus.CANCELLED) {
      log.info("Order already cancelled | orderId={}", orderId);
      return; // idempotent
    }
    if (!OrderStatusTransitionPolicy.canCancel(currentStatus)) {
      throw new OrderCancellationNotAllowedException(orderId, currentStatus);
    }
    order.setStatus(OrderStatus.CANCELLED.name());
    order.setUpdatedAt(Instant.now());
    orderRepository.save(order);
    log.info("Order cancelled | orderId={}", orderId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<OrderEntity> listOrders(int limit, int offset) {
    log.debug("Listing orders | limit={} | offset={}", limit, offset);
    int page = offset / limit;
    Pageable pageable = PageRequest.of(page, limit);
    return orderRepository.findAll(pageable);
  }

}
