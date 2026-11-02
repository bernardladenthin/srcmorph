### sql/ledger
- H: 1.0
- C: 0AA8FF04
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T23:18:58Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
**package.ai.md**

### sql/ledger
- **Purpose**: The `ledger_dump.sql` file contains a set of DDL/DML statements that define a series of tables and procedures related to a financial ledger system. The primary purpose is to model financial transactions and related ledger entries, allowing for the storage, retrieval, and manipulation of account balances, transaction records, and related metadata.
- **Type**: DDL (Data Definition Language) and DML (Data Manipulation Language) statements.
- **Schema**: Includes tables (`accounts`, `transactions`, `ledger_entries`, `transfer`, `transfer_log`) and a procedure (`get_ledger_balance`).
- **Reads**: `get_ledger_balance` procedure reads from `accounts` table.
- **Writes**: `INSERT` statements write to `accounts`, `transactions`, `ledger_entries`, `transfer`, and `transfer_log` tables.
- **Relationships**:
  - `accounts` ↔ `transactions`: One-to-many
  - `transactions` ↔ `ledger_entries`: One-to-many
  - `ledger_entries` ↔ `transfer`: One-to-many
  - `transfer` ↔ `accounts`: Many-to-many
  - `transfer_log` ↔ `transfer`: One-to-many
- **Routine Logic**: `get_ledger_balance` procedure reads from `accounts` table.
- **Dependencies**: `get_ledger_balance` depends on `accounts` table.

This schema and its associated DDL/DML statements provide a basic framework for managing financial transactions and ledger entries in a structured manner.
