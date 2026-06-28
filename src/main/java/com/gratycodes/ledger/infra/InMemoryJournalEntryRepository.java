package com.gratycodes.ledger.infra;

import com.gratycodes.ledger.application.JournalEntryRepository;
import com.gratycodes.ledger.domain.JournalEntry;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryJournalEntryRepository implements JournalEntryRepository {
  private final Map<String, JournalEntry> entriesByIdempotencyKey = new ConcurrentHashMap<>();

  @Override
  public Optional<JournalEntry> findByIdempotencyKey(String idempotencyKey) {
    return Optional.ofNullable(entriesByIdempotencyKey.get(idempotencyKey));
  }

  @Override
  public JournalEntry save(String idempotencyKey, JournalEntry entry) {
    JournalEntry existing = entriesByIdempotencyKey.putIfAbsent(idempotencyKey, entry);
    return existing == null ? entry : existing;
  }
}

