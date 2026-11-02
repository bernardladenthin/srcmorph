### sql/ledger
- H: 1.0
- C: 0AA8FF04
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:32:58Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL ledger system that stores customers, their bank accounts, and inter‑account transfers, plus a pre‑aggregated daily volume view.

#### Purpose
- Persist customer identities and country codes.  
- Store bank accounts linked to customers, with IBANs and opening dates.  
- Record transfers between accounts, including amount and booking timestamp.  
- Provide a daily transfer volume aggregation.

#### Responsibilities
- **Customer Management** – maintain `ledger_customer` table.  
- **Account Management** – maintain `ledger_account` table linked to customers.  
- **Transfer Recording** – maintain `ledger_transfer` table with foreign‑key integrity.  
- **Analytics** – expose `ledger_daily_volume` view for daily transfer totals.

#### Key units
- `ledger_customer` – primary key `customer_id`, fields `display_name`, `country_code`.  
- `ledger_account` – primary key `account_id`, foreign key `customer_id`, unique `iban`, `opened_on`.  
- `ledger_transfer` – primary key `transfer_id`, foreign keys `from_account`, `to_account`, `amount_cents`, `booked_at`.  
- `ledger_daily_volume` – view aggregating `amount_cents` per day from `ledger_transfer`.  
- Indexes: `idx_transfer_from` on `from_account`; `idx_transfer_booked` on `booked_at`.

#### Data flow
1. **Insert** customers → `ledger_customer`.  
2. **Insert** accounts referencing customers → `ledger_account`.  
3. **Insert** transfers referencing accounts → `ledger_transfer`.  
4. **Query** `ledger_daily_volume` aggregates transfers by day for reporting.

#### Dependencies
- `ledger_account` → `ledger_customer` (many‑to‑one).  
- `ledger_transfer` → `ledger_account` (from/to many‑to‑one).  
- `ledger_daily_volume` depends on `ledger_transfer`.

#### Cross-cutting
- Foreign‑key constraints enforce referential integrity across tables.  
- Indexes on `from_account` and `booked_at` support transfer lookups and daily aggregation.  
- `amount_cents` uses a positive check constraint to ensure non‑negative amounts.
