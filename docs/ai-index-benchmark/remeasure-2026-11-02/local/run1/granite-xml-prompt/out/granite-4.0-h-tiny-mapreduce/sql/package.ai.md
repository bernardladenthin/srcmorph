### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:30:46Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A SQL object that models financial ledger transfers between accounts, recording deposits and withdrawals with timestamps.

#### Purpose
- Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

#### Responsibilities
- **ledger_transfer** table: stores individual transfers with `transfer_id`, `from_account`, `to_account`, `amount_cents`, and `booked_at`.
- **ledger_account** table: maintains account balances (`balance_cents`) referenced by transfers.
- **ledger_category** table: (not used in this file) provides category information for transfers.
- **Routine logic**: 6,500 `INSERT` statements populate `ledger_transfer` with transfers between accounts, inserting corresponding rows into `ledger_account` as needed.

#### Key units
- **ledger_transfer** (`ledger_transfer.sql`):  
  - Columns: `transfer_id` PK, `from_account` FK, `to_account` FK, `amount_cents` NOT NULL, `booked_at` NOT NULL.  
  - Indexes: `pk_ledger_transfer`, `uk_ledger_transfer`, `idx_ledger_transfer_booked_at`.  
  - Relationships: `from_account → ledger_account.account_id`, `to_account → ledger_account.account_id`.

- **ledger_account** (`ledger_account.sql`):  
  - Columns: `account_id` PK, `balance_cents`.  

- **ledger_category** (`ledger_category.sql`):  
  - Columns: `category_id` PK, `name` NOT NULL.  

#### Data flow
1. **INSERT** statements in `ledger_transfer.sql` populate `ledger_transfer` with 6,500 rows, each inserting a transfer and referencing `ledger_account` rows.
2. `ledger_account` rows are inserted via FK inserts to support transfer amounts.
3. `ledger_category` remains unused in this file but is available for future categorization.

#### Dependencies
- **Internal**: `ledger_account` (FK references), `ledger_category` (referenced but not used).
- **External**: PostgreSQL (PostgreSQL-specific syntax and functions).

#### Cross-cutting
- Uses PostgreSQL-specific data types (`INTEGER`, `BIGINT`, `TIMESTAMP`) and constraints (`PK`, `FK`, `UNIQUE`, `NOT NULL`).  
- No shared base types, exception handling, or concurrency notes specific to this file.
