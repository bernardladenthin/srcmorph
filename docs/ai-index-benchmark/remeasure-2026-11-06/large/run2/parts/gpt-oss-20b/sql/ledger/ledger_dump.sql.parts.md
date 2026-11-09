# Map-reduce answers of sql/ledger/ledger_dump.sql

## Chunk 1 of 9

> A data‑dump of a payments ledger, defining customers, accounts and transfers, and populating the customer and account tables.

#### Purpose
- Create the core tables for a payments ledger: customers, accounts, transfers.
- Define a daily volume view over transfers.
- Seed the database with a large set of customer and account records.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`.
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`.
- **Dialect**: PostgreSQL (standard SQL, no extensions).

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
  - `idx_transfer_from` on `ledger_transfer(from_account)`
  - `idx_transfer_booked` on `ledger_transfer(booked_at)`

- **View**
  - `ledger_daily_volume`  
    `SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

#### Reads
- None (no SELECTs or joins in this chunk).

#### Writes
- `ledger_customer`: 4000 rows inserted (single bulk `INSERT` statement).
- `ledger_account`: 500 rows inserted (single bulk `INSERT` statement).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id` (FK).
- `ledger_transfer.from_account → ledger_account.account_id` (FK).
- `ledger_transfer.to_account → ledger_account.account_id` (FK).

#### Routine logic
- None (only DDL and data inserts).

#### Dependencies
- None beyond the tables and view defined in this file.

## Chunk 2 of 9

> Inserts a large batch of rows into the `ledger_account` table, populating account data for the ledger.

#### Purpose
- Populate the `ledger_account` table with account records (account ID, customer ID, IBAN, opened date).

#### Type
- Table: `ledger_account` (PostgreSQL)

#### Schema
- `account_id` integer NOT NULL PRIMARY KEY
- `customer_id` integer NOT NULL
- `iban` text NOT NULL
- `opened_on` date NOT NULL

#### Reads
- None (only INSERT statements)

#### Writes
- `ledger_account`: 3,168 rows inserted (rows 833 – 4000)

#### Relationships
- None (no foreign keys referenced in this chunk)

## Chunk 3 of 9

> A bulk data load for the core ledger tables, inserting thousands of account records and a full year’s worth of transfer transactions.

#### Purpose
- Populate the `ledger_account` and `ledger_transfer` tables with historical data for testing, analytics, or migration.

#### Type
- Data file (INSERT statements) for two tables: `ledger_account` and `ledger_transfer`.

#### Schema
**ledger_account**
- `account_id` integer PK
- `customer_id` integer
- `iban` text
- `opened_on` date

**ledger_transfer**
- `transfer_id` integer PK
- `from_account` integer FK → `ledger_account.account_id`
- `to_account` integer FK → `ledger_account.account_id`
- `amount_cents` integer
- `booked_at` timestamp

#### Reads
None – the file only contains INSERT statements.

#### Writes
- `ledger_account`: 1 352 rows inserted (IDs 4149‑4500, 5001‑5500, 5501‑6000).  
- `ledger_transfer`: 1 000 rows inserted (IDs 1‑1000).

#### Relationships
- `ledger_transfer.from_account` → `ledger_account.account_id`  
- `ledger_transfer.to_account` → `ledger_account.account_id`

#### Dependencies
- `ledger_transfer` depends on `ledger_account` for the foreign‑key references.

## Chunk 4 of 9

> This chunk inserts a large batch of ledger transfer records into the `ledger_transfer` table, populating transaction history for the system.

#### Type
- Table: `ledger_transfer`

#### Schema
- `transfer_id` integer PK
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Writes
- Inserts 463 rows (transfer_id 1538 – 2000) into `ledger_transfer`.

## Chunk 5 of 9

> A bulk data load that populates the ledger transfer history and places fraud‑review holds on accounts.

#### Purpose
- Seed the ledger system with historical transfer records and account hold flags for fraud review.

#### Type
- **Table**: `ledger_hold` (PostgreSQL).  
- **Data**: `INSERT` statements for `ledger_transfer` (PostgreSQL).

