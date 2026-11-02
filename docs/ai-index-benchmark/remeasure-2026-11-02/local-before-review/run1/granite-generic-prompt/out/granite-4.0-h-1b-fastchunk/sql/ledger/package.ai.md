### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T22:06:01Z
- T: 2026-11-01T22:51:23Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
### Purpose
The SQL file defines a `ledger_transfer` table for recording financial transfers between accounts, along with related DDL/DML statements for managing a ledger of financial transactions.

### Responsibilities
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

### Key Units
- **Table**: `ledger_transfer`
- **Views**: `ledger`

### Data Flow
- **Input**: No external input; the file generates data by inserting and updating the `ledger_transfer` table.
- **Output**: No direct output; the file modifies the state of the `ledger_transfer` table.

### Key Dependencies
- **External Dependencies**: None
- **Internal Collaborations**: None

### Cross-cutting
- **Patterns**: CRUD operations on a financial ledger table, updating account balances, and maintaining transaction records.

### Summary
The SQL file provides a structured way to manage financial transactions by defining a `ledger_transfer` table and related DDL/DML statements. It ensures data integrity through constraints and provides a clear, organized method for recording and managing financial transfers between accounts.
