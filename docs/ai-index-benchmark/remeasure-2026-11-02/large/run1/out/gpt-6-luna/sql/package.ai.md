### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:36:51Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Maintains customer bank accounts and inter-account transfers, supports fraud-review holds, and summarizes transfer volumes by booking date.

#### Purpose
- Defines and populates customer, account, transfer, and hold records.
- Provides daily transfer totals through `ledger_daily_volume`.

#### Responsibilities
- Customer and account data: `ledger_customer` identifies customers; `ledger_account` links accounts to customers and stores IBANs and opening dates.
- Transfer and fraud-review records: `ledger_transfer` tracks account-to-account payments; `ledger_hold` records account holds and reasons.
- Daily aggregation: groups transfer amounts by booking date.
- Bulk data loading: includes 61 insert statements across four tables.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with unique IBANs and opening dates.
- `ledger_transfer` — source and destination accounts, positive amounts in cents, and booking timestamps.
- `ledger_hold` — account-linked fraud-review holds and reasons.
- `ledger_daily_volume` — sums transfer amounts by date.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer source, booking time, and held account.

#### Data flow
- Customers are linked to accounts; transfers and holds reference accounts.
- `ledger_daily_volume` casts `ledger_transfer.booked_at` to a date and sums `amount_cents` per day.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account`, `ledger_transfer.to_account`, and `ledger_hold.account_id` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Primary and foreign keys enforce row identity and customer/account relationships.
- Account IBANs are unique; transfer amounts must be positive.
