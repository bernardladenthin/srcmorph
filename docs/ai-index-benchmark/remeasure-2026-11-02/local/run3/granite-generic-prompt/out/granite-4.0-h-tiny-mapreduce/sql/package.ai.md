### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T02:15:19Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A SQL object that models financial ledger transfers between accounts, recording deposits and withdrawals with timestamps.

#### Purpose
- Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

#### Responsibilities
- **ledger_transfer** table: stores individual transfers with `transfer_id`, `from_account`, `to_account`, `amount_cents`, and `booked_at`.
- **ledger_account** table: maintains account balances (`balance_cents`) referenced by transfers.
- **ledger_category** table: (not used in this file) provides category information for transfers.
- Routine logic: inserts 6,500 rows into `ledger_transfer` representing transfers between accounts, updating balances accordingly.

#### Key units
- **ledger_transfer** (`transfer_id` PK, `from_account` FK, `to_account` FK, `amount_cents`, `booked_at`): primary table for transfers.
- **ledger_account** (`account_id` PK, `balance_cents`): stores account balances used in transfers.
- **ledger_category** (`category_id` PK, `name`): optional categorization of transfers (not utilized here).

#### Data flow
1. **INSERT** statements populate `ledger_transfer` with 6,500 rows, each linking two accounts and recording an amount and date.
2. Corresponding `ledger_account` rows are inserted/updated to reflect the balance changes.
3. No reads or writes to `ledger_category` occur in this file.

#### Dependencies
- **ledger_account**: referenced via foreign keys (`from_account`, `to_account`).
- **ledger_category**: referenced but not used in this file.

#### Cross-cutting
- Uses PostgreSQL syntax with standard data types (`INTEGER`, `BIGINT`, `TIMESTAMP`).
- No error handling or concurrency mechanisms are defined in this file.
