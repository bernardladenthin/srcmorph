### sql
- H: 1.0
- C: 479593E6
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T01:09:17Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A PostgreSQL payments ledger that stores customers, accounts, and transfer records while providing a daily volume aggregation view.

#### Purpose
- Persist core payment entities: customers, accounts, and transfers.  
- Offer a materialised view summarising daily transfer volume.

#### Responsibilities
- **Data modelling** – tables for customers, accounts, and transfers.  
- **Aggregation** – view `ledger_daily_volume` summarises total transfer amount per day.  
- **Indexing** – indexes on transfer source and booking timestamp to optimise queries.

#### Key units
- `ledger_customer` – customer master (ID, name, country).  
- `ledger_account` – bank account (ID, customer FK, IBAN, open date).  
- `ledger_transfer` – transfer record (ID, from/to accounts, amount, booked time).  
- `ledger_daily_volume` – view computing `day` and `volume_cents` from `ledger_transfer`.  
- Indexes: `idx_transfer_from`, `idx_transfer_booked`.

#### Data flow
1. **Insert** customers → `ledger_customer`.  
2. **Insert** accounts → `ledger_account` (FK to customer).  
3. **Insert** transfers → `ledger_transfer` (FKs to accounts).  
4. **View** `ledger_daily_volume` aggregates `ledger_transfer` by day.

#### Dependencies
- `ledger_transfer` → `ledger_account` → `ledger_customer`.  
- `ledger_daily_volume` depends on `ledger_transfer`.  
- Indexes support efficient look‑ups on transfer source and booking time.

#### Cross-cutting
- Foreign key constraints enforce referential integrity.  
- `amount_cents` uses a `CHECK` constraint to ensure positive values.  
- No triggers, functions, or extensions are used; pure standard PostgreSQL.
