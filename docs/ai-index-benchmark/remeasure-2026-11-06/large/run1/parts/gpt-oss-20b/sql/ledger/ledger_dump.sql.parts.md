# Map-reduce answers of sql/ledger/ledger_dump.sql

## Chunk 1 of 9

> A data model for a payments ledger, with customers, accounts and transfers, plus a daily volume view.

#### Purpose
- Stores customer, account and transfer information for a payments system.  
- Provides a view (`ledger_daily_volume`) summarising total transfer volume per day.

#### Type
- PostgreSQL DDL (tables, view, indexes, data inserts).  

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

**View `ledger_daily_volume`**  
- `day` DATE (derived from `booked_at`)  
- `volume_cents` BIGINT (sum of `amount_cents`)  

**Indexes**  
- `idx_transfer_from` ON `ledger_transfer(from_account)`  
- `idx_transfer_booked` ON `ledger_transfer(booked_at)`  

**Foreign‑Key Relationships**  
- `ledger_account.customer_id → ledger_customer.customer_id`  
- `ledger_transfer.from_account → ledger_account.account_id`  
- `ledger_transfer.to_account → ledger_account.account_id`  

#### Reads
- View `ledger_daily_volume` reads from `ledger_transfer`.  

#### Writes
- Bulk `INSERT` into `ledger_customer` (≈ 400 rows in this chunk).  
- No inserts into `ledger_account` or `ledger_transfer` in this chunk.  

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`  
- `ledger_transfer.from_account → ledger_account.account_id`  
- `ledger_transfer.to_account → ledger_account.account_id`

## Chunk 2 of 9

> A bulk data load of the `ledger_account` table, inserting account records with IDs, customer references, IBANs, and opening dates.

#### Purpose
- Populate the `ledger_account` table with account data.

#### Type
- Table: `ledger_account` (PostgreSQL)

#### Schema
- `account_id` integer NOT NULL PRIMARY KEY
- `customer_id` integer NOT NULL
- `iban` text NOT NULL
- `opened_on` date NOT NULL

#### Reads
- None

#### Writes
- `ledger_account`: 3,168 rows inserted (IDs 833 → 4000)

#### Relationships
- None (no foreign keys referenced in this chunk)

## Chunk 3 of 9

> Bulk data inserts for the ledger_account and ledger_transfer tables.

#### Purpose
- Populate the ledger_account table with account records.
- Populate the ledger_transfer table with transfer records.

#### Type
- Table data insert statements for `ledger_account` and `ledger_transfer`.

#### Writes
- **ledger_account**: 852 rows inserted (IDs 4149 – 5000).
- **ledger_transfer**: 1 000 rows inserted (transfer_id 1 – 1000).

## Chunk 4 of 9

> This file populates the `ledger_transfer` table with a large batch of transfer records, each representing a monetary movement between two accounts at a specific timestamp.

#### Purpose
- Bulk load of ledger transfer data for testing, seeding, or migration.

#### Type
- Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer PK
- `from_account` integer NOT NULL
- `to_account` integer NOT NULL
- `amount_cents` integer NOT NULL
- `booked_at` timestamp NOT NULL

#### Writes
- `ledger_transfer`: 3 463 INSERTs (transfer_id 1538 → 5000). No other tables are affected. No UPDATE/DELETE statements are present in this chunk.

## Chunk 5 of 9, half 1

> Inserts bulk ledger transfer records and creates a table for fraud review holds.

#### Purpose
- Populate the `ledger_transfer` table with historical transfer data.
- Define and populate the `ledger_hold` table for fraud review holds.

#### Type
- **Table**: `ledger_hold` (PostgreSQL)
- **Index**: `idx_hold_account` on `ledger_hold(account_id)`
- **Insert Statements**: `INSERT INTO ledger_transfer` (PostgreSQL)

#### Schema
- **ledger_hold**
  - `hold_id INTEGER PRIMARY KEY`
  - `account_id INTEGER NOT NULL REFERENCES ledger_account(account_id)`
  - `hold_reason VARCHAR(80) NOT NULL`
- **Index**
  - `idx_hold_account` – B‑tree on `account_id`

#### Reads
- None (only INSERT statements).

#### Writes
- `ledger_transfer`: 1 020 rows (20 + 1 000)
- `ledger_hold`: 50 rows

#### Relationships
- `ledger_hold.account_id -> ledger_account.account_id`

#### Dependencies
- `ledger_account` (referenced by `ledger_hold.account_id`)
- `ledger_transfer` (assumed to reference `ledger_account` for `from_account` and `to_account`)

## Chunk 5 of 9, half 2

> A bulk load of ledger transfer records, inserting 1,315 rows into the `ledger_transfer` table.

#### Purpose
- Populate the `ledger_transfer` table with historical transfer data.

#### Type
- Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer PRIMARY KEY
- `from_account` integer NOT NULL
- `to_account` integer NOT NULL
- `amount_cents` integer NOT NULL
- `booked_at` timestamp NOT NULL

#### Reads
- None.

#### Writes
- `ledger_transfer`: 1,315 rows inserted (6686‑7000 + 7001‑8000).

#### Relationships
- None (no foreign keys shown in this chunk).

#### Dependencies
- None.

## Chunk 6 of 9

> Inserts 3,451 rows into the ledger_transfer table, adding new transfer records with amounts and timestamps.

#### Type
Table

#### Schema
- `transfer_id` integer
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Writes
- `ledger_transfer`: 3,451 INSERT statements (rows 8390 → 11830). Total rows inserted: **3,451**.

## Chunk 7 of 9

> This file contains a bulk data load for the `ledger_transfer` table, inserting transfer records with IDs, source/destination accounts, amounts, and timestamps.

#### Purpose
- Populate the `ledger_transfer` table with a large set of transfer records for testing or seeding.

#### Type
- Data file (INSERT statements).

#### Schema
- **Table:** `ledger_transfer`
  - `transfer_id` integer PK
  - `from_account` integer NOT NULL
  - `to_account` integer NOT NULL
  - `amount_cents` integer NOT NULL
  - `booked_at` timestamp NOT NULL

#### Reads
- None (only INSERTs).

#### Writes
- Inserts into `ledger_transfer`:
  - 3203 rows (transfer_id 11798 – 15000).

#### Relationships
- None declared in this file (foreign keys would be defined elsewhere).

#### Routine logic
- N/A (no procedures or triggers).

#### Dependencies
- None (only references the `ledger_transfer` table).

## Chunk 8 of 9

> Inserting a large batch of transfer records into the ledger_transfer table, adding 3,414 new rows.

#### Purpose
- Bulk load of historical transfer data for the ledger system.

#### Type
- Table: ledger_transfer

#### Schema
- `ledger_transfer` columns:
  - `transfer_id` integer PK
  - `from_account` integer NOT NULL
  - `to_account` integer NOT NULL
  - `amount_cents` integer NOT NULL
  - `booked_at` timestamp NOT NULL

#### Reads
- None.

#### Writes
- `ledger_transfer`: 3,414 rows inserted.

#### Relationships
- None (no foreign keys referenced in this chunk).

#### Dependencies
- None.

## Chunk 9 of 9

> Bulk insert of ledger transfer records for the period 2026‑02‑18 to 2026‑10‑27.

#### Purpose
- Populate the `ledger_transfer` table with historical transfer data.

#### Type
- Table insert (INSERT INTO `ledger_transfer`).

#### Schema
- `ledger_transfer(transfer_id integer, from_account integer, to_account integer, amount_cents integer, booked_at timestamp)`

#### Reads
- None.

#### Writes
- `ledger_transfer: 1444 rows` (transfer_id 18557‑20000).

#### Relationships
- None (no foreign keys referenced in this chunk).

#### Dependencies
- None.

## Reduce round 1, batch 1 of 1 (10 parts)

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

