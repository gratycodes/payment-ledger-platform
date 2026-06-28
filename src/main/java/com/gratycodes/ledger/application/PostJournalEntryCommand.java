package com.gratycodes.ledger.application;

import com.gratycodes.ledger.domain.Direction;
import java.util.List;

public record PostJournalEntryCommand(
    String idempotencyKey,
    String reference,
    String currency,
    List<Line> lines
) {
  public record Line(String accountId, Direction direction, long amountMinor) {
  }
}

