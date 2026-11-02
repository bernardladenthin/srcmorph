### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:35:47Z
- G: 
- A: 0.0.0
- X: file
- P: AC7FA967
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 9 of 9 chunks summarized

> A data‑dump that populates a payments ledger with customers, accounts, holds, and a large volume of transfer transactions, plus a view summarising daily transfer volume.

#### Purpose
- Seed the ledger with test data for customers, accounts, fraud holds, and transfer history.
- Provide a materialised view (`ledger_daily_volume`) for daily transfer volume reporting.

#### Type
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`
- **View**: `ledger_daily_volume`
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`
- Dialect: PostgreSQL

#### Schema
##### `ledger_customer`
- `customer_id` INTEGER PRIMARY KEY
- `display_name` VARCHAR(120) NOT NULL
- `country_code` CHAR(2) NOT NULL

##### `ledger_account`
- `account_id` INTEGER PRIMARY KEY
- `customer_id` INTEGER NOT NULL REFERENCES `ledger_customer`(`customer_id`)
- `iban` VARCHAR(34) NOT NULL UNIQUE
- `opened_on` DATE NOT NULL

##### `ledger_transfer`
- `transfer_id` INTEGER PRIMARY KEY
- `from_account` INTEGER NOT NULL REFERENCES `ledger_account`(`account_id`)
- `to_account` INTEGER NOT NULL REFERENCES `ledger_account`(`account_id`)
- `amount_cents` BIGINT NOT NULL CHECK (amount_cents > 0)
- `booked_at` TIMESTAMP NOT NULL

##### `ledger_hold`
- `hold_id` INTEGER PRIMARY KEY
- `account_id` INTEGER NOT NULL REFERENCES `ledger_account`(`account_id`)
- `hold_reason` VARCHAR(80) NOT NULL

##### Indexes
- `idx_transfer_from` ON `ledger_transfer`(`from_account`)
- `idx_transfer_booked` ON `ledger_transfer`(`booked_at`)
- `idx_hold_account` ON `ledger_hold`(`account_id`)

##### View
- `ledger_daily_volume`  
  `SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);`

#### Reads
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Writes
- `ledger_customer`: 4000 rows (10 batches of 100).
- `ledger_account`: 7660 rows (4000 + 3160 + 500; family: 3 families, first 4000 rows, then 3160, then 500).
- `ledger_transfer`: about 18 000 rows (families: 1600, 2938, 1000+, 1278, 3070, 3420, 3422, 1379; total ≈ 18 000).
- `ledger_hold`: 50 rows.

#### Relationships
- `ledger_account.customer_id` → `ledger_customer.customer_id`
- `ledger_transfer.from_account` → `ledger_account.account_id`
- `ledger_transfer.to_account` → `ledger_account.account_id`
- `ledger_hold.account_id` → `ledger_account.account_id`

#### Dependencies
- `ledger_customer` referenced by `ledger_account`.
- `ledger_account` referenced by `ledger_transfer` and `ledger_hold`.
