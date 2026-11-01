### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:16:22Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL schema that records customers, accounts, transfers, and holds, and exposes a daily transfer volume view.

#### Purpose
- Persist financial ledger data: customers, accounts, transfers, and holds.
- Provide aggregated daily transfer volume.

#### Responsibilities
- **Data modeling**: tables for customers, accounts, transfers, and holds.
- **Aggregation**: view `ledger_daily_volume` summarizing transfer volume per day.
- **Indexing**: performance indexes on transfer source, booking time, and hold account.

#### Key units
- `ledger_customer` – stores customer identity and country.
- `ledger_account` – links accounts to customers, holds IBAN and open date.
- `ledger_transfer` – records money movements with amount, source, destination, and booking timestamp.
- `ledger_hold` – tracks temporary holds on accounts with reason.
- `ledger_daily_volume` – view that aggregates `ledger_transfer` amounts by day.
- Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Insert** customers → `ledger_customer`.
2. **Insert** accounts referencing customers → `ledger_account`.
3. **Insert** transfers referencing accounts → `ledger_transfer`.
4. **Insert** holds referencing accounts → `ledger_hold`.
5. **Query** `ledger_daily_volume` to obtain daily transfer totals.

#### Dependencies
- `ledger_account` → `ledger_customer`.
- `ledger_transfer` → `ledger_account` (both `from_account` and `to_account`).
- `ledger_hold` → `ledger_account`.
- `ledger_daily_volume` aggregates data from `ledger_transfer`.

#### Cross-cutting
- All tables use integer primary keys.
- Foreign key constraints enforce referential integrity.
- Indexes target common query patterns: transfer source, booking date, and hold account lookups.
