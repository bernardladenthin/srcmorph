### sql/ledger
- H: 1.0
- C: 0AA8FF04
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:26:08Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Defines a customer-linked banking ledger for accounts and positive-value transfers, with daily aggregation of booked transfer volume.

#### Purpose
- Stores customer, bank-account, and account-to-account transfer records.
- Provides a daily view of transfer amounts and includes seed-data fragments.

#### Responsibilities
- **Customer and account records:** `ledger_customer` stores names and country codes; `ledger_account` associates accounts and unique IBANs with customers.
- **Transfer tracking:** `ledger_transfer` records source and destination accounts, positive amounts in cents, and booking timestamps.
- **Daily reporting:** `ledger_daily_volume` aggregates transfer amounts by booking date.
- **Seed data:** Includes customer, account, and transfer `INSERT` fragments; some transfer and account batches are incomplete or reported in separate parts.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — account identifiers, customer links, unique IBANs, and opening dates.
- `ledger_transfer` — transfer endpoints, amount in cents, and booking timestamp.
- `ledger_daily_volume` — daily sum of booked transfer amounts.
- `idx_transfer_from` — index on `ledger_transfer.from_account`.
- `idx_transfer_booked` — index on `ledger_transfer.booked_at`.

#### Data flow
- Customer rows are referenced by `ledger_account.customer_id`.
- Transfers reference accounts through `from_account` and `to_account`.
- `ledger_daily_volume` reads transfer timestamps and amounts, groups by the date of booking, and reports summed cents.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Primary keys identify customers, accounts, and transfers; account IBANs are unique.
- Required fields use `NOT NULL`; transfer amounts are constrained to be greater than zero.
- Transfer indexes support lookup by source account and booking timestamp.
