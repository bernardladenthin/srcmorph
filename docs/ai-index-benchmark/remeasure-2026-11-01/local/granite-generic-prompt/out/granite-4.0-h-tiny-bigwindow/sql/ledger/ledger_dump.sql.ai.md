### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:44:30Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 2 of 2 chunks summarized

**ledger_dump.sql**

> A data‑dump file that records the snapshot of a financial ledger on 2026‑10‑24 00:00:00, containing all accounts, transactions, balances and related metadata.

#### Purpose
- Stores a complete historical snapshot of the accounting ledger.
- Provides a point‑in‑time view for audits, reconciliation, and historical reporting.

#### Type
- **SQL script** (DDL/DML statements)

#### Schema
| Table | Columns (type) | Primary Key / Constraints |
|-------|----------------|---------------------------|
| `account` | `id SERIAL PRIMARY KEY`, `name VARCHAR(100) NOT NULL`, `type VARCHAR(20) NOT NULL CHECK (type IN ('asset','liability','equity','income','expense'))`, `created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP` | – |
| `transaction` | `id SERIAL PRIMARY KEY`, `account_id INT NOT NULL REFERENCES account(id)`, `date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP`, `description TEXT`, `amount NUMERIC(15,2) NOT NULL`, `is_credit BOOLEAN NOT NULL DEFAULT FALSE` | – |
| `balance` | `id SERIAL PRIMARY KEY`, `account_id INT NOT NULL REFERENCES account(id)`, `snapshot_date DATE NOT NULL DEFAULT CURRENT_DATE`, `balance NUMERIC(15,2) NOT NULL`, `created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP` | – |
| `ledger_snapshot` | `id SERIAL PRIMARY KEY`, `snapshot_date DATE NOT NULL DEFAULT CURRENT_DATE`, `checksum VARCHAR(64) NOT NULL`, `notes TEXT` | – |

#### Reads
- `account` for listing accounts and balances.
- `transaction` for retrieving transaction details.
- `balance` for retrieving current balances per account.
- `ledger_snapshot` for historical snapshots.

#### Writes
- `transaction` INSERTs new journal entries.
- `balance` INSERTs new snapshot records after each transaction batch.
- `ledger_snapshot` INSERTs a new snapshot entry daily.

#### Relationships
- `transaction.account_id -> account.id`
- `balance.account_id -> account.id`
- `ledger_snapshot` records daily snapshots of `balance` rows.

#### Routine logic
- **INSERT** a new transaction: `account_id` must exist; `amount` is added to the corresponding account’s balance.
- **UPDATE** account balances: after each transaction batch, compute running totals and INSERT a new `balance` row.
- **INSERT** a new snapshot: `ledger_snapshot` records a daily checksum and notes for reconciliation.

#### Dependencies
- `account` table (required for all transactions).
- `balance` table (required for reporting snapshots).
- `ledger_snapshot` table (required for historical audit trails).
