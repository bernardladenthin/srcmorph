# Map-reduce answers of sql/ledger/ledger_dump.sql

## Chunk 1 of 9, half 1

> A data‑model for a payments ledger, defining customers, accounts and transfers, and pre‑populating the customer table with thousands of rows.

#### Purpose
- Stores customer, account and transfer information for a payments system.
- Provides a daily volume view for reporting.

#### Type
- PostgreSQL tables and view.

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
- **ledger_daily_volume** (view)  
  - `day DATE` (derived from `booked_at`)  
  - `volume_cents BIGINT` (sum of `amount_cents`)  

Indexes  
- `idx_transfer_from` on `ledger_transfer(from_account)`  
- `idx_transfer_booked` on `ledger_transfer(booked_at)`

#### Reads
- None (no SELECT statements in this chunk).

#### Writes
- `ledger_customer`: 3000 rows inserted (three INSERT statements covering IDs 1‑3000).

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`  
- `ledger_transfer.from_account -> ledger_account.account_id`  
- `ledger_transfer.to_account -> ledger_account.account_id`

#### Dependencies
- None beyond the tables and view defined above.

## Chunk 1 of 9, half 2

> A bulk data load that populates the `ledger_customer` and `ledger_account` tables with new customer and account records.

#### Purpose
- Seed the ledger system with a large set of customer and account data for testing or migration.

#### Type
- Data insert statements (DDL/DML) for tables `ledger_customer` and `ledger_account`.

#### Schema
- **ledger_customer**  
  - `customer_id` integer PK  
  - `display_name` text  
  - `country_code` char(2)  

- **ledger_account**  
  - `account_id` integer PK  
  - `customer_id` integer FK → `ledger_customer.customer_id`  
  - `iban` varchar(34)  
  - `opened_on` date  

#### Reads
- None (pure inserts).

#### Writes
- `ledger_customer`: 2 321 rows inserted (rows 2680‑4000 + 3001‑4000).  
- `ledger_account`: 800 rows inserted (account_id 1‑800).

#### Relationships
- `ledger_account.customer_id` → `ledger_customer.customer_id`

#### Dependencies
- None beyond the two tables referenced above.

## Chunk 2 of 9

> Inserts a large batch of customer‑account records into the ledger_account table.

#### Purpose
Bulk data load for the ledger_account table.

#### Type
Table: `ledger_account` (PostgreSQL).

#### Schema
- `account_id` integer PK
- `customer_id` integer NOT NULL
- `iban` text NOT NULL
- `opened_on` date NOT NULL

#### Reads
None.

#### Writes
- `ledger_account`: 3171 rows inserted (account_id 830 → 4000).

#### Relationships
None.

#### Routine logic
None.

#### Dependencies
None.

## Chunk 3 of 9

> Bulk data inserts for the ledger_account and ledger_transfer tables, populating them with account and transfer records.

#### Purpose
- Populate the `ledger_account` and `ledger_transfer` tables with initial data for a financial ledger system.

#### Type
- Data insertion statements (INSERT INTO) for two tables: `ledger_account` and `ledger_transfer`.

#### Writes
- `ledger_account`: 858 rows inserted (IDs 4143 – 5000).  
- `ledger_transfer`: 1 000 rows inserted (IDs 1 – 1000).

#### Dependencies
- None (no referenced tables, functions, or other objects).

## Chunk 4 of 9

> A bulk load of ledger transfer records, inserting thousands of rows into the `ledger_transfer` table.

#### Type
- **Table**: `ledger_transfer` (PostgreSQL)

#### Writes
- **ledger_transfer**: 3 472 rows inserted (transfer_id 1529 → 5000).  

No other tables are referenced or modified.

## Chunk 5 of 9

> Inserts sample ledger transfer and hold records into the ledger tables.

#### Purpose
- Populate the `ledger_transfer` and `ledger_hold` tables with sample data for testing or seeding.

#### Type
- SQL data insertion statements for tables:
  - `ledger_transfer`
  - `ledger_hold`

#### Writes
- `ledger_transfer`: 3 441 rows (transfer_id 4968 → 8408)
- `ledger_hold`: 50 rows (hold_id 1 → 50)

#### Relationships
- None (no foreign‑key references shown in this chunk).

#### Dependencies
- None.

## Chunk 6 of 9

> **This chunk inserts a large batch of ledger transfer records into the `ledger_transfer` table, populating it with transaction data for accounts.**

#### Type
- **Table**: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer PK
- `from_account` integer NOT NULL
- `to_account` integer NOT NULL
- `amount_cents` integer NOT NULL
- `booked_at` timestamp NOT NULL

#### Writes
- Inserts **3 427** rows into `ledger_transfer` (IDs 8374 → 11800).  
- Each row records a transfer amount (in cents) and the booking timestamp. No other tables are affected.

## Chunk 7 of 9

> Inserting a large batch of ledger transfer records into the `ledger_transfer` table.

#### Type
Table

#### Schema
- `transfer_id` integer
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Writes
- `ledger_transfer`: 3 222 rows inserted (rows 11779‑15000 across 12 INSERT statements).

## Chunk 8 of 9

> This chunk inserts a large number of rows into the `ledger_transfer` table, populating transfer data for the ledger system.

#### Purpose
- Populate the `ledger_transfer` table with transfer records.

#### Type
- **Table**: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` INT PK  
- `from_account` INT  
- `to_account` INT  
- `amount_cents` INT  
- `booked_at` TIMESTAMP

