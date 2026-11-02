### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:36Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> Maintains customer-owned bank accounts, records inter-account transfers and account holds, and summarizes booked transfer volume by day.

#### Purpose
- Defines a ledger schema and inserts sample customer, account, transfer, and hold data.
- Provides a daily aggregate of booked transfer amounts.

#### Responsibilities
- **Customer and account records:** `ledger_customer` stores customer names and country codes; `ledger_account` associates accounts with customers and records IBANs and opening dates.
- **Transfer and hold records:** `ledger_transfer` records source and destination accounts, positive amounts in cents, and booking timestamps; `ledger_hold` records account hold reasons.
- **Volume reporting:** `ledger_daily_volume` aggregates transfer amounts by booking date.
- **Seed data:** Inserts thousands of customers, accounts, and transfers, plus 50 account holds.

#### Key units
- `ledger_customer` — customer identity and country data.
- `ledger_account` — customer-linked bank accounts with unique IBANs.
- `ledger_transfer` — account-to-account transfers with positive amounts and booking timestamps.
- `ledger_hold` — account-linked hold reasons.
- `ledger_daily_volume` — daily sum of transfer amounts in cents.

#### Data flow
- Customer records associate with accounts through `customer_id`; transfers and holds reference accounts through their respective account IDs.
- `ledger_daily_volume` groups `ledger_transfer` rows by the date portion of `booked_at` and sums `amount_cents`.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Primary keys identify records; foreign keys encode customer/account relationships.
- `ledger_account.iban` is unique; `ledger_transfer.amount_cents` has a positive-value check.
- Indexes cover transfer source account, transfer booking timestamp, and hold account.
