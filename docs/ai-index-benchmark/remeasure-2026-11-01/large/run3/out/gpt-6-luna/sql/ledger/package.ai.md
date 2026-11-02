### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:12:58Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Defines customer-owned bank accounts, money transfers, and account holds, with daily transfer-volume reporting and seeded ledger records.

#### Purpose
- Describes the relational structure and seed data for customers, accounts, transfers, and account holds.
- Provides a daily aggregate view of transfer amounts.

#### Responsibilities
- **Ledger entities:** Store customers and their bank accounts, with account ownership and unique IBANs.
- **Transfer records and reporting:** Store account-to-account transfers and expose daily summed volume.
- **Account holds:** Record hold reasons associated with accounts.
- **Seed data and lookup support:** Populate the ledger tables and index transfer and hold lookup fields.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with IBAN and opening date.
- `ledger_transfer` — source and destination accounts, positive amount in cents, and booking timestamp.
- `ledger_hold` — account-linked hold reason.
- `ledger_daily_volume` — daily sum of transfer amounts, grouped by date derived from `booked_at`.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes on transfer source account, transfer booking timestamp, and hold account.

#### Data flow
- Seed records populate customers and accounts; transfers and holds reference accounts.
- `ledger_daily_volume` reads `ledger_transfer.booked_at` and `amount_cents`, casts booking timestamps to dates, and sums amounts by day.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` depends on `ledger_transfer`.

#### Cross-cutting
- Transfer amounts are constrained to be positive; customer, account, transfer, and hold records use integer primary keys.
- Account IBANs are unique; required fields are marked NOT NULL.
