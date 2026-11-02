### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:32:35Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 5 of 5 chunks summarized

> Models ledger customers and bank accounts, records inter-account transfers, and tracks accounts under fraud review.

#### Purpose
- Describes customer, account, transfer, and fraud-hold data, with a daily transfer-volume aggregate.
- Includes summaries of seed data and a timestamped record fragment whose target is unspecified.

#### Type
- Tables, view, indexes, and DML are described; SQL dialect is not evident.
- No extension or user-defined type is shown.

#### Schema
- `ledger_customer`: `customer_id INTEGER` (PK; unnamed), `display_name VARCHAR(120)` (NOT NULL), `country_code CHAR(2)` (NOT NULL).
- `ledger_account`: `account_id INTEGER` (PK; unnamed), `customer_id INTEGER` (NOT NULL, FK; unnamed), `iban VARCHAR(34)` (NOT NULL, UNIQUE; unnamed), `opened_on DATE` (NOT NULL).
- `ledger_transfer`: `transfer_id INTEGER` (PK; unnamed), `from_account INTEGER` (NOT NULL, FK; unnamed), `to_account INTEGER` (NOT NULL, FK; unnamed), `amount_cents BIGINT` (NOT NULL, CHECK `amount_cents > 0`; unnamed), `booked_at TIMESTAMP` (NOT NULL).
- `ledger_hold`: `hold_id INTEGER` (PK; unnamed), `account_id INTEGER` (NOT NULL, FK; unnamed), `hold_reason VARCHAR(80)` (NOT NULL).
- `ledger_daily_volume` view: `day` (date cast of `ledger_transfer.booked_at`), `volume_cents` (sum of `ledger_transfer.amount_cents`), grouped by booking date.
- Index `idx_transfer_from` on `ledger_transfer(from_account)`.
- Index `idx_transfer_booked` on `ledger_transfer(booked_at)`.
- Index `idx_hold_account` on `ledger_hold(account_id)`.

#### Reads
- `ledger_daily_volume` reads `ledger_transfer.booked_at` and `ledger_transfer.amount_cents`.

#### Writes
- `ledger_customer(customer_id, display_name, country_code)`: 8 inserts × 500 = **4,000 rows**.
- `ledger_account(account_id, customer_id, iban, opened_on)`: Part 1 reports 9 inserts × 500 + 434 = **4,934 rows**; Part 2 separately reports 100 + 500 + 500 + 500 = **1,600 visible rows**, excluding an incomplete tuple.
- `ledger_transfer(transfer_id, from_account, to_account, amount_cents, booked_at)`: Part 2 reports 11 × 500 + 2 = **5,502 rows**; Part 3 reports 14 × 500 + 97 = **7,097 rows**; Part 4 reports 13 × 500 + 113 = **6,613 visible rows**, plus 438 preceding tuples of unknown target and statement boundary.
- `ledger_hold(hold_id, account_id, hold_reason)`: **50 rows** in one insert.
- An additional Part 5 fragment contains timestamped value tuples; target table, column mapping, and tuple count are not provided.

#### Relationships
- `ledger_account.customer_id -> ledger_customer.customer_id` (FK).
- `ledger_transfer.from_account -> ledger_account.account_id` (FK in Part 1; relationship by column name only in Part 2).
- `ledger_transfer.to_account -> ledger_account.account_id` (FK in Part 1; relationship by column name only in Part 2).
- `ledger_hold.account_id -> ledger_account.account_id` (FK).

#### Dependencies
- `ledger_daily_volume` references `ledger_transfer`.
- `ledger_account` references `ledger_customer`.
- `ledger_transfer` references `ledger_account`.
- `ledger_hold` references `ledger_account`.
- `ledger_account`, `ledger_transfer`, and `ledger_hold` are described as insert targets.
