### sql
- H: 1.0
- C: 479593E6
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T23:35:43Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A financial ledger database that records transactions, accounts, and balances for accounting and reporting.

#### Purpose
- Models a complete accounting ledger system.
- Stores financial transactions, account structures, and balance calculations.

#### Responsibilities
- **Transactions Table**: Records each financial transaction with `id`, `account_id`, `amount`, `date`, and `description`.
- **Accounts Table**: Manages account structures with `id`, `name`, `type`, and `balance`.
- **Account Balances View**: Provides aggregated balances for each account.
- **Update Balance Procedure**: Adjusts account balances by adding amounts.
- **Balance Retrieval Function**: Retrieves current balances for accounts.
- **Audit Trigger**: Logs transaction changes to an audit table.

#### Key Units
- **`transactions` Table**: Stores individual transactions with foreign key `account_id` referencing `accounts.id`.
- **`accounts` Table**: Manages account details and current balances.
- **`account_balances` View**: Aggregates balances for reporting.
- **`update_account_balance` Procedure**: Updates account balances by adding specified amounts.
- **`get_account_balance` Function**: Retrieves current balance for a given account.
- **`audit_transaction` Trigger**: Audits transaction changes by calling `audit_log_transaction`.

#### Data Flow
Inputs from `transactions` and `accounts` tables flow through the system, with `update_account_balance` modifying balances and `get_account_balance` retrieving them. The trigger on `transactions` ensures audit logs are maintained.

#### Dependencies
- Relies on the `accounts` table for account information and balance updates.
- Assumes an `audit_log` table for transaction auditing.

#### Cross-cutting
- Uses standard SQL DDL constructs.
- Implements procedural logic via a procedure and a trigger.
- No external dependencies beyond standard SQL features.
