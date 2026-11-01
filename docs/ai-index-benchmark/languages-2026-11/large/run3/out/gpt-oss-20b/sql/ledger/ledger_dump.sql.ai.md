### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:27:01Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

> A PostgreSQL ledger system that stores customers, their bank accounts, and inter‑account transfers, plus a pre‑aggregated daily volume view.

#### Purpose
- Persist customer identities and country codes.  
- Store bank accounts linked to customers, with IBANs and opening dates.  
- Record transfers between accounts, including amount and booking timestamp.  
- Provide a daily transfer volume aggregation.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`  
- **View**: `ledger_daily_volume`  
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`  
- **Dialect**: PostgreSQL

#### Schema
##### ledger_customer
- `customer_id` INTEGER PRIMARY KEY  
- `display_name` VARCHAR(120) NOT NULL  
- `country_code` CHAR(2) NOT NULL  

##### ledger_account
- `account_id` INTEGER PRIMARY KEY  
- `customer_id` INTEGER NOT NULL REFERENCES ledger_customer(customer_id)  
- `iban` VARCHAR(34) NOT NULL UNIQUE  
- `opened_on` DATE NOT NULL  

##### ledger_transfer
- `transfer_id` INTEGER PRIMARY KEY  
- `from_account` INTEGER NOT NULL REFERENCES ledger_account(account_id)  
- `to_account` INTEGER NOT NULL REFERENCES ledger_account(account_id)  
- `amount_cents` BIGINT NOT NULL CHECK (amount_cents > 0)  
- `booked_at` TIMESTAMP NOT NULL  

##### ledger_daily_volume (view)
- `day` DATE ← `CAST(booked_at AS DATE)`  
- `volume_cents` BIGINT ← `SUM(amount_cents)`  
- **Definition**: `SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

##### Indexes
- `idx_transfer_from` ON `ledger_transfer(from_account)`  
- `idx_transfer_booked` ON `ledger_transfer(booked_at)`

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.  
- Application queries read from `ledger_customer`, `ledger_account`, `ledger_transfer`.

#### Writes
- `ledger_customer`: 1 000 rows inserted (IDs 1‑1000).  
- `ledger_account`: 1 739 rows inserted (IDs 4 501‑6 280).  
- `ledger_transfer`:  
  - 1 739 rows inserted (IDs 1‑1 739).  
  - 5 000 rows inserted (IDs 1‑5 000).  
  - 3 308 rows inserted (IDs 8 693‑12 000).  
  - 15 600 rows inserted (IDs 1‑15 600).  
  - 984 rows inserted (IDs 19 017‑20 000).  
  **Total**: 1 739 + 5 000 + 3 308 + 15 600 + 984 = **26 631** rows.

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id` (many‑to‑one).  
- `ledger_transfer.from_account → ledger_account.account_id` (many‑to‑one).  
- `ledger_transfer.to_account → ledger_account.account_id` (many‑to‑one).

#### Routine logic
- **View `ledger_daily_volume`**: aggregates `amount_cents` per calendar day from `ledger_transfer`.

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.  
- `ledger_account` depends on `ledger_customer`.  
- `ledger_transfer` depends on `ledger_account`.
