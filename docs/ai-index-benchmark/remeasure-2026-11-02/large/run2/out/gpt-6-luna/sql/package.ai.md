### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:27Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> Maintains customer bank accounts, account holds, and transfers, with daily reporting of transfer volume.

#### Purpose
- Defines and seeds a relational ledger for customers, accounts, transfers, and holds.
- Aggregates transfer amounts by booking date.

#### Responsibilities
- Stores customer identities and their bank accounts.
- Records account-to-account transfers and account holds.
- Supports daily transfer-volume reporting and indexed lookups.

#### Key units
- `ledger_customer` — customer identity, display name, and country code.
- `ledger_account` — customer-linked account with a unique IBAN and opening date.
- `ledger_transfer` — account endpoints, positive amount in cents, and booking timestamp.
- `ledger_hold` — account-linked hold reason.
- `ledger_daily_volume` — transfer amounts summed by the date of `booked_at`.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer source, booking time, and hold account.

#### Data flow
- Customer records link to accounts; accounts are referenced by transfers and holds.
- `ledger_daily_volume` groups `ledger_transfer` rows by booking date and sums `amount_cents`.
- Seeds four tables with approximately 30,050 rows across 61 `INSERT` statements.

#### Dependencies
- `ledger_account.customer_id` references `ledger_customer.customer_id`.
- `ledger_transfer.from_account` and `to_account`, and `ledger_hold.account_id`, reference `ledger_account.account_id`.
- `ledger_daily_volume` depends on `ledger_transfer`.

#### Cross-cutting
- Primary and foreign keys establish record identity and customer/account relationships.
- `ledger_account.iban` is unique; `ledger_transfer.amount_cents` must be positive.
- No functions, procedures, triggers, or sequences are defined.
