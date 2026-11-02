### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:24:46Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized (1 part summary cut at the output budget)

> Creates a payments ledger schema with customers, accounts, transfers, and holds, and populates it with test data for development and reporting.

#### Purpose
- Define core ledger tables and a daily volume view.
- Seed tables with realistic customer, account, transfer, and hold records.

#### Type
- PostgreSQL DDL: tables, indexes, view, bulk INSERTs.

#### Schema
- **ledger_customer**
  - `customer_id INTEGER PRIMARY KEY`
  - `display_name VARCHAR(120) NOT NULL`
  - `country_code CHAR(2) NOT NULL`
- **ledger_account**
  - `account_id INTEGER PRIMARY KEY`
  - `customer_id INTEGER NOT NULL REFERENCES ledger_customer(customer_id)`
  - `iban VARCHAR(34) NOT NULL UNIQUE`
  - `opened_on DATE NOT NULL`
- **ledger_transfer**
  - `transfer_id INTEGER PRIMARY KEY`
  - `from_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`
  - `to_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`
  - `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`
  - `booked_at TIMESTAMP NOT NULL`
- **ledger_hold**
  - `hold_id INTEGER PRIMARY KEY`
  - `account_id INTEGER NOT NULL REFERENCES ledger_account(account_id)`
- **Indexes**
  - `idx_transfer_from` on `ledger_transfer(from_account)`
  - `idx_transfer_booked` on `ledger_transfer(booked_at)`
  - `idx_hold_account` on `ledger_hold(account_id)`
- **View**
  - `ledger_daily_volume` – aggregates `amount_cents` per day from `ledger_transfer`.

#### Reads
- None (only INSERT statements).

#### Writes
- `ledger_customer`: ~6 600 rows (2600 + 4000).
- `ledger_account`: ~5 160 rows (500 + 3 160 + 1 500).
- `ledger_transfer`: ~15 741 rows (3 438 + 1 278 + 3 071 + 3 154 + 3 421 + 1 379).
- `ledger_hold`: ~1 749 rows (remaining to reach ~30 050 total).

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id`
- `ledger_transfer.from_account -> ledger_account.account_id`
- `ledger_transfer.to_account -> ledger_account.account_id`
- `ledger_hold.account_id -> ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.
