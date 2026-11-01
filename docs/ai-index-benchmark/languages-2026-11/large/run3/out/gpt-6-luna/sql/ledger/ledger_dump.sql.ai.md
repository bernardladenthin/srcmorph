### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:24:25Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

> Models customers, bank accounts, and account-to-account money transfers, with a view of daily booked transfer volume and seed-data fragments.

#### Purpose
- Describes a payments ledger schema for customers, accounts, and transfers.
- Defines a daily transfer-volume view and includes account and transfer data-load fragments.

#### Type
- Tables, view, indexes, and DML fragments; SQL dialect not evident.

#### Schema
- `ledger_customer`
  - `customer_id INTEGER` — primary key; constraint name not specified.
  - `display_name VARCHAR(120)` — NOT NULL.
  - `country_code CHAR(2)` — NOT NULL.
- `ledger_account`
  - `account_id INTEGER` — primary key; constraint name not specified.
  - `customer_id INTEGER` — NOT NULL; FK to `ledger_customer(customer_id)`.
  - `iban VARCHAR(34)` — NOT NULL, UNIQUE.
  - `opened_on DATE` — NOT NULL.
- `ledger_transfer`
  - `transfer_id INTEGER` — primary key; constraint name not specified.
  - `from_account INTEGER` — NOT NULL; FK to `ledger_account(account_id)`.
  - `to_account INTEGER` — NOT NULL; FK to `ledger_account(account_id)`.
  - `amount_cents BIGINT` — NOT NULL; CHECK `amount_cents > 0`.
  - `booked_at TIMESTAMP` — NOT NULL.
- `ledger_daily_volume` view
  - `day` — `CAST(ledger_transfer.booked_at AS DATE)`.
  - `volume_cents` — `SUM(ledger_transfer.amount_cents)`, grouped by cast booking date.
- Indexes:
  - `idx_transfer_from` — `ledger_transfer(from_account)`; method and operator class not specified.
  - `idx_transfer_booked` — `ledger_transfer(booked_at)`; method and operator class not specified.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at` and `ledger_transfer.amount_cents`.

#### Writes
- `ledger_customer(customer_id, display_name, country_code)` — 8 reported INSERTs × 500 = 4,000 rows.
- `ledger_account(account_id, customer_id, iban, opened_on)` — Part 1 reports 8 INSERTs × 500 plus 434 = 4,434 rows; Part 2 reports 3 complete INSERTs × 500 = 1,500 rows and an incomplete preceding statement fragment. Counts are from separate provided parts and cannot be reconciled into a single total.
- `ledger_transfer(transfer_id, from_account, to_account, amount_cents, booked_at)`:
  - Part 1 reports no transfer rows.
  - Part 2 describes transfer INSERT batches but ends in an incomplete fragment; exact row count unavailable.
  - Part 3 reports 14 complete INSERTs × 500 plus 129 tuples = 7,129 visible tuples.
  - Part 4 reports 406 tuples in an initial partial batch, 13 INSERTs × 500, and 145 tuples in a final partial INSERT = 7,051 complete visible tuples; one additional leading tuple is truncated.
  - Part 5 reports 390 complete tuples for IDs `19611`–`20000`, with an incomplete preceding tuple; target table and columns are not shown in that part.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` references `ledger_transfer`.
- `ledger_account` references `ledger_customer`.
- `ledger_transfer` references `ledger_account`.
