# ADR 0002: Use the Outbox Pattern

## Status

Accepted

## Context

Publishing events directly to a broker inside request handling can create inconsistencies when database commits and broker writes fail independently.

## Decision

Write outbox records in the same transaction as ledger records. A separate publisher sends committed events downstream.

## Consequences

- Downstream consumers see only committed ledger events.
- Event publishing becomes retryable.
- Consumers must handle at-least-once delivery.

