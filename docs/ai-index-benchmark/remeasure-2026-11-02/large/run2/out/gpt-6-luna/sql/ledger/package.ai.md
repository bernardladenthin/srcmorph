### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:23Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Stores customer-owned bank accounts, account holds, and monetary transfers, and summarizes daily transfer volume.

#### Purpose
- Defines and populates a ledger for customers, accounts, transfers, and account holds.
- Provides a daily aggregate of transfer amounts by booking date.

#### Responsibilities
- Customer and account records, linked by `ledger_customer` and `ledger_account`.
- Transfers between accounts and holds associated with accounts.
- Daily transfer-volume reporting and indexes for account, booking-date, and hold-account lookups.

#### Key units
- `ledger_customer` — customer identity, display name, and country code.
- `ledger_account` — customer-linked bank account with a unique IBAN and opening date.
- `ledger_transfer` — transfer endpoints, positive amount in cents, and booking timestamp.
- `ledger_hold` — account-linked hold reason.
- `ledger_daily_volume` — aggregates transfer amounts by the date of `booked_at`.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes on transfer source account, transfer booking time, and hold account, respectively.

#### Data flow
- Customer records support account records; accounts are referenced by transfer source/destination and account holds.
- `ledger_daily_volume` reads `ledger_transfer.booked_at` and `amount_cents`, groups by booking date, and reports summed cents.
- The SQL file seeds four tables with approximately 30,050 rows across 61 `INSERT` statements.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` depends on `ledger_transfer`.

#### Cross-cutting
- Primary keys identify records; foreign keys express customer/account relationships.
- `ledger_account.iban` is unique; `ledger_transfer.amount_cents` must be positive.
- No functions, procedures, triggers, or sequences are defined.
