package com.gratycodes.ledger.domain;

import java.util.Currency;
import java.util.Objects;

public record Money(Currency currency, long amountMinor) {
  public Money {
    Objects.requireNonNull(currency, "currency is required");
    if (amountMinor <= 0) {
      throw new IllegalArgumentException("amountMinor must be positive");
    }
  }
}

