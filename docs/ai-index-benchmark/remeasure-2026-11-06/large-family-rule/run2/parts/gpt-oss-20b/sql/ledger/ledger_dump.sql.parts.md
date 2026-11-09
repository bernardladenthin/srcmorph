# Map-reduce answers of sql/ledger/ledger_dump.sql

## Chunk 1 of 9

> A data‑model for a payments ledger, with customers, accounts and transfers, plus a daily‑volume view.

#### Purpose
- Stores customer, account and transfer data for a payments system.
- Provides a view (`ledger_daily_volume`) that aggregates transfer amounts per day.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`
- **View**: `ledger_daily_volume`
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`
- **Foreign keys**: `ledger_account.customer_id → ledger_customer.customer_id`; `ledger_transfer.from_account → ledger_account.account_id`; `ledger_transfer.to_account → ledger_account.account_id`

#### Schema
- **ledger_customer**
  - `customer_id INTEGER PRIMARY KEY`
  - `display_name VARCHAR(120) NOT NULL`
  - `country_code CHAR(2) NOT NULL`
- **ledger_account**
  - `account_id INTEGER PRIMARY KEY`
  - `customer_id INTEGER NOT NULL REFERENCES ledger_customer(customer_id)`
  - `iban VARCHAR(34) NOT NULL UNIQUE`
  - `opened_on DATE NOT NULL`
- **ledger_transfer**
  - `transfer_id INTEGER PRIMARY KEY`
  - `from_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`
  - `to_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`
  - `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`
  - `booked_at TIMESTAMP NOT NULL`
- **Indexes**
  - `idx_transfer_from (from_account)`
  - `idx_transfer_booked (booked_at)`
- **View**
  - `ledger_daily_volume`  
    `SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

#### Reads
- None (only DDL and data inserts).

#### Writes
- `ledger_customer`: 4000 rows inserted (one bulk `INSERT` statement).
- `ledger_account`: 500 rows inserted (one bulk `INSERT` statement).
- `ledger_transfer`: none in this chunk.

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`
- `ledger_transfer.from_account → ledger_account.account_id`
- `ledger_transfer.to_account → ledger_account.account_id`

#### Dependencies
- View `ledger_daily_volume` depends on table `ledger_transfer`.

## Chunk 2 of 9

> A bulk data load that populates the `ledger_account` table with new customer‑account records.

#### Purpose
* Inserts a large batch of new ledger account records into the database.

#### Type
* Table – `ledger_account` (PostgreSQL)

#### Schema
`ledger_account`  
- `account_id` integer  
- `customer_id` integer  
- `iban` text  
- `opened_on` date  

#### Reads
None – this chunk only performs inserts.

#### Writes
* `ledger_account`: 3 171 rows inserted (values from `(830, …)` to `(4000, …)`).

#### Relationships
None – the table has no foreign keys or other relational constraints shown in this chunk.

#### Dependencies
None – no other tables, views, functions, or sequences are referenced.

## Chunk 3 of 9

> Bulk data load for the ledger domain: inserts account and transfer records.

#### Purpose
- Populate the `ledger_account` and `ledger_transfer` tables with initial data for a financial ledger system.

#### Type
- Data insert script for tables `ledger_account` and `ledger_transfer`.

#### Schema
- **ledger_account**  
  - `account_id` integer  
  - `customer_id` integer  
  - `iban` text  
  - `opened_on` date  

- **ledger_transfer**  
  - `transfer_id` integer  
  - `from_account` integer  
  - `to_account` integer  
  - `amount_cents` integer  
  - `booked_at` timestamp  

#### Reads
- None (pure data insertion).

#### Writes
- `ledger_account`: 5000 rows (IDs 4143 – 5000).  
- `ledger_transfer`: 1500 rows (IDs 1 – 1500).

#### Relationships
- None (no foreign‑key constraints are referenced in this chunk).

#### Dependencies
- None (no other objects referenced).

## Chunk 4 of 9

> Bulk insertion of ledger transfer records, populating the ledger_transfer table with transaction data for accounts.

#### Purpose
- Populate the ledger_transfer table with transaction records.

#### Type
- Data insertion (INSERT INTO) for the table `ledger_transfer`.

#### Schema
- **ledger_transfer**  
  - `transfer_id` (integer) – primary key  
  - `from_account` (integer) – source account ID  
  - `to_account` (integer) – destination account ID  
  - `amount_cents` (integer) – transaction amount in cents  
  - `booked_at` (timestamp) – timestamp of the transaction

#### Reads
- None (this chunk only contains INSERT statements).

#### Writes
- `ledger_transfer`: 3,472 rows inserted (transfer_id 1529 through 5000).

#### Relationships
- `ledger_transfer.from_account` → `accounts.id` (foreign key, implied)  
- `ledger_transfer.to_account` → `accounts.id` (foreign key, implied)

#### Routine logic
- None (pure data insertion).

#### Dependencies
- None (no other tables or functions referenced in this chunk).

## Chunk 5 of 9

> **Bulk insertion of ledger transfer and hold records for the 2026 fiscal year**

#### Purpose
- Populate the `ledger_transfer` table with 3 400 transaction records (IDs 5001‑8400) and the `ledger_hold` table with 50 hold entries (IDs 1‑50).  
- These inserts seed the ledger with historical transfer data and account holds for subsequent reporting and reconciliation.

#### Type
- **Tables**: `ledger_transfer`, `ledger_hold`  
- **Index**: `idx_hold_account` on `ledger_hold(account_id)`  
- **Dialect**: PostgreSQL (standard SQL)

