### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-01T22:06:01Z
- T: 2026-11-02T00:09:15Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL‑based financial ledger package that models customer accounts, account transfers, and daily transaction volumes, providing searchable read‑only queries by customer, account, IBAN, and transaction volume.

#### Purpose
- Models financial ledger transfers, deposits, withdrawals, and account activity.
- Stores transfer metadata (date, amounts, account IDs).
- Supports read‑only queries for balances, totals, and detailed transfer listings.

#### Responsibilities
- **ledger_dump.sql** – DDL and DML for tables `ledger_customer`, `ledger_account`, `ledger_transfer`, and `ledger_daily_volume`.
- **ledger_daily_volume** – Materialized view aggregating `ledger_transfer` by date to compute daily transaction volume.

#### Key Units
- **ledger_customer** (`customer_id PK`, `display_name VARCHAR(120) NOT NULL`, `country_code CHAR(2) NOT NULL`) – Unique customers.
- **ledger_account** (`account_id PK`, `customer_id FK → ledger_customer`, `iban VARCHAR(34) NOT NULL UNIQUE`, `opened_on DATE NOT NULL`) – Account identifiers and IBANs.
- **ledger_transfer** (`transfer_id PK`, `from_account FK → ledger_account`, `to_account FK → ledger_account`, `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`, `booked_at TIMESTAMP NOT NULL`) – Transfer records.
- **ledger_daily_volume** (`day DATE PK`, `volume_cents BIGINT NOT NULL`) – Materialized view summarizing daily transaction volumes.

#### Data Flow
Inputs (INSERT statements) populate `ledger_customer`, `ledger_account`, and `ledger_transfer`.  
`ledger_daily_volume` reads from `ledger_transfer` via `SUM` to compute daily totals.

#### Dependencies
- `ledger_customer`, `ledger_account`, `ledger_transfer` (all tables).
- `ledger_daily_volume` (materialized view).

#### Cross‑cutting
- Uses PostgreSQL standard SQL.
- FK constraints enforce referential integrity.
- Materialized view provides efficient daily volume queries.

*End of summary.*
