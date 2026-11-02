### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:10:55Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A PostgreSQL data‑dump that creates a fully populated payments ledger schema, seeds it with realistic test data, and exposes a daily transfer volume view for analytics.

#### Purpose
- Provide a ready‑to‑use payments ledger database for testing and demos.
- Offer a pre‑built view summarising daily transfer volume.

#### Responsibilities
- **Schema definition** – tables for customers, accounts, transfers, and holds.
- **Data population** – bulk INSERTs for thousands of customers, accounts, transfers, and holds.
- **Analytics** – a view that aggregates transfer volume per day.

#### Key units
- **Tables**
  - `ledger_customer` – customer metadata.
  - `ledger_account` – bank accounts linked to customers.
  - `ledger_transfer` – fund movements between accounts.
  - `ledger_hold` – temporary holds on accounts.
- **View**
  - `ledger_daily_volume` – aggregates `amount_cents` by booking date.
- **Indexes**
  - `idx_transfer_from` (ledger_transfer.from_account)
  - `idx_transfer_booked` (ledger_transfer.booked_at)
  - `idx_hold_account` (ledger_hold.account_id)

#### Data flow
1. Seed `ledger_customer` and `ledger_account`.
2. Insert `ledger_transfer` records referencing accounts.
3. Insert `ledger_hold` records referencing accounts.
4. Query `ledger_daily_volume` for daily transfer totals.

#### Dependencies
- `ledger_daily_volume` reads from `ledger_transfer`.
- Foreign keys link accounts to customers and transfers/holds to accounts.
- Indexes support efficient querying on transfer source, booking time, and hold account.

#### Cross-cutting
- Standard PostgreSQL types; no custom extensions.
- Referential integrity via foreign keys.
- Indexes created immediately after table definitions to support the view and typical queries.
