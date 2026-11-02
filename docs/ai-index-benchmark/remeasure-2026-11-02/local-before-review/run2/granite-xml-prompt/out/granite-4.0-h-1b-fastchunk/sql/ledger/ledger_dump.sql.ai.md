### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T22:06:01Z
- T: 2026-11-01T23:04:18Z
- G: 
- A: 0.0.0
- X: file
- P: 8EC66CCF
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 95 of 95 chunks summarized (4 part summaries cut at the output budget)

**Overall Summary**

- **Purpose**: The SQL file defines a `ledger_transfer` table for recording financial transfers between accounts, along with related DDL/DML statements for managing a ledger of financial transactions.  
- **Type**: A collection of DDL (CREATE TABLE, CREATE INDEX) and DML (INSERT, UPDATE, SELECT) statements modeling a financial ledger.  
- **Schema**:  
  - **Table**: `ledger_transfer`  
    - Columns: `transfer_id` (INT, PK), `from_account` (INT, FK to `accounts(id)`), `to_account` (INT, FK to `accounts(id)`), `amount_cents` (INT, UNIQUE), `booked_at` (TIMESTAMP, UNIQUE)  
    - Indexes: `idx_amount_cents`, `idx_booked_at`  
  - **Views**: `ledger` (table)  
  - **Functions/Procedures**: None  
  - **Triggers**: None  
  - **Types**: None  
  - **Alter Table**: None  
  - **Sequences**: None  
  - **Extensions**: None  
  - **Schemas/Databases**: None  
- **Reads**: The file reads from the `ledger_transfer` table (SELECT statements) to retrieve transfer details, account balances, and date ranges.  
- **Writes**: The file writes to the `ledger_transfer` table (INSERT statements) to add new transfers, update balances, and modify booking timestamps.  
- **Relationships**:  
  - `from_account` → `accounts(id)`  
  - `to_account` → `accounts(id)`  
- **Routine Logic**: The file performs CRUD operations on the `ledger_transfer` table, manipulating transaction records and updating account balances.  
- **Dependencies**: No external tables, views, or sequences referenced.