#### Schema
- **ledger_transfer**  
  - `transfer_id` INTEGER PRIMARY KEY  
  - `from_account` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`  
  - `to_account` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`  
  - `amount_cents` INTEGER NOT NULL  
  - `booked_at` TIMESTAMP NOT NULL  

- **ledger_hold**  
  - `hold_id` INTEGER PRIMARY KEY  
  - `account_id` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`  
  - `hold_reason` VARCHAR(80) NOT NULL  

- **Index**  
  - `idx_hold_account` on `ledger_hold(account_id)`

#### Reads
- None (this chunk contains only `INSERT` statements).

#### Writes
- `ledger_transfer`: 3 400 rows (IDs 5001‑8400).  
- `ledger_hold`: 50 rows (IDs 1‑50) – inserted earlier in the file.

#### Relationships
- `ledger_transfer.from_account` → `ledger_account.account_id`  
- `ledger_transfer.to_account` → `ledger_account.account_id`  
- `ledger_hold.account_id` → `ledger_account.account_id`

#### Routine logic
- N/A (only data insertion).

#### Dependencies
- Requires the `ledger_account` table to exist for foreign‑key references.

## Chunk 6 of 9

> Inserting a large batch of transfer records into the ledger_transfer table.

#### Purpose
- Populate the ledger_transfer table with a bulk set of transfer transactions.

#### Type
- Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Writes
- `ledger_transfer`: 3313 rows inserted (transfer_id 8501 – 11813).

## Chunk 7 of 9

> Inserts a large batch of ledger transfer records into the ledger_transfer table.

#### Type
Table

#### Schema
- `transfer_id` integer
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Writes
ledger_transfer: 3222 rows inserted.

## Chunk 8 of 9

> Bulk data load of ledger transfer records for the 2026 fiscal year.

#### Purpose
- Populate the `ledger_transfer` table with a large set of transfer records for the year 2026.

#### Type
- SQL `INSERT` statement into the `ledger_transfer` table (PostgreSQL dialect).

#### Schema
- `ledger_transfer`  
  - `transfer_id` integer PK  
  - `from_account` integer NOT NULL  
  - `to_account` integer NOT NULL  
  - `amount_cents` integer NOT NULL  
  - `booked_at` timestamp NOT NULL  

#### Reads
- None (data insertion only).

#### Writes
- `ledger_transfer`: 3 406 rows inserted (transfer_id 15155 → 18560).

#### Relationships
- None (no foreign keys referenced in this chunk).

#### Routine logic
- None (pure data insertion).

#### Dependencies
- None (no other tables, functions, or sequences referenced).

## Chunk 9 of 9

> A bulk load of transaction records into the ledger_transfer table.

#### Purpose
- Populate the ledger_transfer table with historical transfer data.

#### Type
- Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Reads
- None.

#### Writes
- `ledger_transfer`: 1469 rows inserted (transfer_id 18532 – 20000).

#### Relationships
- None.

#### Dependencies
- None.

## Reduce round 1, batch 1 of 1 (9 parts)

> A SQL script that creates a payments ledger schema and bulk‑loads millions of customer, account, transfer and hold records, then exposes a daily‑volume view for reporting.

#### Purpose
- Define tables for customers, accounts, transfers and holds in a payments ledger.
- Populate these tables with bulk INSERTs (≈30 k rows).
- Provide a view that aggregates transfer volume per day.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`
- **View**: `ledger_daily_volume`
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`
- **Foreign keys**:  
  - `ledger_account.customer_id → ledger_customer.customer_id`  
  - `ledger_transfer.from_account → ledger_account.account_id`  
  - `ledger_transfer.to_account → ledger_account.account_id`  
  - `ledger_hold.account_id → ledger_account.account_id`

#### Schema
- **ledger_customer**  
  - `customer_id INTEGER PRIMARY KEY`  
  - `display_name VARCHAR(120) NOT NULL`  
  - `country_code CHAR(2) NOT NULL`
- **ledger_account**  
  - `account_id INTEGER PRIMARY KEY`  
  - `customer_id INTEGER NOT NULL REFERENCES ledger_customer(customer_id)`  
  - `iban VARCHAR(34) NOT NULL UNIQUE`  
  - `opened_on DATE NOT NULL`
- **ledger_transfer**  
  - `transfer_id INTEGER PRIMARY KEY`  
  - `from_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
  - `to_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
  - `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`  
  - `booked_at TIMESTAMP NOT NULL`
- **ledger_hold**  
  - `hold_id INTEGER PRIMARY KEY`  
  - `account_id INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
  - `hold_reason VARCHAR(80) NOT NULL`
- **Indexes**  
  - `idx_transfer_from (from_account)`  
  - `idx_transfer_booked (booked_at)`  
  - `idx_hold_account (account_id)`
- **View** `ledger_daily_volume`  
  ```sql
  SELECT CAST(booked_at AS DATE) AS day,
         SUM(amount_cents) AS volume_cents
  FROM ledger_transfer
  GROUP BY CAST(booked_at AS DATE);
  ```

#### Reads
- None (only DDL and data inserts).

#### Writes
- `ledger_customer`: 4 000 rows (single bulk INSERT).  
- `ledger_account`: 5 000 rows (single bulk INSERT).  
- `ledger_transfer`: 30 050 rows (61 INSERT statements, 30050 VALUES rows).  
- `ledger_hold`: 50 rows (single bulk INSERT).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`  
- `ledger_transfer.from_account → ledger_account.account_id`  
- `ledger_transfer.to_account → ledger_account.account_id`  
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- View `ledger_daily_volume` depends on table `ledger_transfer`.  
- All foreign‑key constraints reference `ledger_customer` and `ledger_account`.

