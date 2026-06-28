package com.gratycodes.ledger.application;

import java.time.Instant;
import java.util.UUID;

public record JournalEntryResult(
    UUID journalEntryId,
    String reference,
    String status,
    Instant postedAt
) {
}

