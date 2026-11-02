### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:06:06Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 5 of 5 chunks summarized

> Models customers, bank accounts, inter-account transfers, and fraud-review holds, with a daily transfer-volume summary.

#### Purpose
- Stores ledger customers, accounts, transfers, and account holds.
- Aggregates booked transfer amounts by day.

#### Type
- Tables: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`; view: `ledger_daily_volume`; indexes; DML; SQL dialect not evident.

#### Schema
- `ledger_customer`: `customer_id INTEGER` (PK); `display_name VARCHAR(120) NOT NULL`; `country_code CHAR(2) NOT NULL`.
- `ledger_account`: `account_id INTEGER` (PK); `customer_id INTEGER NOT NULL` (FK); `iban VARCHAR(34) NOT NULL UNIQUE`; `opened_on DATE NOT NULL`.
- `ledger_transfer`: `transfer_id INTEGER` (PK); `from_account INTEGER NOT NULL` (FK); `to_account INTEGER NOT NULL` (FK); `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`; `booked_at TIMESTAMP NOT NULL`.
- `ledger_hold`: `hold_id INTEGER` (PK); `account_id INTEGER NOT NULL` (FK); `hold_reason VARCHAR(80) NOT NULL`.
- `ledger_daily_volume`: `day` = `CAST(booked_at AS DATE)`; `volume_cents` = `SUM(amount_cents)`, grouped by date.
- Index `idx_transfer_from`: `ledger_transfer (from_account)`.
- Index `idx_transfer_booked`: `ledger_transfer (booked_at)`.
- Index `idx_hold_account`: `ledger_hold (account_id)`; method not specified.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at`, `ledger_transfer.amount_cents`.

#### Writes
- `ledger_customer`: 8 INSERTs × 500 rows = 4,000 rows; IDs 1–4000; columns `customer_id`, `display_name`, `country_code`.
- `ledger_account`: 13 INSERT batches across the summaries; about 6,034 rows; IDs reported from 1–6000; columns `account_id`, `customer_id`, `iban`, `opened_on`.
- `ledger_transfer`: 40 INSERTs across the summaries; about 19,823 rows in reported batches, plus 174 continuation tuples; columns `transfer_id`, `from_account`, `to_account`, `amount_cents`, `booked_at`. Reported ranges: 1–5440, 5405–12457, 12423–19426, and 19501–20000.
- `ledger_hold`: 1 INSERT with 50 rows; `hold_id` values 1–50; columns `hold_id`, `account_id`, `hold_reason`.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`
- `ledger_hold.account_id -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.
