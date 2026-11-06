# Map-reduce answers of sql/ledger/ledger_dump.sql

## Chunk 1 of 9

> A data‑model for a payments ledger, including customers, accounts and transfer history, plus a daily volume view.

#### Purpose
* Stores customer, account and transfer data for a payments system.  
* Provides a view of daily transfer volume.

#### Type
* **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`  
* **View**: `ledger_daily_volume`  
* **Indexes**: `idx_transfer_from`, `idx_transfer_booked`  
* **Dialect**: PostgreSQL

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

**Indexes**  
- `idx_transfer_from (from_account)`  
- `idx_transfer_booked (booked_at)`  

**View**  
- `ledger_daily_volume` – aggregates `amount_cents` per day from `ledger_transfer`.

#### Reads
* `ledger_customer` – read by `ledger_account` (customer lookup).  
* `ledger_account` – read by `ledger_transfer` (account lookup).  
* `ledger_transfer` – read by `ledger_daily_volume` (aggregation).  

#### Writes
* `ledger_customer`: 40 INSERTs × 100 rows = **4000 rows**.  
* `ledger_account`: 400 INSERTs = **400 rows**.  
* `ledger_transfer`: none in this chunk.  

#### Relationships
* `ledger_account.customer_id → ledger_customer.customer_id` (FK).  
* `ledger_transfer.from_account → ledger_account.account_id` (FK).  
* `ledger_transfer.to_account → ledger_account.account_id` (FK).  

#### Dependencies
* `ledger_customer` referenced by `ledger_account`.  
* `ledger_account` referenced by `ledger_transfer`.  
* `ledger_transfer` used by view `ledger_daily_volume`.

## Chunk 2 of 9

> This chunk inserts a large batch of rows into the `ledger_account` table, adding new customer accounts with their IBANs and opening dates.

#### Purpose
- Populate the `ledger_account` table with account records.

#### Type
- Table: `ledger_account`

#### Schema
- `account_id` integer NOT NULL PRIMARY KEY
- `customer_id` integer NOT NULL
- `iban` text NOT NULL
- `opened_on` date NOT NULL

#### Reads
- None.

#### Writes
- `ledger_account`: 3,168 rows (account_id 833 – 4000).

#### Relationships
- None.

#### Dependencies
- None.

## Chunk 3 of 9

> A bulk data load that populates the core ledger tables with account records and a large set of transfer transactions.

#### Purpose
- Seed the **ledger_account** table with 1 000+ account rows (account_id, customer_id, iban, opened_on).  
- Seed the **ledger_transfer** table with 1 000 transfer rows (transfer_id, from_account, to_account, amount_cents, booked_at).

#### Type
- **Table**: `ledger_account` (PostgreSQL).  
- **Table**: `ledger_transfer` (PostgreSQL).  
- No extensions, types, or triggers are defined in this chunk.

#### Schema
**ledger_account**  
- `account_id` integer PK  
- `customer_id` integer NOT NULL  
- `iban` text NOT NULL  
- `opened_on` date NOT NULL  

**ledger_transfer**  
- `transfer_id` integer PK  
- `from_account` integer NOT NULL  
- `to_account` integer NOT NULL  
- `amount_cents` integer NOT NULL  
- `booked_at` timestamp NOT NULL  

#### Reads
None – the chunk only contains INSERT statements.

#### Writes
- **ledger_account**: 1 000 INSERTs (rows 4149–5000).  
- **ledger_transfer**: 1 000 INSERTs (transfer_id 1–1000).  

#### Relationships
No foreign‑key or join constraints are declared in this chunk.

#### Routine logic
None – only data insertion.

#### Dependencies
None.

## Chunk 4 of 9

> The file contains a bulk data load for the `ledger_transfer` table, inserting transaction records into the ledger.

#### Purpose
- Populate the `ledger_transfer` table with transfer records for the ledger system.

#### Type
- Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer PK
- `from_account` integer
- `to_account` integer
- `amount_cents` integer
- `booked_at` timestamp

#### Reads
- None (only INSERT statements)

#### Writes
- `ledger_transfer`: 3,463 rows inserted (transfer_id 1538 → 5000)

#### Relationships
- None (no foreign keys referenced in this chunk)

#### Dependencies
- None (only data insertion)

## Chunk 5 of 9

> Populates the ledger_transfer and ledger_hold tables with sample transaction data.

#### Purpose
- Seed the ledger system with realistic transfer and hold records for testing and development.

#### Type
- **Table**: `ledger_hold` (PostgreSQL)
- **Index**: `idx_hold_account` on `ledger_hold(account_id)`
- **Insert statements** for `ledger_transfer` (existing table)

#### Schema
- **ledger_hold**
  - `hold_id` INTEGER PRIMARY KEY
  - `account_id` INTEGER NOT NULL REFERENCES `ledger_account(account_id)`
  - `hold_reason` VARCHAR(80) NOT NULL
- **Index**
  - `idx_hold_account` ON `ledger_hold(account_id)`

#### Reads
- None (only INSERT operations).

#### Writes
- `ledger_transfer`: **2500** rows inserted (transfer_id 5501 – 8000).
- `ledger_hold`: **50** rows inserted (hold_id 1 – 50).

#### Relationships
- `ledger_hold.account_id` → `ledger_account.account_id` (foreign key).

#### Dependencies
- `ledger_account` (referenced by `ledger_hold`).

## Chunk 6 of 9

> Inserting a large batch of ledger transfer records into the `ledger_transfer` table.

#### Type
Table

#### Schema
`ledger_transfer`  
- `transfer_id` integer  
- `from_account` integer  
- `to_account` integer  
- `amount_cents` integer  
- `booked_at` timestamp

#### Writes
`ledger_transfer`: 3443 rows inserted (transfer_id 8390 – 11832).

## Chunk 7 of 9

> This file contains a bulk data load of ledger transfer records, inserting thousands of rows into the `ledger_transfer` table.

#### Type
- **Data file** (INSERT statements)

#### Schema
- **ledger_transfer**  
  - `transfer_id` (integer) – primary key  
  - `from_account` (integer)  
  - `to_account` (integer)  
  - `amount_cents` (integer)  
  - `booked_at` (timestamp)

#### Writes
- `ledger_transfer`: 3 414 rows inserted (transfer_id 11798 → 15211)

## Chunk 8 of 9

> This file populates the ledger_transfer table with a large batch of transfer records, each describing a transfer of funds between two accounts at a specific timestamp.

#### Type
Table

#### Schema
- `transfer_id` integer PK  
- `from_account` integer  
- `to_account` integer  
- `amount_cents` integer  
- `booked_at` timestamp

#### Writes
- `ledger_transfer`: 3 414 rows inserted (transfer_id values 15177 → 18590).

## Chunk 9 of 9

> Bulk insert of 1,000 ledger transfer records into the `ledger_transfer` table.

#### Purpose
* Populates the `ledger_transfer` table with historical transfer data.

#### Type
* Table: `ledger_transfer` (PostgreSQL)

#### Schema
- `transfer_id` integer PK
- `from_account` integer NOT NULL
- `to_account` integer NOT NULL
- `amount_cents` integer NOT NULL
- `booked_at` timestamp NOT NULL

#### Reads
None.

#### Writes
- `ledger_transfer`: 1,000 rows inserted (transfer_id 19001 – 20000).

#### Relationships
None (no foreign keys referenced in this chunk).

#### Dependencies
None.

## Reduce round 1, batch 1 of 1 (9 parts)

> A PostgreSQL data‑load script that seeds a payments ledger with customers, accounts, transfers, and holds, and defines a daily volume view.

#### Purpose
* Populate the core ledger tables (`ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`) with bulk test data.  
* Provide a view (`ledger_daily_volume`) that aggregates transfer volume per day.

#### Type
* **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`  
* **View**: `ledger_daily_volume`  
* **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`  
* **Dialect**: PostgreSQL

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

**ledger_hold**  
- `hold_id INTEGER PRIMARY KEY`  
- `account_id INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
- `hold_reason VARCHAR(80) NOT NULL`  

