### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:11:22Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 5 of 5 chunks summarized

> Models customer-owned bank accounts, account-to-account transfers, and fraud-review holds, with a view of daily transfer volume.

#### Purpose
- Describes a ledger schema for customers, accounts, transfers, and account holds.
- Describes customer/account seed data, transfer loads, and a daily transfer-volume view.

#### Type
- Described objects: tables `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`; view `ledger_daily_volume`; indexes.
- SQL dialect not evident; no `CREATE EXTENSION` or `CREATE TYPE` is described.

#### Schema
- `ledger_customer`
  - `customer_id INTEGER` — primary key; constraint name not given.
  - `display_name VARCHAR(120)` — NOT NULL.
  - `country_code CHAR(2)` — NOT NULL.
- `ledger_account`
  - `account_id INTEGER` — primary key; constraint name not given.
  - `customer_id INTEGER` — NOT NULL; foreign key to `ledger_customer.customer_id`.
  - `iban VARCHAR(34)` — NOT NULL, UNIQUE.
  - `opened_on DATE` — NOT NULL.
- `ledger_transfer`
  - `transfer_id INTEGER` — primary key; constraint name not given.
  - `from_account INTEGER` — NOT NULL; described as referencing `ledger_account.account_id`.
  - `to_account INTEGER` — NOT NULL; described as referencing `ledger_account.account_id`.
  - `amount_cents BIGINT` — NOT NULL; CHECK `amount_cents > 0`.
  - `booked_at TIMESTAMP` — NOT NULL.
- `ledger_hold`
  - `hold_id INTEGER` — primary key; constraint name not given.
  - `account_id INTEGER` — NOT NULL; foreign key to `ledger_account.account_id`.
  - `hold_reason VARCHAR(80)` — NOT NULL.
- `ledger_daily_volume` view: `day` (date cast from `booked_at`), `volume_cents` (sum of `amount_cents`).
- Indexes:
  - `idx_transfer_from` on `ledger_transfer(from_account)`; method not specified.
  - `idx_transfer_booked` on `ledger_transfer(booked_at)`; method not specified.
  - `idx_hold_account` on `ledger_hold(account_id)`; method not specified.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at`, `ledger_transfer.amount_cents`.

#### Writes
- `ledger_customer(customer_id, display_name, country_code)`: Part 1 reports 8 inserts of 500 rows each, total 4,000.
- `ledger_account(account_id, customer_id, iban, opened_on)`: Part 1 reports 8 × 500 + 434 = 4,434 rows; Part 2 separately reports 1,500 rows across three complete visible inserts, excluding a partial opening continuation.
- `ledger_transfer(transfer_id, from_account, to_account, amount_cents, booked_at)`: reported counts differ by part—Part 2: 5,502; Part 3: 7,097; Part 4: 7,051 visible complete tuples, excluding an incomplete opening fragment. Part 1 reports no transfer insert.
- `ledger_hold(hold_id, account_id, hold_reason)`: 50 rows.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`
- `ledger_hold.account_id -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.
- `ledger_account`, `ledger_transfer` are referenced as insert targets or relationship targets.
