### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T22:06:01Z
- T: 2026-11-01T23:49:21Z
- G: 
- A: 0.0.0
- X: file
- P: 9F454DD1
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 99 of 99 chunks summarized (2 part summaries cut at the output budget)

**Purpose**  
The file `ledger_dump.sql` contains a set of DDL/DML statements that model a financial ledger system, including creating tables, defining indexes, and inserting sample data for accounts and ledger transfers, along with a view and a procedure for managing the ledger.

**Type**  
- **DDL**: Table definitions, indexes, and procedures/functions.  
- **DML**: Data insertion statements (INSERT).

**Schema**  
| Table | Columns | Type | Constraints |
|-------|---------|------|-------------|
| **accounts** | `account_id` (INT) | PRIMARY KEY | `NOT NULL` |
| | `name` (VARCHAR) | NOT NULL | |
| **ledger** | `transfer_id` (INT) | PRIMARY KEY | `NOT NULL` |
| | `from_account` (INT) | FK → `accounts.id` | |
| | `to_account` (INT) | FK → `accounts.id` | |
| | `amount_cents` (INT) | NOT NULL | |
| | `booked_at` (DATE) | NOT NULL | |
| **ledger_transfer** | `transfer_id` (INT) | PRIMARY KEY | `NOT NULL` |
| | `from_account` (INT) | FK → `accounts.id` | |
| | `to_account` (INT) | FK → `accounts.id` | |
| | `amount_cents` (INT) | NOT NULL | |
| | `booked_at` (DATE) | NOT NULL | |

**Reads**  
- The DDL statements read the schema definition (no data sources).

**Writes**  
- **Tables**: `accounts` (INSERTs) and `ledger` (INSERTs).  
- **View**: `view_ledger_summary` (joins `ledger` and `ledger_transfer` on `transfer_id`).  
- **Procedure**: `report_ledger` – returns ledger details for a given `account_id`.  
- **Function**: `get_total_amount` – calculates total amount from the ledger.

**Relationships**  
- `ledger.from_account` → `accounts.account_id` (FK)  
- `ledger.to_account` → `accounts.account_id` (FK)  
- `ledger_transfer.from_account` → `accounts.account_id` (FK)  
- `ledger_transfer.to_account` → `accounts.account_id` (FK)

**Routine logic**  
- `create_ledger` procedure inserts a new ledger transfer for each row in `accounts`.  
- The `report_ledger` procedure fetches all ledger rows for a given `account_id`.  
- The `get_total_amount` function sums `amount_cents` from `ledger` for a given `account_id`.

**Dependencies**  
- No external tables/views referenced.

**Summary**  
The file provides the DDL for a single-column‑named table (`ledger_dump`) that stores daily ledger entries with columns for an identifier, date, transaction ID, amount, and description. It does not read from, write to, or reference any other objects.
