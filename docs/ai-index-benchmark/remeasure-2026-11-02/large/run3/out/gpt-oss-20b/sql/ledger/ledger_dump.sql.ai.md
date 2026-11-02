### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:08:19Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A PostgreSQL data‑dump that creates a payments ledger schema, loads thousands of customers, accounts, holds and transfer records, and defines a view summarising daily transfer volume.

#### Purpose
- Define core ledger tables (`ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`) and a daily‑volume view.
- Populate these tables with realistic seed data for testing and demo purposes.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.
- **Dialect**: PostgreSQL (standard SQL, no extensions or custom types).

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
  - `hold_reason VARCHAR(80) NOT NULL`
- **Indexes**
  - `idx_transfer_from ON ledger_transfer(from_account)`
  - `idx_transfer_booked ON ledger_transfer(booked_at)`
  - `idx_hold_account ON ledger_hold(account_id)`
- **View**
  - `ledger_daily_volume`  
    `SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- **ledger_customer**: 4 000 rows (40 INSERTs).
- **ledger_account**: 3 760 rows (600 + 3 160 + ~1 000).
- **ledger_hold**: 50 rows.
- **ledger_transfer**: about 14 000 rows (sum of 3 438, 2 570, 3 420, 3 423, 1 379 and earlier 1 600+ inserts).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`
- `ledger_transfer.from_account → ledger_account.account_id`
- `ledger_transfer.to_account → ledger_account.account_id`
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.
- All indexes depend on their respective tables.
