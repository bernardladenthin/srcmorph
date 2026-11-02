### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T01:03:42Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

> A PostgreSQL data dump that defines a simple payments ledger: customers, their accounts, and inter‑account transfers, plus a daily volume view.

#### Purpose
- Stores customer information, bank accounts, and transfer records for a payments system.  
- Provides a materialised view (`ledger_daily_volume`) summarising total transfer volume per day.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`  
- **View**: `ledger_daily_volume`  
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`  
- **Dialect**: PostgreSQL (standard SQL, no extensions used)

#### Schema
**ledger_customer**  
- `customer_id INTEGER PRIMARY KEY`  
- `display_name VARCHAR(120) NOT NULL`  
- `country_code CHAR(2) NOT NULL`  

**ledger_account**  
- `account_id INTEGER PRIMARY KEY`  
- `customer_id INTEGER NOT NULL REFERENCES ledger_customer(customer_id)`  
- `iban VARCHAR(34) NOT NULL UNIQUE`  
- `opened_on DATE NOT NULL`  

**ledger_transfer**  
- `transfer_id INTEGER PRIMARY KEY`  
- `from_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
- `to_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
- `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`  
- `booked_at TIMESTAMP NOT NULL`  

**ledger_daily_volume** (view)  
- `day DATE` (derived from `booked_at`)  
- `volume_cents BIGINT` (sum of `amount_cents` per day)  

**Indexes**  
- `idx_transfer_from ON ledger_transfer(from_account)`  
- `idx_transfer_booked ON ledger_transfer(booked_at)`  

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- `ledger_customer`: 4000 rows inserted (IDs 1‑4000).  
- `ledger_account`: 400+ rows inserted (IDs 1‑400+).  
- `ledger_transfer`: no INSERT statements present (0 rows).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id` (FK).  
- `ledger_transfer.from_account → ledger_account.account_id` (FK).  
- `ledger_transfer.to_account → ledger_account.account_id` (FK).

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.  
- `ledger_transfer` depends on `ledger_account`.  
- `ledger_account` depends on `ledger_customer`.
