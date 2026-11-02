### sql/ledger
- H: 1.0
- C: 0AA8FF04
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T22:06:13Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A financial ledger database that records transactions, accounts, and balances for accounting and reporting.

#### Purpose
- Models a complete accounting ledger system.
- Stores financial transactions, account structures, and balance calculations.

#### Responsibilities
- **Transactions**: Records individual financial entries with account, amount, date, and description.
- **Accounts**: Manages account structures, types, and balances.
- **Balances**: Provides current balances for accounts.
- **Auditing**: Logs changes to transactions for accountability.

#### Key units
- **`transactions` table**: Stores transaction details with `id`, `account_id`, `amount`, `date`, and `description`.
- **`accounts` table**: Manages account information with `id`, `name`, `type`, and `balance`.
- **`account_balances` view**: Provides aggregated balances for accounts.
- **`update_account_balance` procedure**: Updates account balances by adding amounts.
- **`get_account_balance` function**: Retrieves current balances for accounts.
- **Trigger**: `audit_transaction` logs transaction changes to an audit log.

#### Data flow
- **Reads**: `transactions` and `accounts` tables are read for balance calculations and account details.
- **Writes**: `transactions` are inserted, updating account balances via `update_account_balance` and logging changes via the trigger.

#### Dependencies
- Relies on the `accounts` table for account details and balance updates.
- Assumes an `audit_log` table is created by the trigger for transaction auditing.

#### Cross-cutting
- Uses standard SQL DDL practices for schema definition and data integrity.
- Implements auditing through a trigger mechanism.
