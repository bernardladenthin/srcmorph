### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:13:54Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A payments ledger schema with customers, accounts, transfers, and a daily volume view, plus bulk data inserts.

#### Purpose
- Store customer, account, and transfer data for a financial ledger.
- Provide a view for daily transfer volume.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold` (in later parts).
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.
- **Dialect**: PostgreSQL.

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
  - `ledger_daily_volume(day DATE, volume_cents BIGINT)`
    ```sql
    SELECT CAST(booked_at AS DATE) AS day,
           SUM(amount_cents) AS volume_cents
    FROM ledger_transfer
    GROUP BY CAST(booked_at AS DATE);
    ```

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- `ledger_customer`: 4000 rows inserted (IDs 1–4000).
- `ledger_account`: 5000 rows inserted (IDs 1–5000).
- `ledger_transfer`: ~12 000+ rows inserted across multiple blocks (exact counts per block vary; total >12 000).
- `ledger_hold`: 50 rows inserted.

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`
- `ledger_transfer.from_account → ledger_account.account_id`
- `ledger_transfer.to_account → ledger_account.account_id`
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- `ledger_daily_volume` depends on `ledger_transfer`.
- `ledger_transfer` and `ledger_hold` depend on `ledger_account`.
- `ledger_account` depends on `ledger_customer`.
