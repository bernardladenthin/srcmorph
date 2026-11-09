### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:09:48Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 3D451A40
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL payments ledger that records customers, accounts, transfers and fraud holds, and aggregates daily transfer volume.

#### Purpose
- Persist core payment entities: customers, accounts, transfers, and holds.
- Expose a daily volume summary view for reporting.

#### Responsibilities
- **Data persistence** – tables for customers, accounts, transfers, and holds with referential integrity.
- **Reporting** – `ledger_daily_volume` view that aggregates transfer amounts per day.
- **Indexing** – performance indexes on transfer source, booking timestamp, and hold account.

#### Key units
- `ledger_customer` – stores customer identity and country.
- `ledger_account` – links accounts to customers, holds IBAN and opening date.
- `ledger_transfer` – records fund movements with amount, source, destination, and booking time.
- `ledger_hold` – tracks fraud‑hold reasons per account.
- `ledger_daily_volume` – view summarizing total transfer volume per day.
- Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Insert phase** – bulk `INSERT` statements populate the four tables.
2. **Reporting phase** – `ledger_daily_volume` queries `ledger_transfer`, grouping by day and summing `amount_cents`.

#### Dependencies
- Foreign keys: `ledger_account.customer_id → ledger_customer`, `ledger_transfer.from_account → ledger_account`, `ledger_transfer.to_account → ledger_account`, `ledger_hold.account_id → ledger_account`.
- View depends on `ledger_transfer` for aggregation.

#### Cross-cutting
- All tables use `INTEGER` primary keys and enforce non‑null constraints.
- `ledger_transfer.amount_cents` has a check constraint (`> 0`).
- Indexes target common query patterns: filtering by source account, booking date, and hold account.
