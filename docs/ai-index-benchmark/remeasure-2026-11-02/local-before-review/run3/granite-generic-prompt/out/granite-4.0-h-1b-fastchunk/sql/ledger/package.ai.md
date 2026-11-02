### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T22:06:01Z
- T: 2026-11-02T00:21:26Z
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
- **Input**: No external input; the file generates data through DDL/DML statements.
- **Output**: Generates data by inserting records into the `ledger_transfer` table and updating existing records.

### Key Dependencies
- **External Modules/Tables**: None
- **External Packages**: None

### Cross-cutting
- **Shared Base Types/Interfaces**: None
- **Common Exception/Error Handling**: None
- **Threading/Concurrency Notes**: None
- **Configuration**: None
