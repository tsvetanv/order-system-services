package com.tsvetanv.order.processing.order.service.application;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderService {

  private static final BigDecimal INITIAL_UNIT_AMOUNT = BigDecimal.ZERO;

  @Autowired
  private OrderRepository orderRepository;

  @Transactional(readOnly = true)
  public OrderEntity getOrderById(UUID orderId) {
    log.debug("Fetching order with id={}", orderId);

    return orderRepository.findById(orderId)
      .orElseThrow(() -> {
        log.warn("Order with id={} not found", orderId);
        return new OrderNotFoundException(orderId);
      });
  }

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
   * Cancels an order according to business rules.
   */
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
}
