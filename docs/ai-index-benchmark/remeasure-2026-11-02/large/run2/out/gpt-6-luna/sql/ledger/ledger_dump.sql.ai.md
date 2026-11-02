### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:51:07Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 5 of 5 chunks summarized

> Models customers, bank accounts, account holds, and monetary transfers, with a daily summary of transfer volume.

#### Purpose
- Stores customer and account records, fraud-review holds, and transfers between accounts.
- Provides `ledger_daily_volume`, aggregating transfer amounts by booking date.

#### Type
- Tables, view, indexes, and DML; SQL dialect not explicit.

#### Schema
- `ledger_customer`: `customer_id INTEGER` (PK); `display_name VARCHAR(120)` (NOT NULL); `country_code CHAR(2)` (NOT NULL).
- `ledger_account`: `account_id INTEGER` (PK); `customer_id INTEGER` (NOT NULL, FK); `iban VARCHAR(34)` (NOT NULL, UNIQUE); `opened_on DATE` (NOT NULL).
- `ledger_transfer`: `transfer_id INTEGER` (PK); `from_account INTEGER` (NOT NULL, FK); `to_account INTEGER` (NOT NULL, FK); `amount_cents BIGINT` (NOT NULL, CHECK `amount_cents > 0`); `booked_at TIMESTAMP` (NOT NULL).
- `ledger_hold`: `hold_id INTEGER` (PK); `account_id INTEGER` (NOT NULL, FK); `hold_reason VARCHAR(80)` (NOT NULL).
- `ledger_daily_volume` view: `day` (date cast of `booked_at`); `volume_cents` (sum of `amount_cents`); grouped by booking date.
- Index `idx_transfer_from` on `ledger_transfer(from_account)`; method not specified.
- Index `idx_transfer_booked` on `ledger_transfer(booked_at)`; method not specified.
- Index `idx_hold_account` on `ledger_hold(account_id)`; method not specified.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at` and `ledger_transfer.amount_cents`.

#### Writes
- `ledger_customer(customer_id, display_name, country_code)`: 8 INSERTs, 4,000 rows; `customer_id` 1–4000.
- `ledger_account(account_id, customer_id, iban, opened_on)`: 10 INSERTs, about 6,034 rows; IDs span 1–6000.
- `ledger_transfer(transfer_id, from_account, to_account, amount_cents, booked_at)`: 42 INSERTs, about 20,000 rows; IDs span 1–20000.
- `ledger_hold(hold_id, account_id, hold_reason)`: 1 INSERT, 50 rows; `hold_id` 1–50.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`
- `ledger_hold.account_id -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.
- `ledger_account` depends on `ledger_customer`.
- `ledger_transfer` and `ledger_hold` depend on `ledger_account`.
