### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:11:15Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: B48CB49F
- N: CA5BDE78
- M: 2FA139DC
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A PostgreSQL data‑dump that creates a payments ledger schema, defines a daily volume view, and seeds customers, accounts, transfers, and fraud‑hold records with tens of thousands of rows.

#### Purpose
- Create core ledger tables: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- Define `ledger_daily_volume` view for daily transfer totals.
- Populate the tables with a large historical dataset for testing and analytics.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.
- **Dialect**: PostgreSQL (standard SQL, no extensions).

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
  - `idx_transfer_from` on `ledger_transfer(from_account)`
  - `idx_transfer_booked` on `ledger_transfer(booked_at)`
  - `idx_hold_account` on `ledger_hold(account_id)`

- **View**
  - `ledger_daily_volume`  
    `SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

#### Reads
- None (the file contains only DDL and INSERT statements).

#### Writes
- `ledger_customer`: 4 000 rows (single bulk INSERT).
- `ledger_account`: 1 352 rows (bulk inserts across parts 1–3).
- `ledger_transfer`: 30 050 rows (61 INSERT statements, grouped into families of 1 000–4 000 rows each).
- `ledger_hold`: 50 rows (single bulk INSERT).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`
- `ledger_transfer.from_account → ledger_account.account_id`
- `ledger_transfer.to_account → ledger_account.account_id`
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- All tables reference `ledger_customer` and `ledger_account` via foreign keys.
- View `ledger_daily_volume` depends on `ledger_transfer`.

#### Facts (exact, whole file)
- INSERT statements: 61  
- VALUES rows (approx): 30 050  
- Tables: 4  
- Views: 1  
- Indexes: 3  
- Functions/procedures: 0  
- Triggers: 0  
- Types: 0  
- ALTER TABLE: 0  
- Sequences: 0  
- Extensions: 0