**Indexes**  
- `idx_transfer_from (from_account)`  
- `idx_transfer_booked (booked_at)`  
- `idx_hold_account (account_id)`  

**View**  
- `ledger_daily_volume` – aggregates `amount_cents` per day from `ledger_transfer`.

#### Reads
* `ledger_customer` – read by `ledger_account`.  
* `ledger_account` – read by `ledger_transfer` and `ledger_hold`.  
* `ledger_transfer` – read by `ledger_daily_volume`.

#### Writes
* `ledger_customer`: 4000 rows (40 INSERTs × 100).  
* `ledger_account`: 4568 rows (400 + 3168 + 1000).  
* `ledger_transfer`: 18 234 rows (sum of all bulk INSERTs).  
* `ledger_hold`: 50 rows.

#### Relationships
* `ledger_account.customer_id → ledger_customer.customer_id` (FK).  
* `ledger_transfer.from_account → ledger_account.account_id` (FK).  
* `ledger_transfer.to_account → ledger_account.account_id` (FK).  
* `ledger_hold.account_id → ledger_account.account_id` (FK).

#### Dependencies
* `ledger_account` depends on `ledger_customer`.  
* `ledger_transfer` depends on `ledger_account`.  
* `ledger_hold` depends on `ledger_account`.  
* `ledger_daily_volume` depends on `ledger_transfer`.

