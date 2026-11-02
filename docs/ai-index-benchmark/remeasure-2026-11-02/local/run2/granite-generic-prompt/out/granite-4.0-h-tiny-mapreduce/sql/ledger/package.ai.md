### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T01:33:27Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A SQL object that models financial ledger transfers between accounts, recording deposits and withdrawals with timestamps.

#### Purpose
- Models financial ledger transfers between accounts, tracks account balances, and supports CRUD operations on transfers.

#### Responsibilities
- **ledger_transfer** table: stores individual transfers with `transfer_id`, `from_account`, `to_account`, `amount_cents`, and `booked_at`.
- **ledger_account** table: maintains account balances (`account_id`, `balance_cents`).
- **ledger_category** table: (not used in this file) would categorize transfers.
- **Insert routine**: populates `ledger_transfer` with 6,500 rows, inserting transfers between accounts and recording amounts and dates.

#### Key units
- **ledger_transfer** (`transfer_id` PK, `from_account` FK, `to_account` FK, `amount_cents`, `booked_at`): stores transfer records.
- **ledger_account** (`account_id` PK, `balance_cents`): stores account balances.
- **ledger_category** (`category_id` PK, `name`): categorizes transfers (unused).

#### Data flow
1. **Insert routine** reads `ledger_account` to validate `from_account` and `to_account`.
2. Inserts 6,500 rows into `ledger_transfer`, linking accounts and recording amounts and dates.
3. Reads from `ledger_account` for balance checks during inserts.

#### Dependencies
- `ledger_account` (FK references)
- `ledger_category` (referenced but not used)

#### Cross-cutting
- Uses PostgreSQL syntax and conventions.
- No error handling or concurrency notes present.
