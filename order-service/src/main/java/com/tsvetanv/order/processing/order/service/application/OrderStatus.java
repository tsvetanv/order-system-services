package com.tsvetanv.order.processing.order.service.application;

/**
 * Domain-level order lifecycle states.
 */
public enum OrderStatus {
  CREATED,
  CONFIRMED,
  PAID,
  SHIPPED,
  CANCELLED
}