#### Writes
- `ledger_transfer`: 3 406 rows inserted  
  - `transfer_id` range: **15155 – 18560**  
  - Each row specifies `from_account`, `to_account`, `amount_cents`, and `booked_at`.  
  - No other tables are affected.

## Chunk 9 of 9

> Inserts a large batch of ledger transfer records into the ledger_transfer table.

#### Purpose
- Populate the ledger_transfer table with historical transfer data.

#### Type
- Table: `ledger_transfer` (PostgreSQL).

#### Schema
- `transfer_id` integer PK
- `from_account` integer NOT NULL
- `to_account` integer NOT NULL
- `amount_cents` integer NOT NULL
- `booked_at` timestamp NOT NULL

#### Writes
- `ledger_transfer`: 1469 rows (18532‑19000: 469 rows; 19001‑19500: 500 rows; 19501‑20000: 500 rows)

#### Relationships
- None declared in this chunk.

## Reduce round 1, batch 1 of 1 (10 parts)

> A PostgreSQL ledger schema that defines customers, accounts, transfers, and a daily volume view, then bulk‑loads thousands of rows into each table for testing or migration.

#### Purpose
- Stores customer, account, and transfer data for a payments ledger.
- Provides a daily volume view for reporting.

#### Type
- PostgreSQL tables, view, and indexes.

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

- **ledger_daily_volume** (view)  
  - `day DATE` (derived from `booked_at`)  
  - `volume_cents BIGINT` (sum of `amount_cents`)  

Indexes  
- `idx_transfer_from` on `ledger_transfer(from_account)`  
- `idx_transfer_booked` on `ledger_transfer(booked_at)`

#### Reads
- None (only INSERT statements).

#### Writes
- `ledger_customer`: 5 321 rows inserted (3000 + 2321).  
- `ledger_account`: 4 829 rows inserted (800 + 3171 + 858).  
- `ledger_transfer`: 19 437 rows inserted (1000 + 3472 + 3441 + 3427 + 3222 + 3406 + 1469).  
- `ledger_hold`: 50 rows inserted.  

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`  
- `ledger_transfer.from_account -> ledger_account.account_id`  
- `ledger_transfer.to_account -> ledger_account.account_id`

#### Dependencies
- None beyond the tables, view, and indexes defined above.

