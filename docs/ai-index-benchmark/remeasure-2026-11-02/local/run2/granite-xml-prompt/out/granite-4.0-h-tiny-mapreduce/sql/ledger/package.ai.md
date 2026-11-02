### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T01:12:39Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A SQL object that models financial ledger transfers between accounts, recording deposits and withdrawals with timestamps.

#### Purpose
- Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

#### Responsibilities
- **ledger_transfer** table: stores individual transfers with `transfer_id`, `from_account`, `to_account`, `amount_cents`, and `booked_at`.
- **ledger_account** table: maintains account balances (`balance_cents`).
- **ledger_category** table: (not used in this file) would categorize transfers.
- **Routine logic**: 6,500 `INSERT` statements populate `ledger_transfer` with transfers between accounts, inserting corresponding rows into `ledger_account` (via foreign keys).

#### Key units
- **ledger_transfer** (`ledger_transfer.sql`):  
  - Columns: `transfer_id` PK, `from_account` FK, `to_account` FK, `amount_cents` NOT NULL, `booked_at` NOT NULL.  
  - Primary key: `pk_ledger_transfer`.  
  - Unique constraint: `uk_ledger_transfer` on (`from_account`, `to_account`, `booked_at`).  
  - Index: `idx_ledger_transfer_booked_at` on `booked_at`.  
- **ledger_account** (`ledger_account.sql`):  
  - Columns: `account_id` PK, `balance_cents`.  
- **ledger_category** (`ledger_category.sql`):  
  - Columns: `category_id` PK, `name` NOT NULL.  

#### Data flow
1. **INSERT** statements in `ledger_transfer.sql` populate `ledger_transfer` with 6,500 rows, each inserting a transfer between two accounts and recording the amount and booking date.  
2. Corresponding rows are inserted into `ledger_account` via foreign key constraints.  
3. `ledger_category` remains unused in this file.

#### Dependencies
- **Internal**: `ledger_account` (referenced by foreign keys), `ledger_category` (referenced but not used).  
- **External**: PostgreSQL (PostgreSQL-specific syntax and functions).

#### Cross-cutting
- Uses PostgreSQL-specific data types (`INTEGER`, `BIGINT`, `TIMESTAMP`).  
- Follows standard CRUD pattern with `INSERT` statements.  
- No error handling, logging, or concurrency mechanisms defined in this file.
