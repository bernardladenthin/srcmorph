### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:41Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Stores customer bank accounts, account-to-account transfers and holds, and daily booked transfer totals.

#### Purpose
- Defines the ledger schema and populates sample customers, accounts, transfers, and holds.
- Reports booked transfer volume grouped by day.

#### Responsibilities
- **Customer and account records:** `ledger_customer` stores customer identity and country; `ledger_account` links customers to accounts with unique IBANs and opening dates.
- **Transfers and holds:** `ledger_transfer` records account-to-account amounts and booking times; `ledger_hold` records account hold reasons.
- **Daily reporting and seed data:** `ledger_daily_volume` aggregates transfer amounts by booking date; sample data populates the ledger tables.

#### Key units
- `ledger_customer` — customer identity and country records.
- `ledger_account` — customer-linked bank accounts.
- `ledger_transfer` — transfers with positive amounts in cents and booking timestamps.
- `ledger_hold` — account-linked hold reasons.
- `ledger_daily_volume` — daily sums of booked transfer amounts.

#### Data flow
- `ledger_customer` records link to `ledger_account`; transfers and holds reference accounts.
- `ledger_daily_volume` groups `ledger_transfer` rows by the date of `booked_at` and sums `amount_cents`.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Primary and foreign keys enforce record identity and customer/account relationships.
- `ledger_account.iban` is unique; `ledger_transfer.amount_cents` must be positive.
- Indexes cover transfer source account, transfer booking timestamp, and hold account.
