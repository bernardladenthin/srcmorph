### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:01:57Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: B48CB49F
- N: CA5BDE78
- M: 2FA139DC
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A payments ledger that stores customers, accounts, transfers and fraud‑hold records, and exposes a daily volume summary view.

#### Purpose
- Persist customer, account, transfer and hold data for a payments system.
- Provide a `ledger_daily_volume` view summarising total transfer volume per day.

#### Type
- PostgreSQL DDL: tables, view, indexes, bulk INSERTs.  
- No functions, procedures, triggers, sequences, extensions or types.

#### Schema
**Table `ledger_customer`**  
- `customer_id` INTEGER PK  
- `display_name` VARCHAR(120) NOT NULL  
- `country_code` CHAR(2) NOT NULL  

**Table `ledger_account`**  
- `account_id` INTEGER PK  
- `customer_id` INTEGER NOT NULL REFERENCES `ledger_customer(customer_id)`  
- `iban` VARCHAR(34) NOT NULL UNIQUE  
- `opened_on` DATE NOT NULL  

**Table `ledger_transfer`**  
- `transfer_id` INTEGER PK  
- `from_account` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`  
- `to_account` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`  
- `amount_cents` BIGINT NOT NULL CHECK (amount_cents > 0)  
- `booked_at` TIMESTAMP NOT NULL  

**Table `ledger_hold`**  
- `hold_id` INTEGER PK  
- `account_id` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`  
- `hold_reason` VARCHAR(80) NOT NULL  

**View `ledger_daily_volume`**  
- `day` DATE (derived from `booked_at`)  
- `volume_cents` BIGINT (sum of `amount_cents`)  

**Indexes**  
- `idx_transfer_from` ON `ledger_transfer(from_account)`  
- `idx_transfer_booked` ON `ledger_transfer(booked_at)`  
- `idx_hold_account` ON `ledger_hold(account_id)`  

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- `ledger_customer`: ~400 rows (INSERTs in Part 1).  
- `ledger_account`: 4,020 rows (INSERTs in Parts 2 & 3).  
- `ledger_transfer`: 18,310 rows (INSERTs across Parts 3–10).  
- `ledger_hold`: 50 rows (INSERTs in Part 5).  
- Total INSERT statements: 61; approximate total rows inserted: 30,050.

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`  
- `ledger_transfer.from_account → ledger_account.account_id`  
- `ledger_transfer.to_account → ledger_account.account_id`  
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold` referenced by INSERTs and FK constraints.