#### Schema
**ledger_hold**  
| Column | Type | Constraints |
|--------|------|-------------|
| hold_id | INTEGER | PRIMARY KEY |
| account_id | INTEGER | NOT NULL, REFERENCES `ledger_account(account_id)` |
| hold_reason | VARCHAR(80) | NOT NULL |

**Indexes**  
- `idx_hold_account` on `ledger_hold(account_id)`.

#### Reads
None – this chunk only inserts data.

#### Writes
- **ledger_hold**: 50 rows inserted (hold_id 1‑50).  
- **ledger_transfer**: 5 000+ rows inserted (transfer_id 4981‑8000).

#### Relationships
- `ledger_hold.account_id` → `ledger_account.account_id` (foreign key).

#### Dependencies
- `ledger_account` (referenced by `ledger_hold`).

## Chunk 6 of 9

> Inserts transaction records into the ledger_transfer table.

#### Purpose
- Records transfer events between accounts with amount and timestamp.

#### Type
- Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Writes
- `ledger_transfer`: 3441 rows inserted  
  * 111 rows (transfer_id 8390‑8500)  
  * 3300 rows (transfer_id 8501‑11800)  
  * 30 rows (transfer_id 11801‑11830)

#### Relationships
- None (no foreign keys referenced in this chunk).

## Chunk 7 of 9

> Inserting a large batch of ledger transfer records into the `ledger_transfer` table.

#### Type
INSERT statement

#### Writes
- `ledger_transfer`: 3413 rows inserted (transfer_id 11798 → 15210).

## Chunk 8 of 9

> The file inserts a large batch of transfer records into the `ledger_transfer` table, adding new transaction data for the ledger system.

#### Type
- **Table**: `ledger_transfer`

#### Schema
- `transfer_id` (integer, PK)
- `from_account` (integer)
- `to_account` (integer)
- `amount_cents` (integer)
- `booked_at` (timestamp)

#### Writes
- `ledger_transfer`: 3 414 rows inserted (transfer IDs 15177 → 18590).

## Chunk 9 of 9

> Bulk insert of ledger transfer records for the fiscal year 2026.

#### Purpose
- Populate the `ledger_transfer` table with historical transfer data.

#### Type
- Table insert (INSERT INTO `ledger_transfer`).

#### Schema
- `ledger_transfer(transfer_id integer, from_account integer, to_account integer, amount_cents integer, booked_at timestamp)`

#### Writes
- `ledger_transfer`: 1444 rows inserted (transfer_id 18557 – 20000).

#### Dependencies
- None (only writes to `ledger_transfer`).

## Reduce round 1, batch 1 of 1 (9 parts)

> A PostgreSQL data‑dump that creates a payments ledger schema, defines a daily volume view, and seeds customers, accounts, transfers, and fraud‑hold records with tens of thousands of rows.

#### Purpose
- Create core ledger tables: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- Define `ledger_daily_volume` view for daily transfer totals.
- Populate the tables with a large historical dataset for testing and analytics.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.
- **Dialect**: PostgreSQL (standard SQL, no extensions).

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
  - `idx_transfer_from` on `ledger_transfer(from_account)`
  - `idx_transfer_booked` on `ledger_transfer(booked_at)`
  - `idx_hold_account` on `ledger_hold(account_id)`

- **View**
  - `ledger_daily_volume`  
    `SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

#### Reads
- None (the file contains only DDL and INSERT statements).

#### Writes
- `ledger_customer`: 4 000 rows (single bulk INSERT).
- `ledger_account`: 1 352 rows (bulk inserts across parts 1–3).
- `ledger_transfer`: 30 050 rows (61 INSERT statements, grouped into families of 1 000–4 000 rows each).
- `ledger_hold`: 50 rows (single bulk INSERT).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`
- `ledger_transfer.from_account → ledger_account.account_id`
- `ledger_transfer.to_account → ledger_account.account_id`
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- All tables reference `ledger_customer` and `ledger_account` via foreign keys.
- View `ledger_daily_volume` depends on `ledger_transfer`.

#### Facts (exact, whole file)
- INSERT statements: 61  
- VALUES rows (approx): 30 050  
- Tables: 4  
- Views: 1  
- Indexes: 3  
- Functions/procedures: 0  
- Triggers: 0  
- Types: 0  
- ALTER TABLE: 0  
- Sequences: 0  
- Extensions: 0

