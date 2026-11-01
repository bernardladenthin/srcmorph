### sql/ledger
- H: 1.0
- C: 0AA8FF04
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:38:14Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Stores customer-owned bank accounts and timestamped transfers, with a daily view of transfer volume.

#### Purpose
- Defines the core customer, account, and transfer records for a banking ledger.
- Provides a daily aggregation of booked transfer amounts and loads records through bulk inserts.

#### Responsibilities
- **Ledger schema:** `ledger_customer`, `ledger_account`, and `ledger_transfer` tables model customers, their accounts, and transfers between accounts.
- **Transfer reporting:** `ledger_daily_volume` groups transfer amounts by booking date.
- **Data loading:** Multi-row inserts populate the tables; transfer batches cover overlapping ranges, so no combined transfer total is stated.

#### Key units
- `ledger_customer` — customer identity, display name, and country code.
- `ledger_account` — account ownership, IBAN, and opening date.
- `ledger_transfer` — source and destination accounts, positive amount in cents, and booking timestamp.
- `ledger_daily_volume` — daily sum of booked transfer amounts in cents.
- `idx_transfer_from` — index on `ledger_transfer(from_account)`.
- `idx_transfer_booked` — index on `ledger_transfer(booked_at)`.

#### Data flow
- Customer records are inserted into `ledger_customer`; account records reference customers through `customer_id`.
- Transfers reference accounts through `from_account` and `to_account`.
- `ledger_daily_volume` casts `booked_at` to a date and sums `amount_cents` per day.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer.booked_at` and `amount_cents`.

#### Cross-cutting
- Primary keys identify customers, accounts, and transfers; accounts also enforce unique IBANs.
- Transfer amounts must be positive; required fields are marked `NOT NULL`.
- Indexes support lookups by transfer source account and booking timestamp.
