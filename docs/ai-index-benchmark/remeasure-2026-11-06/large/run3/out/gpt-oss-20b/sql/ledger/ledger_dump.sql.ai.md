### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:15:46Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: B48CB49F
- N: CA5BDE78
- M: 2FA139DC
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A PostgreSQL data‑load script that seeds a payments ledger with customers, accounts, transfers, and holds, and defines a daily volume view.

#### Purpose
* Populate the core ledger tables (`ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`) with bulk test data.  
* Provide a view (`ledger_daily_volume`) that aggregates transfer volume per day.

#### Type
* **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`  
* **View**: `ledger_daily_volume`  
* **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`  
* **Dialect**: PostgreSQL

#### Schema
**ledger_customer**  
- `customer_id INTEGER PRIMARY KEY`  
- `display_name VARCHAR(120) NOT NULL`  
- `country_code CHAR(2) NOT NULL`  

**ledger_account**  
- `account_id INTEGER PRIMARY KEY`  
- `customer_id INTEGER NOT NULL REFERENCES ledger_customer(customer_id)`  
- `iban VARCHAR(34) NOT NULL UNIQUE`  
- `opened_on DATE NOT NULL`  

**ledger_transfer**  
- `transfer_id INTEGER PRIMARY KEY`  
- `from_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
- `to_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
- `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`  
- `booked_at TIMESTAMP NOT NULL`  

**ledger_hold**  
- `hold_id INTEGER PRIMARY KEY`  
- `account_id INTEGER NOT NULL REFERENCES ledger_account(account_id)`  
- `hold_reason VARCHAR(80) NOT NULL`  

**Indexes**  
- `idx_transfer_from (from_account)`  
- `idx_transfer_booked (booked_at)`  
- `idx_hold_account (account_id)`  

**View**  
- `ledger_daily_volume` – aggregates `amount_cents` per day from `ledger_transfer`.

#### Reads
* `ledger_customer` – read by `ledger_account`.  
* `ledger_account` – read by `ledger_transfer` and `ledger_hold`.  
* `ledger_transfer` – read by `ledger_daily_volume`.

#### Writes
* `ledger_customer`: 4000 rows (40 INSERTs × 100).  
* `ledger_account`: 4568 rows (400 + 3168 + 1000).  
* `ledger_transfer`: 18 234 rows (sum of all bulk INSERTs).  
* `ledger_hold`: 50 rows.

#### Relationships
* `ledger_account.customer_id → ledger_customer.customer_id` (FK).  
* `ledger_transfer.from_account → ledger_account.account_id` (FK).  
* `ledger_transfer.to_account → ledger_account.account_id` (FK).  
* `ledger_hold.account_id → ledger_account.account_id` (FK).

#### Dependencies
* `ledger_account` depends on `ledger_customer`.  
* `ledger_transfer` depends on `ledger_account`.  
* `ledger_hold` depends on `ledger_account`.  
* `ledger_daily_volume` depends on `ledger_transfer`.
