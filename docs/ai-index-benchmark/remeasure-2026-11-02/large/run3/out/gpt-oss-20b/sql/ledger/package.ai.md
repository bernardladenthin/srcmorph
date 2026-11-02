### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:10:52Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL data‑dump that defines a payments ledger schema, seeds it with realistic test data, and exposes a daily transfer volume view.

#### Purpose
- Create a fully populated payments ledger database for testing and demos.
- Provide a ready‑to‑use view summarising daily transfer volume.

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
1. **Insert** seed data into `ledger_customer` and `ledger_account`.
2. **Insert** transfer records into `ledger_transfer`, referencing accounts.
3. **Insert** hold records into `ledger_hold`, referencing accounts.
4. **Query** `ledger_daily_volume` to obtain daily transfer totals.

#### Dependencies
- `ledger_daily_volume` reads from `ledger_transfer`.
- Foreign key constraints link accounts to customers and transfers/holds to accounts.
- Indexes support efficient querying on transfer source, booking time, and hold account.

#### Cross-cutting
- All tables use standard PostgreSQL types; no custom extensions.
- Referential integrity enforced via foreign keys.
- Indexes are created immediately after table definitions to support the view and typical queries.
