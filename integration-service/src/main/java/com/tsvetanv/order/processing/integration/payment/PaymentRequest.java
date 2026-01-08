package com.tsvetanv.order.processing.integration.payment;

import java.util.UUID;

/**
 * Immutable payment request sent to an external system.
 */
public record PaymentRequest(
  UUID orderId,
  // customerId → explicitly passed (important for real payment systems)
  UUID customerId,
  String currency,
  // amount → string (safe for serialization & external APIs)
  String amount
) {

}
