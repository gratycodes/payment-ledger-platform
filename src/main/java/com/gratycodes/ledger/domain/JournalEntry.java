package com.gratycodes.ledger.domain;

import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public record JournalEntry(
    UUID id,
    String reference,
    List<LedgerLine> lines,
    Instant postedAt
) {
  public JournalEntry {
    Objects.requireNonNull(id, "id is required");
    if (reference == null || reference.isBlank()) {
      throw new IllegalArgumentException("reference is required");
    }
    lines = List.copyOf(lines);
    if (lines.size() < 2) {
      throw new IllegalArgumentException("journal entry requires at least two lines");
    }
    ensureBalanced(lines);
    Objects.requireNonNull(postedAt, "postedAt is required");
  }

  public static JournalEntry post(String reference, List<LedgerLine> lines) {
    return new JournalEntry(UUID.randomUUID(), reference, lines, Instant.now());
  }

  private static void ensureBalanced(List<LedgerLine> lines) {
    Map<Currency, Long> balanceByCurrency = lines.stream()
        .collect(Collectors.groupingBy(
            line -> line.money().currency(),
            Collectors.summingLong(JournalEntry::signedAmount)
        ));

    boolean balanced = balanceByCurrency.values().stream().allMatch(total -> total == 0);
    if (!balanced) {
      throw new IllegalArgumentException("journal entry must balance per currency");
    }
  }

  private static long signedAmount(LedgerLine line) {
    return line.direction() == Direction.DEBIT
        ? line.money().amountMinor()
        : -line.money().amountMinor();
  }
}

