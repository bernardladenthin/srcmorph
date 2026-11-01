### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:38:01Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL data‑dump that seeds a payments ledger with customers, accounts, transfers, holds, and a daily‑volume view.

#### Purpose
- Seed the payments ledger tables with realistic test data.
- Provide a materialised view for daily transfer volume.

#### Responsibilities
- **Data population**: Insert rows into `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- **Analytics support**: Create `ledger_daily_volume` view aggregating transfer volume per day.
- **Indexing**: Define indexes to optimise transfer and hold lookups.

#### Key units
- **Tables**
  - `ledger_customer` – customer metadata (id, name, country).
  - `ledger_account` – bank accounts linked to customers (iban, open date).
  - `ledger_transfer` – fund movements between accounts (amount, timestamp).
  - `ledger_hold` – holds on accounts (reason).
- **View**
  - `ledger_daily_volume` – aggregates `amount_cents` by booking date.
- **Indexes**
  - `idx_transfer_from` (ledger_transfer.from_account)
  - `idx_transfer_booked` (ledger_transfer.booked_at)
  - `idx_hold_account` (ledger_hold.account_id)

#### Data flow
1. **Customers** → `ledger_customer` (4 000 rows).
2. **Accounts** → `ledger_account` (4 000 rows, FK to customers).
3. **Transfers** → `ledger_transfer` (≈48 000 rows, FK to accounts).
4. **Holds** → `ledger_hold` (50 rows, FK to accounts).
5. **View** `ledger_daily_volume` reads `ledger_transfer` to compute daily volume.

#### Dependencies
- Foreign keys: `ledger_account.customer_id → ledger_customer.customer_id`; `ledger_transfer.from_account → ledger_account.account_id`; `ledger_transfer.to_account → ledger_account.account_id`; `ledger_hold.account_id → ledger_account.account_id`.
- View `ledger_daily_volume` depends on `ledger_transfer`.

#### Cross‑cutting
- Consistent use of `INTEGER` primary keys and `BIGINT` for monetary amounts.
- Referential integrity enforced via foreign keys.
- Indexes target common query patterns on transfer source, booking time, and hold account.
