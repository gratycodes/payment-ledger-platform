package com.gratycodes.ledger.application;

import com.gratycodes.ledger.domain.JournalEntry;
import java.util.Optional;

public interface JournalEntryRepository {
  Optional<JournalEntry> findByIdempotencyKey(String idempotencyKey);

  JournalEntry save(String idempotencyKey, JournalEntry entry);
}

