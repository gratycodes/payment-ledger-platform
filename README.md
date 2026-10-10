# payment-ledger-platform

A Spring Boot service that posts double-entry journal entries through an idempotent REST API. It is a small, readable starting point for a payments ledger: the domain rules are implemented and tested, while persistence, the outbox publisher and reconciliation are designed but not yet built.

## Why

Money movement has to stay correct under client retries and partial failures. This project shows how I model that core: balanced debit/credit legs, amounts in minor units, immutable entries, and idempotency as part of the API contract rather than an afterthought.

## What works today

- `POST /ledger/journal-entries` with a required `Idempotency-Key` header and request validation.
- Domain rules in `JournalEntry`: at least two lines, every amount positive, and the entry must balance per currency (debits equal credits), otherwise it is rejected.
- Money as `long` minor units with `java.util.Currency`; no floating point.
- Idempotent posting: a repeated key returns the original entry with status `duplicate_accepted` instead of posting again. Uses `putIfAbsent` so concurrent retries cannot double-post.
- Layered structure: `api` → `application` → `domain`, with `infra` behind a repository interface.
- PostgreSQL schema (Flyway `V1`) for journal entries, ledger lines and an outbox table, with a unique constraint on the idempotency key.
- Architecture decisions in `docs/adr/` (double-entry ledger, outbox pattern) and `docs/system-design.md`.

## Not built yet

- Entries are stored in an in-memory repository (`InMemoryJournalEntryRepository`), so data is lost on restart. The Postgres tables exist but nothing reads or writes them yet.
- The outbox table exists, but no outbox writer or publisher, and no Kafka integration.
- No balance queries, settlement or reconciliation.
- Domain rule violations (for example an unbalanced entry) currently surface as HTTP 500; there is no error-mapping handler yet.
- Tests cover the domain invariants only; there are no API or integration tests yet.

## Run it

Requires Java 21, Maven and Docker.

```bash
docker compose -f infra/docker-compose.yml up -d   # Postgres on localhost:5433 (Flyway applies the schema on startup)
mvn test
mvn spring-boot:run
```

Post an entry:

```bash
curl -i -X POST localhost:8080/ledger/journal-entries \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: capture_123' \
  -d '{
    "reference": "payment_capture_123",
    "currency": "EUR",
    "lines": [
      { "accountId": "merchant_cash",    "direction": "DEBIT",  "amountMinor": 1999 },
      { "accountId": "platform_payable", "direction": "CREDIT", "amountMinor": 1999 }
    ]
  }'
```

Returns `202 Accepted` with the entry ID and status `posted`. Sending the same request again returns the same ID with status `duplicate_accepted`.

## Layout

```text
src/main/java/com/gratycodes/ledger/
  api/            controller and request DTOs
  application/    PostJournalEntryService, repository port, command/result types
  domain/         JournalEntry, LedgerLine, Money, Direction (invariants live here)
  infra/          in-memory repository
src/main/resources/db/migration/   Flyway schema
docs/             system design and ADRs
infra/            Docker Compose for Postgres
```

## Stack

Java 21, Spring Boot 3.3, Maven, Flyway, PostgreSQL 16, JUnit 5, Docker Compose, GitHub Actions.

## Next steps

1. JDBC repository that writes the entry, its lines and an outbox event in one transaction.
2. Outbox publisher to Kafka with retries and dead-letter tracking.
3. Exception handler mapping domain errors to 400/409.
4. Account balance query and API tests.

## License

MIT
