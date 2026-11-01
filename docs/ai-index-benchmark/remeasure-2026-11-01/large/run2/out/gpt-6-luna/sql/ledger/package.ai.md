### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:51:58Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Stores customer and bank-account records, account-to-account transfers, daily transfer totals, and fraud-review holds.

#### Purpose
- Defines the ledger’s customer, account, transfer, and hold data, with a view for daily transfer volume.
- Loads sample records into all four tables.

#### Responsibilities
- **Customer and account registry:** Stores customer names and country codes; links accounts to customers and enforces unique IBANs.
- **Transfer ledger and reporting:** Records positive-amount transfers between accounts and aggregates transfer amounts by booking date.
- **Fraud-review holds:** Records hold reasons associated with accounts.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with unique IBANs and opening dates.
- `ledger_transfer` — account-to-account transfers with positive amounts in cents and booking timestamps.
- `ledger_hold` — account-linked fraud-review hold reasons.
- `ledger_daily_volume` — daily sum of transfer amounts, grouped by booking date.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer source, transfer booking time, and held account lookups.

#### Data flow
- Customer records support account records; accounts are referenced as both source and destination by transfers, and as the subject of holds.
- `ledger_daily_volume` derives each day’s total from `ledger_transfer.booked_at` and `amount_cents`.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Cross-cutting
- Foreign keys enforce customer/account relationships; transfer amounts must be positive, and account IBANs must be unique.
- Bulk `INSERT` statements populate customers, accounts, transfers, and holds.
