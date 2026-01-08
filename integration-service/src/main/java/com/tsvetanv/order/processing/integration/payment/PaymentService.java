package com.tsvetanv.order.processing.integration.payment;

/**
 * Integration contract for payment processing.
 *
 * <p>This interface represents an outbound integration
 * to an external Payment System.</p>
 *
 * <p>It is intentionally synchronous and minimal.
 * Advanced concerns (retries, async, idempotency) are deferred to later iterations.</p>
 */
public interface PaymentService {

  /**
   * Requests payment authorization for an order.
   *
   * @param request payment request details
   * @return payment result
   */
  PaymentResult authorizePayment(PaymentRequest request);
}
