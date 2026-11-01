### ledger_dump.sql
- H: 1.0
- C: 0A26F72A
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T23:20:30Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 60; VALUES rows (approx): 30000; tables: 3; views: 1; indexes: 2; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

> A financial ledger database that records transactions, accounts, and balances for accounting and reporting.

#### Purpose
- Models a complete accounting ledger system.
- Stores financial transactions, account structures, and balance calculations.

#### Type
- **SQL file**: DDL (CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE FUNCTION, CREATE TRIGGER).

#### Schema
**Table: `transactions`**
- `id` `INT` `PRIMARY KEY`
- `account_id` `INT` `NOT NULL` `REFERENCES accounts(id)`
- `amount` `DECIMAL(12,2)` `NOT NULL`
- `date` `TIMESTAMP` `NOT NULL DEFAULT CURRENT_TIMESTAMP`
- `description` `VARCHAR(255)`
- `created_at` `TIMESTAMP` `DEFAULT CURRENT_TIMESTAMP`

**Table: `accounts`**
- `id` `INT` `PRIMARY KEY`
- `name` `VARCHAR(100)` `NOT NULL`
- `type` `VARCHAR(20)` `NOT NULL` `CHECK (type IN ('asset','liability','equity','income','expense'))`
- `balance` `DECIMAL(12,2)` `DEFAULT 0`

**View: `account_balances`**
- `account_id` `INT` `PRIMARY KEY`
- `type` `VARCHAR(20)`
- `balance` `DECIMAL(12,2)`

**Procedure: `update_account_balance`**
- Parameters: `p_account_id INT`, `p_amount DECIMAL(12,2)`
- Updates `accounts.balance` by adding `p_amount`.

**Function: `get_account_balance`**
- Parameters: `p_account_id INT`
- Returns `balance` from `accounts` where `id = p_account_id`.

**Trigger: `audit_transaction`**
- Fires on `INSERT, UPDATE, DELETE` of `transactions`.
- Calls `audit_log_transaction` with transaction details.

#### Reads
- `transactions` read for balance calculations.
- `accounts` read for account details and balance updates.

#### Writes
- `transactions` inserted (`INSERT`) – exact rows not specified.
- `accounts.balance` updated via `update_account_balance`.
- `audit_log` table (implicitly created) written by trigger.

#### Relationships
- `transactions.account_id -> accounts.id`
- Trigger on `transactions` referencing `accounts`.

#### Routine logic
- **`update_account_balance`**: Adds `p_amount` to `accounts.balance` for given `account_id`.
- **`get_account_balance`**: Retrieves current `balance` for an account.
- **Trigger**: On transaction changes, logs to `audit_log` (not shown).

#### Dependencies
- `accounts` table referenced by `transactions` and `account_balances`.
- `audit_log` table (assumed) created by trigger.
