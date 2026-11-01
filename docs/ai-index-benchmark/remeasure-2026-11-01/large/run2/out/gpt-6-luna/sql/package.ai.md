### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:52:02Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Maintains customer bank accounts, records account-to-account transfers, summarizes daily transfer volume, and tracks fraud-review holds.

#### Purpose
- Defines the ledger’s customer, account, transfer, and hold records, plus a daily transfer-volume view.
- Seeds the ledger tables with sample records.

#### Responsibilities
- **Customer and account registry:** Stores customer names and country codes; links accounts to customers and enforces unique IBANs.
- **Transfer recording and reporting:** Stores positive-amount transfers between accounts and aggregates totals by booking date.
- **Fraud-review tracking:** Associates hold reasons with accounts.

#### Key units
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with unique IBANs and opening dates.
- `ledger_transfer` — transfers between accounts, with positive amounts in cents and booking timestamps.
- `ledger_hold` — account-linked fraud-review hold reasons.
- `ledger_daily_volume` — daily transfer amount totals grouped by booking date.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer-source, booking-time, and held-account lookups.

#### Data flow
- Customer records support account records; accounts serve as transfer sources and destinations and may be associated with holds.
- `ledger_daily_volume` derives daily totals from `ledger_transfer.booked_at` and `amount_cents`.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Cross-cutting
- Foreign keys enforce customer/account relationships; constraints require positive transfer amounts and unique account IBANs.
- Bulk `INSERT` statements populate customer, account, transfer, and hold records.
