### sql
- H: 1.0
- C: 479593E6
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:26:13Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Stores customer-linked bank accounts and positive-value transfers, with daily totals of booked transfer volume.

#### Purpose
- Defines the ledger’s customer, account, and transfer records.
- Supports daily reporting of transfer amounts and includes seed-data fragments.

#### Responsibilities
- **Customer and account data:** Stores customer names and country codes, plus linked accounts with unique IBANs and opening dates.
- **Transfer tracking and reporting:** Records account-to-account transfers and aggregates their amounts by booking date.
- **Seed data:** Provides customer, account, and transfer `INSERT` fragments; some batches are incomplete or split across parts.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — account identifiers, customer links, unique IBANs, and opening dates.
- `ledger_transfer` — source and destination accounts, positive amounts in cents, and booking timestamps.
- `ledger_daily_volume` — daily sum of booked transfer amounts.
- `idx_transfer_from` — index on transfer source accounts.
- `idx_transfer_booked` — index on transfer booking timestamps.

#### Data flow
- `ledger_account.customer_id` links accounts to customers.
- `ledger_transfer.from_account` and `to_account` link transfers to accounts.
- `ledger_daily_volume` groups transfer amounts by booking date and reports summed cents.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Customer, account, and transfer records use primary keys; account IBANs are unique.
- Required fields use `NOT NULL`; transfer amounts must be greater than zero.
- Transfer indexes support queries by source account and booking timestamp.
