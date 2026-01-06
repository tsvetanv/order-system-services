package com.tsvetanv.order.processing.order.service.application.pricing;

import com.tsvetanv.order.processing.order.service.application.money.Money;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Temporary pricing implementation.
 *
 * <p>Returns a fixed unit price until real pricing is integrated.</p>
 */
@Slf4j
@Service
public class DefaultProductPricingService implements ProductPricingService {

  private static final BigDecimal DEFAULT_UNIT_PRICE =
    new BigDecimal("10.00");

  @Override
  public Money getUnitPrice(UUID productId, String currency) {
    log.debug(
      "Resolving unit price | productId={} | currency={}",
      productId,
      currency
    );
    return new Money(DEFAULT_UNIT_PRICE, currency);
  }
}
