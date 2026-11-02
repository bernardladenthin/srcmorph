### sql
- H: 1.0
- C: 479593E6
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:28Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Records customer-owned bank accounts and booked account transfers, and reports transfer volume by booking date.

#### Purpose
- Defines the ledger schema and seed data for customers, accounts, and transfers.
- Provides a daily aggregate of booked transfer amounts.

#### Responsibilities
- **Customer and account records:** Stores customer identity and country, account ownership, unique IBANs, and account opening dates.
- **Transfers and reporting:** Stores positive-amount transfers with booking timestamps; aggregates amounts by booking date.
- **Indexes and seed data:** Indexes transfer source accounts and booking times; seeds the ledger tables with bulk inserts.

#### Key units
- `ledger_customer` — customer ID, display name, and country code.
- `ledger_account` — account ID, customer owner, unique IBAN, and opening date.
- `ledger_transfer` — source and destination accounts, positive amount in cents, and booking timestamp.
- `ledger_daily_volume` — daily transfer-volume view returning `day` and `volume_cents`.
- `idx_transfer_from`, `idx_transfer_booked` — indexes on transfer source account and booking timestamp.

#### Data flow
- `ledger_customer` records are associated with accounts through `ledger_account.customer_id`.
- `ledger_transfer` records reference accounts as `from_account` and `to_account`.
- `ledger_daily_volume` groups transfer amounts by the date of `booked_at`.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `ledger_transfer.to_account` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Cross-cutting
- Primary keys identify customers, accounts, and transfers; account IBANs are unique.
- Required fields are `NOT NULL`; transfer amounts are constrained to be greater than zero.
- Seed data comprises 60 `INSERT` statements and approximately 30,000 value rows; some segment attribution and transfer ID ranges are uncertain or overlapping.
