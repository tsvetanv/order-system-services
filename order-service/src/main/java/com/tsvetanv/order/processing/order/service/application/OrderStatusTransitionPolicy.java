package com.tsvetanv.order.processing.order.service.application;

import java.util.EnumSet;
import java.util.Set;

public final class OrderStatusTransitionPolicy {

  private static final Set<OrderStatus> CANCELLABLE_STATUSES =
    EnumSet.of(OrderStatus.CREATED, OrderStatus.CONFIRMED);

  private OrderStatusTransitionPolicy() {
  }

  public static boolean canCancel(OrderStatus status) {
    return CANCELLABLE_STATUSES.contains(status);
  }
}
