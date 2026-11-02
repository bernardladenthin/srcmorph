### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T21:48:29Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Purpose**  
The `ledger_dump.sql` file contains a set of DDL/DML statements that define a series of tables and procedures related to a financial ledger system. The primary purpose is to model financial transactions and related ledger entries, allowing for the storage, retrieval, and manipulation of account balances, transaction records, and related metadata.

#### Type
- **DDL**: The file creates tables (`accounts`, `transactions`, `ledger_entries`, `transfer`, `transfer_log`) and a procedure (`get_ledger_balance`).
- **DML**: The file includes `INSERT` statements that populate the `accounts`, `transactions`, and `ledger_entries` tables with sample data.

#### Schema
- **Tables**:
  - `accounts`: Stores account identifiers and balances.
  - `transactions`: Stores individual transaction records with amounts and timestamps.
  - `ledger_entries`: Holds detailed ledger entries with reference to transactions and accounts.
  - `transfer`: Manages transfer transactions between accounts.
  - `transfer_log`: Tracks the history of transfers.
- **Procedures**:
  - `get_ledger_balance`: A stored procedure that calculates the balance of a specified account.

#### Reads
- The `get_ledger_balance` procedure reads from the `accounts` table to compute balances.
- The `INSERT` statements read from the `accounts`, `transactions`, and `ledger_entries` tables.

#### Writes
- The `INSERT` statements write to the `accounts`, `transactions`, `ledger_entries`, `transfer`, and `transfer_log` tables.
- The `get_ledger_balance` procedure reads from the `accounts` table.

#### Relationships
- **Tables**:
  - `accounts` ↔ `transactions`: One-to-many relationship (each account can have multiple transactions).
  - `transactions` ↔ `ledger_entries`: One-to-many relationship (each transaction can generate multiple ledger entries).
  - `ledger_entries` ↔ `transfer`: One-to-many relationship (each ledger entry can be associated with multiple transfers).
  - `transfer` ↔ `accounts`: Many-to-many relationship (a transfer can be between any two accounts).
  - `transfer_log` ↔ `transfer`: One-to-many relationship (each transfer can have multiple log entries).

#### Routine logic
- **Procedure (`get_ledger_balance`)**:
  - Accepts an `account_id` as input.
  - Retrieves the balance from the `accounts` table for the specified account.
  - Returns the balance as the result.

#### Dependencies
- The `get_ledger_balance` procedure depends on the `accounts` table for retrieving account balances.

#### Notes
- The `INSERT` statements populate the tables with sample data, which can be used for testing or demonstration purposes.
- The `get_ledger_balance` procedure is a useful utility for retrieving account balances based on the stored ledger entries.

This schema and its associated DDL/DML statements provide a basic framework for managing financial transactions and ledger entries in a structured manner.
