package com.gratycodes.ledger.domain;

import java.util.Objects;

public record LedgerLine(String accountId, Direction direction, Money money) {
  public LedgerLine {
    if (accountId == null || accountId.isBlank()) {
      throw new IllegalArgumentException("accountId is required");
    }
    Objects.requireNonNull(direction, "direction is required");
    Objects.requireNonNull(money, "money is required");
  }
}

