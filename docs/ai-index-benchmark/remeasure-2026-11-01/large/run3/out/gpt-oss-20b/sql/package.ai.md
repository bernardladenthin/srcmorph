### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:16:25Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A PostgreSQL schema that stores customers, accounts, transfers, and holds, and provides a daily transfer volume view.

#### Purpose
- Persist financial ledger data: customers, accounts, transfers, and holds.
- Expose aggregated daily transfer volume.

#### Responsibilities
- **Data modeling** – tables for customers, accounts, transfers, and holds.
- **Aggregation** – view `ledger_daily_volume` summarizing transfer volume per day.
- **Indexing** – performance indexes on transfer source, booking time, and hold account.

#### Key units
- `ledger_customer` – customer identity and country.
- `ledger_account` – account linked to a customer, with IBAN and open date.
- `ledger_transfer` – money movement with amount, source, destination, and booking timestamp.
- `ledger_hold` – temporary hold on an account with a reason.
- `ledger_daily_volume` – view aggregating `ledger_transfer` amounts by day.
- Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. Insert customers → `ledger_customer`.
2. Insert accounts referencing customers → `ledger_account`.
3. Insert transfers referencing accounts → `ledger_transfer`.
4. Insert holds referencing accounts → `ledger_hold`.
5. Query `ledger_daily_volume` for daily transfer totals.

#### Dependencies
- `ledger_account` references `ledger_customer`.
- `ledger_transfer` references `ledger_account` for both `from_account` and `to_account`.
- `ledger_hold` references `ledger_account`.
- `ledger_daily_volume` aggregates data from `ledger_transfer`.

#### Cross-cutting
- Integer primary keys across all tables.
- Foreign key constraints enforce referential integrity.
- Indexes target common query patterns: transfer source, booking date, and hold account lookups.
