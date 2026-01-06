package com.tsvetanv.order.processing.order.service.application.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable value object representing a monetary amount.
 */
public record Money(BigDecimal amount, String currency) {

  public Money {
    Objects.requireNonNull(amount, "amount must not be null");
    Objects.requireNonNull(currency, "currency must not be null");

    amount = amount.setScale(4, RoundingMode.HALF_UP);
  }

  public Money add(Money other) {
    ensureSameCurrency(other);
    return new Money(this.amount.add(other.amount), currency);
  }

  public Money multiply(int factor) {
    return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), currency);
  }

  private void ensureSameCurrency(Money other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException("Currency mismatch");
    }
  }
}
