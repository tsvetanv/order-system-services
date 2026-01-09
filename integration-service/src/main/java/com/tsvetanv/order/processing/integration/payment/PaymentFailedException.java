package com.tsvetanv.order.processing.integration.payment;

import java.util.UUID;

/**
 * Thrown when payment authorization fails for an order.
 */
public class PaymentFailedException extends RuntimeException {

  public PaymentFailedException(UUID orderId, String reason) {
    super(
      "Payment failed for orderId=" + orderId +
        (reason != null ? " | reason=" + reason : "")
    );
  }
}
