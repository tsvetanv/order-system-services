package com.tsvetanv.order.processing.integration.payment;

/**
 * Result returned from the Payment System.
 */
public record PaymentResult(
  PaymentStatus status,
  String reference
) {

}
