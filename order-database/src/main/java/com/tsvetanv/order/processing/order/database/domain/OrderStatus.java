package com.tsvetanv.order.processing.order.database.domain;

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
