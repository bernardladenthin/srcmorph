### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T01:04:58Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 17 of 17 chunks summarized

**Purpose**  
Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

**Type**  
- Table (PostgreSQL)

**Schema**  
| Table | Columns (PK/FK/Constraints) | Indexes |
|-------|-----------------------------|---------|
| **ledger_transfer** | `transfer_id` INTEGER PK, `from_account` INTEGER FK→ledger_account.account_id NOT NULL, `to_account` INTEGER FK→ledger_account.account_id NOT NULL, `amount_cents` BIGINT NOT NULL, `booked_at` TIMESTAMP NOT NULL | `pk_ledger_transfer` (transfer_id PK), `uk_ledger_transfer` (UNIQUE (`from_account`, `to_account`, `booked_at`)), `idx_ledger_transfer_booked_at` (booked_at) |
| **ledger_account** | `account_id` INTEGER PK, `balance_cents` INTEGER | — |
| **ledger_category** | `category_id` INTEGER PK, `name` VARCHAR(50) NOT NULL | — |

**Reads**  
- `ledger_transfer` (all columns)  
- `ledger_account` (via FK columns `from_account`, `to_account`)  
- `ledger_category` (not used in this file)

**Writes**  
- `ledger_transfer` – **6,500** rows inserted (see routine logic)  
- `ledger_account` – **6,500** rows inserted (via FK inserts)  
- `ledger_category` – **0** rows inserted (no inserts in this file)

**Relationships**  
- `ledger_transfer.from_account → ledger_account.account_id`  
- `ledger_transfer.to_account → ledger_account.account_id`  

**Routine logic**  
- **INSERT** statements populate `ledger_transfer` with 6,500 rows; each row inserts a transfer between two accounts and records the amount and booking date.  
- No procedures, functions, or triggers are defined in this file.

**Dependencies**  
- `ledger_account` (referenced by foreign keys)  
- `ledger_category` (referenced but not used in this file)

---  

*End of summary.*

Part 2:  
> A SQL object that models financial ledger transfers between accounts, recording deposits and withdrawals with timestamps.
