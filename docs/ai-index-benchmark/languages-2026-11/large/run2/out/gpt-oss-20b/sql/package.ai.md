### sql
- H: 1.0
- C: 479593E6
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:23:25Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A PostgreSQL data‑warehouse snapshot of a payments ledger, storing customers, accounts, and transfer volumes for reporting.

#### Purpose
- Persist payment system data (customers, accounts, transfers).  
- Expose daily transfer volume via a view for analytics.

#### Responsibilities
- **Data persistence** – tables for customers, accounts, and transfers.  
- **Indexing** – fast lookup on transfer source and booking time.  
- **Reporting** – daily volume aggregation view.

#### Key units
- `ledger_customer` – customer master (id, name, country).  
- `ledger_account` – account master (id, customer ref, IBAN, open date).  
- `ledger_transfer` – transaction record (id, from/to accounts, amount, booked_at).  
- `ledger_daily_volume` – view aggregating `amount_cents` per day.  
- Indexes `idx_transfer_from`, `idx_transfer_booked` on `ledger_transfer`.

#### Data flow
1. Inserts populate `ledger_customer`, `ledger_account`, then `ledger_transfer`.  
2. `ledger_daily_volume` reads `ledger_transfer`, groups by `booked_at` date, sums `amount_cents`.  
3. Indexes accelerate queries on transfer source and booking timestamp.

#### Dependencies
- `ledger_transfer` references `ledger_account`; `ledger_account` references `ledger_customer`.  
- View and indexes depend on the three tables.

#### Cross-cutting
- All tables use standard PostgreSQL types; no extensions.  
- Referential integrity enforced via foreign keys.  
- No triggers, procedures, or custom types.
