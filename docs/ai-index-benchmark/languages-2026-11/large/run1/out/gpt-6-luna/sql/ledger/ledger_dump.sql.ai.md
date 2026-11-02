### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:36:50Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

> Models a banking ledger of customers, accounts, and timestamped transfers, with a view aggregating booked transfer volume by day.

#### Purpose
- Defines customer, account, and transfer records, plus a daily transfer-volume view.
- Loads customer, account, and transfer data through multi-row inserts.

#### Type
- Tables, view, indexes, and DML; SQL dialect not evident.
- No extensions or user-defined types are described.

#### Schema
- `ledger_customer`: `customer_id INTEGER` (PK); `display_name VARCHAR(120) NOT NULL`; `country_code CHAR(2) NOT NULL`.
- `ledger_account`: `account_id INTEGER` (PK); `customer_id INTEGER NOT NULL` (FK); `iban VARCHAR(34) NOT NULL UNIQUE`; `opened_on DATE NOT NULL`.
- `ledger_transfer`: `transfer_id INTEGER` (PK); `from_account INTEGER NOT NULL` (FK); `to_account INTEGER NOT NULL` (FK); `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`; `booked_at TIMESTAMP NOT NULL`.
- `ledger_daily_volume`: view columns `day` (date cast of `ledger_transfer.booked_at`), `volume_cents` (sum of `ledger_transfer.amount_cents`).
- Inline PK, FK, UNIQUE, and CHECK constraints have no declared names.
- Index `idx_transfer_from` on `ledger_transfer(from_account)`; method unspecified.
- Index `idx_transfer_booked` on `ledger_transfer(booked_at)`; method unspecified.
- DML-only portions list `ledger_account` columns `account_id`, `customer_id`, `iban`, `opened_on`, and `ledger_transfer` columns `transfer_id`, `from_account`, `to_account`, `amount_cents`, `booked_at`; types and constraints are not shown there.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at`, `ledger_transfer.amount_cents`.

#### Writes
- `ledger_customer`: 8 inserts of 500 rows each = **4,000 rows**.
- `ledger_account`: described counts are 4,434 rows in Part 1 and 1,500 rows in Part 2; the relationship between these batches is unspecified.
- `ledger_transfer`: Part 2 describes 11 inserts of 500 rows and one of 34 = **5,534 rows**.
- `ledger_transfer`: Part 3 describes 71 inserts of 100 rows and one incomplete insert with 29 visible tuples = **7,129 visible rows**.
- `ledger_transfer`: Part 4 describes **7,051 complete tuples**, plus one incomplete leading tuple.
- Unidentified target: Part 5 describes an in-progress insert with **390 visible tuples**; target table and columns are absent.
- Transfer ranges overlap across described parts; no combined total is stated.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` references `ledger_transfer`.
- `ledger_account` references `ledger_customer`.
- `ledger_transfer` references `ledger_account`.
