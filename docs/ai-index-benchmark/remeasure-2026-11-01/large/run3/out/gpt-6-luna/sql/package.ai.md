### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:13:02Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Stores customer-owned bank accounts, account-to-account transfers, and holds, with daily transfer-volume reporting.

#### Purpose
- Defines the ledger schema and seed records for customers, accounts, transfers, and holds.
- Provides a daily aggregate of transfer amounts.

#### Responsibilities
- **Customer and account records:** Link accounts to customers and enforce unique IBANs.
- **Transfers and reporting:** Store positive-cent transfers between accounts and aggregate amounts by booking date.
- **Account holds:** Store hold reasons associated with ledger accounts.
- **Lookup support:** Index transfer source accounts and booking timestamps, and hold account IDs.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with IBANs and opening dates.
- `ledger_transfer` — source and destination accounts, positive amount in cents, and booking timestamp.
- `ledger_hold` — account-linked hold reason.
- `ledger_daily_volume` — daily sum of transfer amounts derived from `booked_at`.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer and hold lookups.

#### Data flow
- Seeded customers and accounts provide the referenced records for transfers and holds.
- `ledger_daily_volume` groups `ledger_transfer` rows by the date of `booked_at` and sums `amount_cents`.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account`, `ledger_transfer.to_account`, and `ledger_hold.account_id` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Cross-cutting
- Customer, account, transfer, and hold records use integer primary keys; required fields are `NOT NULL`.
- Transfer amounts must be positive; account IBANs are unique.
