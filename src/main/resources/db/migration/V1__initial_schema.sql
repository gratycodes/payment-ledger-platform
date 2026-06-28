create table journal_entries (
  id uuid primary key,
  idempotency_key text not null unique,
  reference text not null,
  posted_at timestamptz not null
);

create table ledger_lines (
  id bigserial primary key,
  journal_entry_id uuid not null references journal_entries(id),
  account_id text not null,
  direction text not null check (direction in ('DEBIT', 'CREDIT')),
  currency char(3) not null,
  amount_minor bigint not null check (amount_minor > 0)
);

create table outbox_events (
  id uuid primary key,
  aggregate_id uuid not null,
  event_type text not null,
  payload jsonb not null,
  created_at timestamptz not null,
  published_at timestamptz
);

create index idx_ledger_lines_account_id on ledger_lines(account_id);
create index idx_outbox_events_unpublished on outbox_events(created_at) where published_at is null;

