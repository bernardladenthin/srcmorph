# Map-reduce answers of sql/ledger/ledger_dump.sql

## Chunk 1 of 9

> A data‑dump script that creates a payments ledger schema, populates customer and account tables, and defines a daily volume view.

#### Purpose
- Provides a sample dataset for a payments ledger.
- Demonstrates table creation, foreign‑key relationships, indexing, and a materialised view.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`.
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`.

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
  - `idx_transfer_from ON ledger_transfer(from_account)`
  - `idx_transfer_booked ON ledger_transfer(booked_at)`
- **View**
  - `ledger_daily_volume` aggregates `amount_cents` per day from `ledger_transfer`.

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- `ledger_customer`: 2000 rows (20 INSERT statements × 100 rows each).
- `ledger_account`: 400 rows (single INSERT statement).
- `ledger_transfer`: 0 rows (no INSERTs in this chunk).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`
- `ledger_transfer.from_account → ledger_account.account_id`
- `ledger_transfer.to_account → ledger_account.account_id`

#### Dependencies
- View `ledger_daily_volume` depends on `ledger_transfer`.

## Chunk 2 of 9

> **Bulk insert of new customer accounts into the ledger.**  

#### Purpose
* Adds a large batch of new customer accounts to the `ledger_account` table, each with a unique account ID, customer reference, IBAN, and opening date.

#### Type
* Table: `ledger_account`  
  * PostgreSQL (assumed from syntax).  
  * No extensions or custom types referenced in this chunk.

#### Schema
| Column | Type | Constraints |
|--------|------|-------------|
| `account_id` | integer | PRIMARY KEY |
| `customer_id` | integer | NOT NULL |
| `iban` | text | NOT NULL |
| `opened_on` | date | NOT NULL |

#### Reads
* None – this chunk only performs inserts.

#### Writes
* Inserts 3,171 rows into `ledger_account` (IDs 830 through 4000).  

#### Relationships
* None explicitly declared in this chunk.  

#### Routine logic
* N/A – this is a data‑load statement, not a stored routine.  

#### Dependencies
* None – only the `ledger_account` table is affected.

## Chunk 3 of 9, half 1

> A bulk load of customer‑account records into the ledger_account table.

#### Purpose
- Populate the ledger_account table with account identifiers, customer references, IBANs, and opening dates.

#### Type
- Table: `ledger_account` (PostgreSQL)

#### Schema
- `account_id` integer NOT NULL
- `customer_id` integer NOT NULL
- `iban` varchar(34) NOT NULL
- `opened_on` date NOT NULL

#### Writes
- **ledger_account**: 858 rows (INSERT 1) + 806 rows (INSERT 2) = **1 664 rows** inserted.

#### Relationships
- None declared in this chunk.

#### Dependencies
- None declared in this chunk.

## Chunk 3 of 9, half 2

> A bulk load of ledger transfer records, inserting thousands of transaction rows into the `ledger_transfer` table.

#### Purpose
- Populate the `ledger_transfer` table with historical transfer data.

#### Type
- Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer PK
- `from_account` integer NOT NULL
- `to_account` integer NOT NULL
- `amount_cents` integer NOT NULL
- `booked_at` timestamp NOT NULL

#### Reads
- None (only INSERT statements)

#### Writes
- `ledger_transfer`: 185 rows (IDs 5816‑6000) + 1 563 rows (IDs 1‑1563) = **1 748 rows** inserted in this chunk.

#### Relationships
- None (no foreign keys referenced in this chunk).

## Chunk 4 of 9

> Bulk insertion of ledger transfer records for the period 2026‑01‑01 to 2026‑10‑28.

#### Purpose
Populate the `ledger_transfer` table with 3 472 transaction records, each representing a transfer between two accounts with an amount and timestamp.

#### Type
Table: `ledger_transfer` (PostgreSQL).  
Columns: `transfer_id` (integer, PK), `from_account` (integer), `to_account` (integer), `amount_cents` (integer), `booked_at` (timestamp).

#### Writes
- `ledger_transfer`: 3 472 rows inserted (transfer_id 1529 → 5000).  
  *No other tables are affected.*

#### Relationships
- `from_account` → `account.id` (foreign key, if defined).  
- `to_account` → `account.id` (foreign key, if defined).

## Chunk 5 of 9

> Inserting a large batch of ledger transfer and hold records into the accounting tables.

#### Purpose
- Populate the `ledger_transfer` and `ledger_hold` tables with new transaction and hold data.

#### Type
- Table definitions and data inserts for `ledger_transfer` and `ledger_hold` (PostgreSQL).

#### Schema
**ledger_hold**
- `hold_id` INTEGER PRIMARY KEY
- `account_id` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`
- `hold_reason` VARCHAR(80) NOT NULL
- Index: `idx_hold_account` on `account_id`

