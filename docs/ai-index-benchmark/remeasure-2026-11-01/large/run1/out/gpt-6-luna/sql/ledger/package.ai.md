### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:19Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Stores customer and bank-account records, captures account-to-account transfers, flags accounts for fraud review, and aggregates transfer volume by day.

#### Purpose
- Provides a ledger data model for customers, accounts, transfers, and account holds.
- Defines a daily transfer-volume view and includes substantial seed data.

#### Responsibilities
- **Customer and account records:** `ledger_customer` stores customer names and countries; `ledger_account` links accounts to customers and stores IBANs and opening dates.
- **Transfers and reporting:** `ledger_transfer` records source and destination accounts, positive amounts in cents, and booking timestamps; `ledger_daily_volume` aggregates amounts by booking date.
- **Fraud review:** `ledger_hold` associates accounts with hold reasons.

#### Key units
- `ledger_customer` — customer identity and country data.
- `ledger_account` — account details linked to a customer, with unique IBANs.
- `ledger_transfer` — timestamped, positive-value transfers between accounts.
- `ledger_hold` — account-level fraud-review hold reasons.
- `ledger_daily_volume` — daily sum of transfer amounts.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes supporting transfer source, booking-time, and held-account lookups.

#### Data flow
- Seed inserts populate customer and account records, then transfers and account holds.
- `ledger_daily_volume` reads transfer booking timestamps and amounts, groups by booking date, and returns daily totals.
- An additional timestamped value fragment has no specified target table or column mapping.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account` reference `ledger_account.account_id`.
- `ledger_hold.account_id` references `ledger_account.account_id`.
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Cross-cutting
- Primary keys and foreign keys link the ledger entities; account IBANs are unique.
- `ledger_transfer.amount_cents` is constrained to values greater than zero.
