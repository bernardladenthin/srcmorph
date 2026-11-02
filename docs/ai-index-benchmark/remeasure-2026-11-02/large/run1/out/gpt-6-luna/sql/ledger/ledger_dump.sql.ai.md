### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:35:19Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 5 of 5 chunks summarized

> Models customer-owned bank accounts, inter-account transfers, daily transfer volume, and accounts held for fraud review.

#### Purpose
- Stores customers, bank accounts, transfers, and fraud-review holds.
- Summarizes transfer amounts by booking date in `ledger_daily_volume`.

#### Type
- Tables, view, indexes, and data inserts; SQL dialect not evident.

#### Schema
- `ledger_customer`: `customer_id INTEGER` (PK); `display_name VARCHAR(120) NOT NULL`; `country_code CHAR(2) NOT NULL`.
- `ledger_account`: `account_id INTEGER` (PK); `customer_id INTEGER NOT NULL` (FK); `iban VARCHAR(34) NOT NULL UNIQUE`; `opened_on DATE NOT NULL`.
- `ledger_transfer`: `transfer_id INTEGER` (PK); `from_account INTEGER NOT NULL` (FK); `to_account INTEGER NOT NULL` (FK); `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`; `booked_at TIMESTAMP NOT NULL`.
- `ledger_hold`: `hold_id INTEGER` (PK); `account_id INTEGER NOT NULL` (FK); `hold_reason VARCHAR(80) NOT NULL`.
- `ledger_daily_volume` view: `day` (date cast of `ledger_transfer.booked_at`); `volume_cents` (sum of `ledger_transfer.amount_cents`), grouped by date.
- Index `idx_transfer_from` on `ledger_transfer(from_account)`.
- Index `idx_transfer_booked` on `ledger_transfer(booked_at)`.
- Index `idx_hold_account` on `ledger_hold(account_id)`; method not specified.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at`, `ledger_transfer.amount_cents`.

#### Writes
- `ledger_customer`: 8 INSERTs; 4,000 rows (customer IDs 1–4000).
- `ledger_account`: 13 INSERTs across the summaries; 6,034 rows reported (account IDs 1–6000).
- `ledger_transfer`: INSERT batches across the summaries; IDs shown span 1–20000, including continuation tuples around batch boundaries. Whole-file total is approximately 30,050 VALUES rows across all tables.
- `ledger_hold`: inserts 50 rows (hold IDs 1–50).

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`
- `ledger_hold.account_id -> ledger_account.account_id`

#### Dependencies
- `ledger_customer`
- `ledger_account`
- `ledger_transfer`