**ledger_transfer**
- `transfer_id` INTEGER PRIMARY KEY
- `from_account` INTEGER
- `to_account` INTEGER
- `amount_cents` INTEGER
- `booked_at` TIMESTAMP

#### Writes
- `ledger_transfer`: 3 433 rows (IDs 4968 – 8400)
- `ledger_hold`: 50 rows (IDs 1 – 50)

## Chunk 6 of 9

> Inserts 3,427 rows into the ledger_transfer table, representing a bulk load of transfer records.

#### Purpose
- Bulk load of transfer transaction data into the ledger_transfer table.

#### Type
- Table: `ledger_transfer` (PostgreSQL).

#### Writes
- `ledger_transfer`: 3,427 rows inserted (transfer_id 8374 – 11800).

## Chunk 7 of 9

> A bulk load of ledger transfer records, inserting thousands of rows into the `ledger_transfer` table.

#### Purpose
- Populate the `ledger_transfer` table with historical transfer data.

#### Type
- `INSERT` statement (data population).

#### Schema
- **Table:** `ledger_transfer`
  - `transfer_id` integer PK
  - `from_account` integer
  - `to_account` integer
  - `amount_cents` integer
  - `booked_at` timestamp

#### Reads
- None (pure data insertion).

#### Writes
- `ledger_transfer`: 2922 rows inserted  
  - Family 1: 222 rows (11779‑12000)  
  - Families 2‑11: 300 rows each (12001‑15000)

#### Relationships
- None (no foreign keys referenced in this chunk).

#### Dependencies
- None (only inserts into `ledger_transfer`).

## Chunk 8 of 9

> This chunk inserts a large batch of transaction records into the `ledger_transfer` table, adding 3,546 rows across several INSERT statements.

#### Writes
- **ledger_transfer**: 3,546 rows inserted  
  - First INSERT: 46 rows (IDs 15155–15200)  
  - Subsequent 7 INSERTs: 500 rows each (IDs 15501–16000, 16001–16500, 16501–17000, 17001–17500, 17501–18000, 18001–18500)

## Chunk 9 of 9

> Bulk insertion of 1,469 ledger transfer records into the `ledger_transfer` table.

#### Type
- **SQL object**: `INSERT` statement
- **Dialect**: PostgreSQL (standard SQL syntax)

#### Writes
- **Table**: `ledger_transfer`
- **Rows inserted**: 1,469 (transfer_id 18532 – 20000)

#### Dependencies
- None (no SELECTs, no other tables referenced).

## Reduce round 1, batch 1 of 1 (10 parts)

> A SQL dump that creates a payments ledger schema, populates customers, accounts, transfers and holds, and defines a daily volume view.

#### Purpose
- Provides a sample dataset for a payments ledger.
- Demonstrates table creation, foreign‑key relationships, indexing, and a materialised view.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`
- **View**: `ledger_daily_volume`
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`
- **Dialect**: PostgreSQL

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
  - `idx_transfer_from ON ledger_transfer(from_account)`
  - `idx_transfer_booked ON ledger_transfer(booked_at)`
  - `idx_hold_account ON ledger_hold(account_id)`
- **View**
  - `ledger_daily_volume` aggregates `amount_cents` per day from `ledger_transfer`.

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- `ledger_customer`: 2000 rows (20 INSERTs × 100 rows each).
- `ledger_account`: about 5 235 rows (400 + 3 171 + 1 664 inserts across parts).
- `ledger_transfer`: about 30 000 rows (61 INSERTs, 30 050 rows total).
- `ledger_hold`: 50 rows (single bulk insert).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`
- `ledger_transfer.from_account → ledger_account.account_id`
- `ledger_transfer.to_account → ledger_account.account_id`
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- View `ledger_daily_volume` depends on `ledger_transfer`.

