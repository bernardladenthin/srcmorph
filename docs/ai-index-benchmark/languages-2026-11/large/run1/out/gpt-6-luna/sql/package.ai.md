### sql
- H: 1.0
- C: 479593E6
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:38:18Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Models customer-owned bank accounts and account-to-account transfers, with daily totals for booked transfer amounts.

#### Purpose
- Defines ledger records for customers, accounts, and transfers.
- Supports daily transfer-volume reporting and bulk record loading.

#### Responsibilities
- **Ledger records:** `ledger_customer`, `ledger_account`, and `ledger_transfer` represent customers, their accounts, and transfers.
- **Transfer reporting:** `ledger_daily_volume` aggregates booked transfer amounts by date.
- **Data loading and lookup support:** Multi-row inserts populate the tables; indexes support transfer queries by source account and booking timestamp.

#### Key units
- `ledger_customer` — customer identity, display name, and country code.
- `ledger_account` — customer-owned account with IBAN and opening date.
- `ledger_transfer` — transfer between accounts with a positive amount in cents and booking timestamp.
- `ledger_daily_volume` — daily sum of transfer amounts in cents.
- `idx_transfer_from` — index on `ledger_transfer(from_account)`.
- `idx_transfer_booked` — index on `ledger_transfer(booked_at)`.

#### Data flow
- Customer records are inserted into `ledger_customer`; accounts link to customers through `customer_id`.
- Transfers link source and destination accounts through `from_account` and `to_account`.
- `ledger_daily_volume` groups transfers by the date portion of `booked_at` and sums `amount_cents`.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account` reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer.booked_at` and `amount_cents`.

#### Cross-cutting
- Primary keys identify customers, accounts, and transfers; account IBANs are unique.
- Required fields are `NOT NULL`; transfer amounts must be positive.
