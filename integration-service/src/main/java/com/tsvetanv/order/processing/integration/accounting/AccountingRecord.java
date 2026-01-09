package com.tsvetanv.order.processing.integration.accounting;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable accounting record emitted by the Order Processing System.
 *
 * <p>
 * This represents a financial fact that must be reported to external accounting systems for
 * compliance and reporting.
 * </p>
 */
public record AccountingRecord(
  UUID orderId,
  UUID customerId,
  String currency,
  String amount,
  Instant occurredAt
) {

}
