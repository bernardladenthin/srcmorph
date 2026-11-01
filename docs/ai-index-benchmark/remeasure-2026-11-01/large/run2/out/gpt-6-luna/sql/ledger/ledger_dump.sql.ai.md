### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:50:23Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 5 of 5 chunks summarized

> Models ledger customers and bank accounts, records account-to-account transfers, aggregates daily transfer volume, and tracks fraud-review holds.

#### Purpose
- Defines customer, account, transfer, and fraud-hold data, plus a daily transfer-volume view.
- Includes bulk data loads for customers, accounts, holds, and transfers.

#### Type
- SQL DDL/DML script; dialect not evident.
- Creates tables `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`, and view `ledger_daily_volume`.

#### Schema
- `ledger_customer`
  - `customer_id INTEGER` — primary key; constraint name unspecified.
  - `display_name VARCHAR(120) NOT NULL`.
  - `country_code CHAR(2) NOT NULL`.
- `ledger_account`
  - `account_id INTEGER` — primary key; constraint name unspecified.
  - `customer_id INTEGER NOT NULL` — FK to `ledger_customer(customer_id)`.
  - `iban VARCHAR(34) NOT NULL UNIQUE`.
  - `opened_on DATE NOT NULL`.
- `ledger_transfer`
  - `transfer_id INTEGER` — primary key; constraint name unspecified.
  - `from_account INTEGER NOT NULL` — FK to `ledger_account(account_id)`.
  - `to_account INTEGER NOT NULL` — FK to `ledger_account(account_id)`.
  - `amount_cents BIGINT NOT NULL` — CHECK `amount_cents > 0`.
  - `booked_at TIMESTAMP NOT NULL`.
- `ledger_hold`
  - `hold_id INTEGER` — primary key; inline, unnamed.
  - `account_id INTEGER NOT NULL` — FK to `ledger_account(account_id)`; inline, unnamed.
  - `hold_reason VARCHAR(80) NOT NULL`.
- `ledger_daily_volume` view
  - `day` — `CAST(booked_at AS DATE)`.
  - `volume_cents` — `SUM(amount_cents)`.
- Indexes:
  - `idx_transfer_from` — `ledger_transfer(from_account)`; method unspecified.
  - `idx_transfer_booked` — `ledger_transfer(booked_at)`; method unspecified.
  - `idx_hold_account` — `ledger_hold(account_id)`; method unspecified.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at`, `ledger_transfer.amount_cents`; groups by `CAST(booked_at AS DATE)`.

#### Writes
- `ledger_customer(customer_id, display_name, country_code)`: 8 INSERTs × 500 = 4,000 rows.
- `ledger_account(account_id, customer_id, iban, opened_on)`: 9 INSERTs = 500 + 500 + 500 + 500 + 500 + 500 + 500 + 500 + 434 = 4,434 rows; additional visible continuation: 100 + 500 + 500 + 500 = 1,600 rows. The continuation begins at account IDs 4401–4500, followed by IDs 4501–6000.
- `ledger_transfer(transfer_id, from_account, to_account, amount_cents, booked_at)`: visible counts by source part: 11 × 500 + 2 = 5,502 rows; 70 × 100 + 97 = 7,097 rows; 13 × 500 + 113 = 6,613 rows. Another visible fragment reaches transfer ID 20000 but has no INSERT context or count.
- `ledger_hold(hold_id, account_id, hold_reason)`: 1 INSERT with 50 rows.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`
- `ledger_hold.account_id -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` references `ledger_transfer`.
- `ledger_account` references `ledger_customer`.
- `ledger_transfer` references `ledger_account`.
- `ledger_hold` references `ledger_account`.
