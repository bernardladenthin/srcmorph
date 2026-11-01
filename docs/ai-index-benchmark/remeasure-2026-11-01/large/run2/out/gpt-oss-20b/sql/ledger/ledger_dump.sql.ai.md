### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:52:51Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A payments ledger schema with customers, accounts, transfers, and a daily volume view, plus bulk data loads for accounts and transfers.

#### Purpose
- Define core tables for a payments ledger: customers, accounts, transfers.
- Provide a view summarising daily transfer volume.
- Include bulk INSERTs for accounts and transfers.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold` (in later parts).
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.
- **Foreign Keys**: `ledger_account.customer_id → ledger_customer.customer_id`; `ledger_transfer.from_account → ledger_account.account_id`; `ledger_transfer.to_account → ledger_account.account_id`; `ledger_hold.account_id → ledger_account.account_id`.

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

**ledger_hold** (from Part 5)  
- `hold_id INTEGER PRIMARY KEY`  
- `account_id INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
- `hold_reason VARCHAR(80) NOT NULL`

**Indexes**  
- `idx_transfer_from ON ledger_transfer(from_account)`  
- `idx_transfer_booked ON ledger_transfer(booked_at)`  
- `idx_hold_account ON ledger_hold(account_id)`

**View**  
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
- `ledger_customer`: 4000 rows (IDs 1‑4000).  
- `ledger_account`: 900 rows (IDs 1‑900) + 4000+ rows (bulk inserts, total > 4900).  
- `ledger_transfer`: 0 rows in schema definition; bulk inserts across parts 4‑9 total ≈ 20 001 rows.  
- `ledger_hold`: 50 rows (bulk insert in Part 5).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`  
- `ledger_transfer.from_account → ledger_account.account_id`  
- `ledger_transfer.to_account → ledger_account.account_id`  
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- View `ledger_daily_volume` depends on `ledger_transfer`.  
- Indexes depend on their respective tables.  
- Foreign keys reference `ledger_customer` and `ledger_account`.  
- Bulk inserts reference only the target tables; no external functions or extensions.
