### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:15:09Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: CF557711
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL data‑dump that builds a payments ledger schema, seeds it with historical data, and exposes a daily volume view for analytics.

#### Purpose
- Provision a fully populated ledger database for testing, analytics, and development.
- Offer a ready‑to‑query view of daily transfer volumes.

#### Responsibilities
- **Schema creation** – tables for customers, accounts, transfers, and holds.
- **Data seeding** – bulk inserts of thousands of rows for realistic load.
- **Analytics support** – a view aggregating transfer volume per day.
- **Indexing** – performance indexes on key query columns.

#### Key units
- `ledger_customer` – customer master table.
- `ledger_account` – bank account master, linked to customers.
- `ledger_transfer` – transaction ledger, records source/destination and amount.
- `ledger_hold` – holds on accounts with reasons.
- `ledger_daily_volume` – view summarizing total transfer volume per calendar day.
- Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Insert** bulk rows into `ledger_customer`.
2. **Insert** bulk rows into `ledger_account` referencing customers.
3. **Insert** bulk rows into `ledger_transfer` referencing accounts.
4. **Insert** bulk rows into `ledger_hold` referencing accounts.
5. **View** `ledger_daily_volume` aggregates `ledger_transfer` by date, providing daily totals.

#### Dependencies
- Foreign keys: `ledger_account.customer_id → ledger_customer`, `ledger_transfer.from_account → ledger_account`, `ledger_transfer.to_account → ledger_account`, `ledger_hold.account_id → ledger_account`.
- View depends on `ledger_transfer` for aggregation.

#### Cross-cutting
- All tables use standard PostgreSQL types and constraints (PRIMARY KEY, UNIQUE, CHECK).
- Indexes target common query patterns: filtering by source account, booking date, and hold account.
- No triggers, functions, or extensions are defined; the dump is purely DDL + data.
