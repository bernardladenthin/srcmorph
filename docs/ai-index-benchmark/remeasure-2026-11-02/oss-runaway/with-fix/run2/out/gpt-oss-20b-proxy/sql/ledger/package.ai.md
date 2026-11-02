### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:34:49Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL ledger that stores customers, accounts, transfers, and fraud holds, and exposes a daily volume view.

#### Purpose
- Persist payment‑ledger data: customers, accounts, transfers, and temporary fraud holds.  
- Provide a view of daily transfer volume.

#### Responsibilities
- **Data persistence** – tables for customers, accounts, transfers, and holds.  
- **Analytics** – a view that aggregates daily transfer volume.  
- **Indexing** – indexes to accelerate transfer lookups by source, booking date, and hold lookups by account.

#### Key units
- `ledger_customer` – stores customer identity, name, and country.  
- `ledger_account` – links a customer to an IBAN and opening date.  
- `ledger_transfer` – records a transfer between two accounts, amount, and booking timestamp.  
- `ledger_hold` – temporary fraud hold on an account with a reason.  
- `ledger_daily_volume` – view that sums `amount_cents` per day from `ledger_transfer`.  
- Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Insert** customers → `ledger_customer`.  
2. **Insert** accounts referencing customers → `ledger_account`.  
3. **Insert** transfers referencing accounts → `ledger_transfer`.  
4. **Insert** holds referencing accounts → `ledger_hold`.  
5. **Query** `ledger_daily_volume` to obtain daily transfer totals.

#### Dependencies
- Internal foreign‑key relationships:  
  - `ledger_account.customer_id → ledger_customer.customer_id`  
  - `ledger_transfer.from_account → ledger_account.account_id`  
  - `ledger_transfer.to_account → ledger_account.account_id`  
  - `ledger_hold.account_id → ledger_account.account_id`  
- No external tables, functions, or sequences are referenced.

#### Cross-cutting
- All tables use integer primary keys.  
- `ledger_transfer.amount_cents` is constrained to be positive.  
- Indexes target common query patterns: source account lookups, date‑range queries, and hold lookups.
