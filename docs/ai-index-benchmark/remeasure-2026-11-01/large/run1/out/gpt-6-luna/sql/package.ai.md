### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:23Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Maintains customer bank accounts and transfer records, supports fraud-review holds, and reports transfer totals by day.

#### Purpose
- Defines ledger data for customers, accounts, transfers, and account holds.
- Provides a daily transfer-volume view and seed data.

#### Responsibilities
- **Customer and account records:** Store customer names and countries, and link accounts with unique IBANs and opening dates.
- **Transfer capture and reporting:** Record positive-amount transfers between accounts and aggregate their amounts by booking date.
- **Fraud review:** Associate accounts with hold reasons.

#### Key units
- `ledger_customer` — customer identity and country data.
- `ledger_account` — customer-linked account details and unique IBANs.
- `ledger_transfer` — source and destination accounts, positive amounts in cents, and booking timestamps.
- `ledger_hold` — account-level hold reasons.
- `ledger_daily_volume` — daily totals derived from transfer amounts.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer source, booking-time, and held-account lookups.

#### Data flow
- Seed inserts populate customers and accounts, followed by transfers and holds.
- `ledger_daily_volume` groups transfer amounts by booking date and returns daily totals.
- An additional timestamped value fragment has no specified target table or column mapping.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Cross-cutting
- Primary and foreign keys connect ledger entities; account IBANs are unique.
- `ledger_transfer.amount_cents` must be greater than zero.
