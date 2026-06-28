# System Design

## Problem

Payment systems must be correct under retries, partial failures, duplicate client submissions, reconciliation delays, and downstream event delivery failures.

This project models the core backend patterns needed for that environment.

## Invariants

- Every journal entry must balance per currency.
- Posted ledger entries are immutable.
- Idempotency keys prevent duplicate financial posting.
- Events are published only for committed ledger entries.
- Reconciliation exceptions are tracked separately from ledger mutation.

## Transaction Boundary

Posting a journal entry should write these records in one database transaction:

1. `journal_entries`
2. `ledger_lines`
3. `outbox_events`

The outbox publisher reads unpublished events after commit and publishes them to a broker.

## Why Double-Entry

A single balance update is easy to corrupt and hard to audit. Double-entry posting records both sides of the movement, making invariants explicit and enabling historical reconstruction.

## Scaling Notes

- Partition ledger lines by posting month or tenant for very large datasets.
- Keep account balance projections separate from immutable journal lines.
- Use optimistic locking or database uniqueness constraints for idempotency.
- Publish events from the outbox in batches with retry and poison-message handling.

