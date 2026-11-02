### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T01:54:31Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A SQL object that models financial ledger transfers between accounts, recording deposits and withdrawals with timestamps.

## Purpose
- Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

## Responsibilities
- **ledger_transfer** table: stores individual transfers with `transfer_id`, `from_account`, `to_account`, `amount_cents`, and `booked_at`.
- **ledger_account** table: maintains account balances (`balance_cents`) referenced by transfers.
- **ledger_category** table: (not used in this file) provides category metadata for transfers.
- Routine logic: inserts 6,500 rows into `ledger_transfer` representing deposits/withdrawals between accounts.

## Key units
- **ledger_transfer** (`transfer_id` PK, `from_account` FK, `to_account` FK, `amount_cents`, `booked_at`): primary table for ledger entries.
- **ledger_account** (`account_id` PK, `balance_cents`): stores current balance for each account.
- **ledger_category** (`category_id` PK, `name`): optional category metadata (unused in this file).

## Data flow
1. **INSERT** statements populate `ledger_transfer` with 6,500 rows, each linking two accounts and recording an amount and date.
2. Foreign keys enforce relationships:
   - `ledger_transfer.from_account → ledger_account.account_id`
   - `ledger_transfer.to_account → ledger_account.account_id`
3. Reads of `ledger_transfer` and `ledger_account` enable balance calculations and transfer queries.

## Dependencies
- **ledger_account**: referenced by foreign keys in `ledger_transfer`.
- **ledger_category**: referenced but not used in this file.

## Cross-cutting
- Uses PostgreSQL syntax and conventions.
- No procedures, functions, or triggers defined in this file.
