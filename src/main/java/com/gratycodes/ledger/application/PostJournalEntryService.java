package com.gratycodes.ledger.application;

import com.gratycodes.ledger.domain.JournalEntry;
import com.gratycodes.ledger.domain.LedgerLine;
import com.gratycodes.ledger.domain.Money;
import java.util.Currency;
import org.springframework.stereotype.Service;

@Service
public class PostJournalEntryService {
  private final JournalEntryRepository repository;

  public PostJournalEntryService(JournalEntryRepository repository) {
    this.repository = repository;
  }

  public JournalEntryResult post(PostJournalEntryCommand command) {
    return repository.findByIdempotencyKey(command.idempotencyKey())
        .map(existing -> toResult(existing, "duplicate_accepted"))
        .orElseGet(() -> postNewEntry(command));
  }

  private JournalEntryResult postNewEntry(PostJournalEntryCommand command) {
    Currency currency = Currency.getInstance(command.currency());
    var lines = command.lines().stream()
        .map(line -> new LedgerLine(
            line.accountId(),
            line.direction(),
            new Money(currency, line.amountMinor())
        ))
        .toList();

    JournalEntry entry = JournalEntry.post(command.reference(), lines);
    JournalEntry saved = repository.save(command.idempotencyKey(), entry);
    return toResult(saved, "posted");
  }

  private static JournalEntryResult toResult(JournalEntry entry, String status) {
    return new JournalEntryResult(entry.id(), entry.reference(), status, entry.postedAt());
  }
}

