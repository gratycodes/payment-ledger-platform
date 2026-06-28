package com.gratycodes.ledger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.gratycodes.ledger.domain.Direction;
import com.gratycodes.ledger.domain.JournalEntry;
import com.gratycodes.ledger.domain.LedgerLine;
import com.gratycodes.ledger.domain.Money;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.Test;

class JournalEntryTest {
  private static final Currency EUR = Currency.getInstance("EUR");

  @Test
  void acceptsBalancedEntry() {
    assertDoesNotThrow(() -> JournalEntry.post(
        "capture_123",
        List.of(
            new LedgerLine("merchant_cash", Direction.DEBIT, new Money(EUR, 1999)),
            new LedgerLine("platform_payable", Direction.CREDIT, new Money(EUR, 1999))
        )
    ));
  }

  @Test
  void rejectsUnbalancedEntry() {
    assertThrows(IllegalArgumentException.class, () -> JournalEntry.post(
        "capture_124",
        List.of(
            new LedgerLine("merchant_cash", Direction.DEBIT, new Money(EUR, 1999)),
            new LedgerLine("platform_payable", Direction.CREDIT, new Money(EUR, 1499))
        )
    ));
  }
}

