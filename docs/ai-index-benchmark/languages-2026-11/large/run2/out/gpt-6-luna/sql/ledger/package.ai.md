### sql/ledger
- H: 1.0
- C: 0AA8FF04
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:21Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Stores customer-owned bank accounts and booked transfers, with daily aggregation of transfer volume.

#### Purpose
- Defines and seeds a ledger schema for customers, accounts, and account-to-account transfers.
- Provides a daily view of booked transfer amounts.

#### Responsibilities
- **Customer and account records:** Stores customer identity and country, account ownership, IBAN, and opening date.
- **Transfer records and reporting:** Stores positive-amount transfers with booking timestamps; aggregates amounts by booking date.
- **Indexes and seed data:** Adds indexes for transfer source account and booking time; includes bulk INSERT data across three tables.

#### Key units
- `ledger_customer` — customer ID, display name, and country code.
- `ledger_account` — account ID, owning customer, unique IBAN, and opening date.
- `ledger_transfer` — source and destination accounts, positive amount in cents, and booking timestamp.
- `ledger_daily_volume` — daily sum of transfer amounts, grouping transfers by the date cast from `booked_at`.
- `idx_transfer_from` — index on `ledger_transfer(from_account)`.
- `idx_transfer_booked` — index on `ledger_transfer(booked_at)`.

#### Data flow
- Seeded customer records associate with accounts through `ledger_account.customer_id`.
- Transfers reference accounts as both `from_account` and `to_account`.
- `ledger_daily_volume` reads transfer timestamps and amounts, groups by booking date, and returns `day` and `volume_cents`.
- Insert counts and table attribution are incomplete or uncertain for some visible data segments; transfer ID ranges overlap between segments.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `ledger_transfer.to_account` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Primary keys identify customers, accounts, and transfers; account IBANs are unique.
- Required fields use `NOT NULL`; transfer amounts are constrained to be greater than zero.
- The SQL script contains 60 INSERT statements and approximately 30,000 value rows.
