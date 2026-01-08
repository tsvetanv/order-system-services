package com.tsvetanv.order.processing.integration.payment;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stub implementation used for demo and early iterations.
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

  @Override
  public PaymentResult authorizePayment(PaymentRequest request) {
    log.info(
      "Authorizing payment | orderId={} | amount={} {}",
      request.orderId(),
      request.amount(),
      request.currency()
    );

    // Always authorize in demo mode
    return new PaymentResult(
      PaymentStatus.AUTHORIZED,
      "PAY-" + UUID.randomUUID()
    );
  }
}

