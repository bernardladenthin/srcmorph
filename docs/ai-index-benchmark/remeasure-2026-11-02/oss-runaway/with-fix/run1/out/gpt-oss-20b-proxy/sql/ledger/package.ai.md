### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:20Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Provides a fully‑populated PostgreSQL payments ledger schema for development and reporting, including customers, accounts, transfers, holds, and a daily volume view.

#### Purpose
- Define core ledger tables and a daily volume view.
- Seed tables with realistic test data for development and reporting.

#### Responsibilities
- **Schema definition** – tables, indexes, and a view for daily transfer volume.
- **Data seeding** – bulk INSERTs of customers, accounts, transfers, and holds.
- **Referential integrity** – foreign keys linking accounts to customers and transfers/holds to accounts.

#### Key units
- `ledger_customer` – customer lookup table (id, name, country).
- `ledger_account` – bank account table (id, customer, IBAN, open date).
- `ledger_transfer` – transaction table (id, from/to accounts, amount, timestamp).
- `ledger_hold` – temporary hold table (id, account).
- `ledger_daily_volume` – view aggregating `amount_cents` per day from `ledger_transfer`.
- Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Customers** inserted → `ledger_customer`.
2. **Accounts** inserted → `ledger_account` (FK to customers).
3. **Transfers** inserted → `ledger_transfer` (FK to accounts).
4. **Holds** inserted → `ledger_hold` (FK to accounts).
5. **View** `ledger_daily_volume` reads `ledger_transfer` to provide daily totals.

#### Dependencies
- Internal FK relationships: accounts ↔ customers, transfers/holds ↔ accounts.
- View depends on `ledger_transfer` for aggregation.

#### Cross‑cutting
- Consistent use of `INTEGER PRIMARY KEY` and `BIGINT` for amounts.
- `CHECK (amount_cents > 0)` ensures positive transfer amounts.
- Unique constraint on `ledger_account.iban`.
- Indexes on foreign keys and timestamp for query performance.
