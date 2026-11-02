### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:36:46Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Maintains customer bank-account records, account-to-account transfers, fraud-review holds, and date-based transfer-volume summaries.

#### Purpose
- Stores customer, account, transfer, and account-hold data, with bulk inserts across the ledger tables.
- Exposes daily transfer totals through `ledger_daily_volume`.

#### Responsibilities
- Customer and account records: `ledger_customer` identifies customers; `ledger_account` associates accounts with customers and records IBANs and opening dates.
- Transfer tracking: `ledger_transfer` records source and destination accounts, positive amounts in cents, and booking timestamps.
- Fraud review: `ledger_hold` associates accounts with hold reasons.
- Daily aggregation: `ledger_daily_volume` groups transfer amounts by booking date.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with unique IBANs and opening dates.
- `ledger_transfer` — inter-account transfer details, including amount and booking time.
- `ledger_hold` — account-linked fraud-review holds and reasons.
- `ledger_daily_volume` — date-level sums of transfer amounts.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes on transfer source account, transfer booking time, and held account.

#### Data flow
- Customer records relate to accounts; transfers and holds reference accounts.
- `ledger_daily_volume` casts `ledger_transfer.booked_at` to a date and sums `amount_cents` per day.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Primary keys identify rows; foreign keys encode customer/account relationships.
- Account IBANs are unique; transfer amounts must be positive.
- The SQL file contains 61 insert statements and approximately 30,050 value rows across four tables.
