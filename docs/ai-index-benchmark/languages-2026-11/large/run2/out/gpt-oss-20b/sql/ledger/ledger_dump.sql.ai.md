### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:18:24Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

> A data‑warehouse snapshot of a payments ledger, including customers, accounts, and transfer volumes.

#### Purpose
- Stores customer, account, and transfer data for a payments system.  
- Provides a daily volume view for reporting.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`  
- **View**: `ledger_daily_volume`  
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`  
- **Dialect**: PostgreSQL (standard SQL, no extensions)

#### Schema
- **ledger_customer**  
  - `customer_id` INTEGER PRIMARY KEY  
  - `display_name` VARCHAR(120) NOT NULL  
  - `country_code` CHAR(2) NOT NULL  

- **ledger_account**  
  - `account_id` INTEGER PRIMARY KEY  
  - `customer_id` INTEGER NOT NULL REFERENCES ledger_customer(customer_id)  
  - `iban` VARCHAR(34) NOT NULL UNIQUE  
  - `opened_on` DATE NOT NULL  

- **ledger_transfer**  
  - `transfer_id` INTEGER PRIMARY KEY  
  - `from_account` INTEGER NOT NULL REFERENCES ledger_account(account_id)  
  - `to_account` INTEGER NOT NULL REFERENCES ledger_account(account_id)  
  - `amount_cents` BIGINT NOT NULL CHECK (amount_cents > 0)  
  - `booked_at` TIMESTAMP NOT NULL  

- **Indexes**  
  - `idx_transfer_from` ON ledger_transfer(from_account)  
  - `idx_transfer_booked` ON ledger_transfer(booked_at)  

- **View**  
  - `ledger_daily_volume`  
    ```sql
    SELECT CAST(booked_at AS DATE) AS day,
           SUM(amount_cents) AS volume_cents
    FROM ledger_transfer
    GROUP BY CAST(booked_at AS DATE);
    ```

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- `INSERT INTO ledger_customer` – 4000 rows (IDs 1‑4000).  
- `INSERT INTO ledger_account` – 900 rows (IDs 1‑900).  
- `INSERT INTO ledger_transfer` – 1700 rows (IDs 1‑1700) in Part 2, 5000 rows (IDs 1701‑5000) in Part 3, 3367 rows (IDs 12250‑15616) in Part 5, 20000 rows in Part 6.  
- Total `ledger_transfer` inserts: 1700 + 5000 + 3367 + 20000 = **29 067** rows.

#### Relationships
- `ledger_account.customer_id` → `ledger_customer.customer_id`  
- `ledger_transfer.from_account` → `ledger_account.account_id`  
- `ledger_transfer.to_account` → `ledger_account.account_id`

#### Dependencies
- `ledger_customer`, `ledger_account`, `ledger_transfer` referenced by the view and indexes.
