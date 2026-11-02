### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:35:09Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A PostgreSQL data‑dump that seeds a payments ledger with customers, accounts, transfers, holds, and a daily‑volume view.

#### Purpose
- Populate `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- Provide a materialised view `ledger_daily_volume` for daily transfer volume.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- **View**: `ledger_daily_volume`.
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Schema
**ledger_customer**  
`customer_id INTEGER PRIMARY KEY`  
`display_name VARCHAR(120) NOT NULL`  
`country_code CHAR(2) NOT NULL`

**ledger_account**  
`account_id INTEGER PRIMARY KEY`  
`customer_id INTEGER NOT NULL REFERENCES ledger_customer(customer_id)`  
`iban VARCHAR(34) NOT NULL UNIQUE`  
`opened_on DATE NOT NULL`

**ledger_transfer**  
`transfer_id INTEGER PRIMARY KEY`  
`from_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
`to_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
`amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`  
`booked_at TIMESTAMP NOT NULL`

**ledger_hold**  
`hold_id INTEGER PRIMARY KEY`  
`account_id INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
`hold_reason VARCHAR(80) NOT NULL`

**Indexes**  
- `idx_transfer_from ON ledger_transfer(from_account)`  
- `idx_transfer_booked ON ledger_transfer(booked_at)`  
- `idx_hold_account ON ledger_hold(account_id)`

**View `ledger_daily_volume`**  
`SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at`, `ledger_transfer.amount_cents`.
- `ledger_transfer` reads `ledger_account.account_id` via foreign keys.
- `ledger_hold` reads `ledger_account.account_id`.

#### Writes
- `ledger_customer`: 4000 rows inserted (IDs 1‑4000).  
- `ledger_account`: 4000 rows inserted (IDs 1‑4000).  
- `ledger_transfer`: 1 700 + 5000 + 2000 + 4000 + 3363 + 19000 + 20000 ≈ 48 000 rows inserted (multiple batches).  
- `ledger_hold`: 50 rows inserted (IDs 1‑50).

#### Relationships
- `ledger_account.customer_id → ledger_customer.customer_id`  
- `ledger_transfer.from_account → ledger_account.account_id`  
- `ledger_transfer.to_account → ledger_account.account_id`  
- `ledger_hold.account_id → ledger_account.account_id`

#### Dependencies
- View `ledger_daily_volume` depends on `ledger_transfer`.  
- All foreign keys reference `ledger_customer` and `ledger_account`.
