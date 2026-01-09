package com.tsvetanv.order.processing.integration.notification;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a domain-relevant notification emitted by the Order Processing System.
 *
 * <p>
 * This is an integration-level event, not a domain event. It is intended for external systems
 * (email, messaging, audit, etc.).
 * </p>
 */
public record OrderNotification(
  UUID orderId,
  UUID customerId,
  String type,
  Instant occurredAt
) {

}
