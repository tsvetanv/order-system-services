package com.tsvetanv.order.processing.order.service.exception;

import com.tsvetanv.order.processing.order.database.domain.OrderStatus;
import java.util.UUID;

public class OrderCancellationNotAllowedException extends RuntimeException {

  public OrderCancellationNotAllowedException(UUID orderId, OrderStatus status) {
    super("Order " + orderId + " cannot be cancelled from status " + status);
  }
}
