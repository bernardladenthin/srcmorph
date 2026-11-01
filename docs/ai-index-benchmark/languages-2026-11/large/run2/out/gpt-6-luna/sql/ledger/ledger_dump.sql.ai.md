### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:15:43Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

> Models customers, bank accounts and booked account-to-account transfers, with a view aggregating daily transfer volume.

#### Purpose
- Defines a ledger schema for customers, account ownership, IBANs and transfers.
- Seeds customer, account and transfer records; exposes daily booked transfer volume.

#### Type
- DDL/DML script; SQL dialect not identifiable.
- Creates tables `ledger_customer`, `ledger_account`, `ledger_transfer`, view `ledger_daily_volume`, and indexes `idx_transfer_from`, `idx_transfer_booked`.
- No extensions or user-defined types are identified.

#### Schema
- `ledger_customer`
  - `customer_id INTEGER` — primary key; inline, unnamed.
  - `display_name VARCHAR(120)` — NOT NULL.
  - `country_code CHAR(2)` — NOT NULL.
- `ledger_account`
  - `account_id INTEGER` — primary key; inline, unnamed.
  - `customer_id INTEGER` — NOT NULL; inline, unnamed FK to `ledger_customer(customer_id)`.
  - `iban VARCHAR(34)` — NOT NULL, UNIQUE; inline, unnamed.
  - `opened_on DATE` — NOT NULL.
- `ledger_transfer`
  - `transfer_id INTEGER` — primary key; inline, unnamed.
  - `from_account INTEGER` — NOT NULL; inline, unnamed FK to `ledger_account(account_id)`.
  - `to_account INTEGER` — NOT NULL; inline, unnamed FK to `ledger_account(account_id)`.
  - `amount_cents BIGINT` — NOT NULL; inline, unnamed CHECK `amount_cents > 0`.
  - `booked_at TIMESTAMP` — NOT NULL.
- `ledger_daily_volume` view: `day` (DATE, cast from `booked_at`), `volume_cents` (SUM of `amount_cents`).
- Index `idx_transfer_from` on `ledger_transfer(from_account)`; method/operator class unspecified.
- Index `idx_transfer_booked` on `ledger_transfer(booked_at)`; method/operator class unspecified.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at`, `ledger_transfer.amount_cents`; groups by `CAST(booked_at AS DATE)`.

#### Writes
- `ledger_customer (customer_id, display_name, country_code)`: 8 INSERT statements × 500 rows = 4,000 rows.
- `ledger_account (account_id, customer_id, iban, opened_on)`: 9 INSERT statements with 500, 500, 500, 500, 500, 500, 500, 500, and 434 rows = 4,434 rows; additional visible account INSERT data has an incomplete leading tuple, so its full row count is undetermined.
- `ledger_transfer (transfer_id, from_account, to_account, amount_cents, booked_at)`:
  - Part 2: 12 INSERT statements, `500 + 500 + 500 + 500 + 500 + 500 + 500 + 500 + 500 + 500 + 500 + 34 = 5,534` rows.
  - Part 3: 15 visible INSERT statements, `14 × 500 + 129 = 7,129` rows; a preceding partial statement has unknown target and count.
  - Part 4: 1 incomplete leading tuple + 7,051 complete tuples = 7,052 visible tuples.
  - These are counts from separate visible segments; transfer-ID ranges overlap between segments, so unique-record count is not established.
- Part 5 contains 390 complete value tuples (IDs `19611`–`20000`), but no visible INSERT target; table attribution is unknown.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` → `ledger_transfer`
- `ledger_account` → `ledger_customer`
- `ledger_transfer` → `ledger_account`
