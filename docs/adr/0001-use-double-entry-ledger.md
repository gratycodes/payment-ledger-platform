# ADR 0001: Use Double-Entry Ledger Modeling

## Status

Accepted

## Context

Financial systems need auditability and reliable reconstruction of balance changes.

## Decision

Represent money movement as journal entries containing debit and credit ledger lines. Each journal entry must balance per currency.

## Consequences

- Correctness is enforced in the domain model.
- Balance projections can be rebuilt from immutable entries.
- API callers must provide both sides of a movement or use a higher-level command that expands into balanced lines.

