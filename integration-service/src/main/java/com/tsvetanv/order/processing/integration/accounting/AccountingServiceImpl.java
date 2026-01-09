package com.tsvetanv.order.processing.integration.accounting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Stub implementation of AccountingService.
 *
 * <p>
 * Logs accounting records and always succeeds.
 * </p>
 */
@Slf4j
@Service
public class AccountingServiceImpl implements AccountingService {

  @Override
  public void report(AccountingRecord record) {
    log.info(
      "Reporting order to accounting | orderId={} | amount={} {}",
      record.orderId(),
      record.amount(),
      record.currency()
    );
  }
}
