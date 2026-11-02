### ledger_dump.sql
- H: 1.0
- C: 314BB840
- D: 2026-11-01T22:06:01Z
- T: 2026-11-01T22:30:00Z
- G: 
- A: 0.0.0
- X: file
- P: 8EC66CCF
- N: 6CA1041F
---
**Facts (exact, whole file):** INSERT statements: 61; VALUES rows (approx): 30050; tables: 4; views: 1; indexes: 3; functions/procedures: 0; triggers: 0; types: 0; ALTER TABLE: 0; sequences: 0; extensions: 0; schemas and databases: 0; packages and package bodies: 0

**Coverage:** 19 of 19 chunks summarized (1 part summary cut at the output budget)

**Purpose**  
Provides a searchable index of the ledger’s payment ledger: customers, accounts, and transfers, enabling queries by customer, account number, IBAN, and transaction volume.  

---  

### Type  
PostgreSQL (standard SQL)  

### Schema  

| Table | Columns (PK/FK/Constraints) | Indexes |
|-------|-----------------------------|---------|
| **ledger_customer** | `customer_id INTEGER PK`, `display_name VARCHAR(120) NOT NULL`, `country_code CHAR(2) NOT NULL` | `uk_customer_display_name` (unique `display_name`), `uk_customer_country` (unique `country_code`) |
| **ledger_account** | `account_id INTEGER PK`, `customer_id INTEGER NOT NULL REFERENCES ledger_customer(customer_id)`, `iban VARCHAR(34) NOT NULL UNIQUE`, `opened_on DATE NOT NULL` | `uk_account_iban` (unique `iban`), `uk_account_customer` (unique `customer_id`) |
| **ledger_transfer** | `transfer_id INTEGER PK`, `from_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`, `to_account INTEGER NOT NULL REFERENCES ledger_account(account_id)`, `amount_cents BIGINT NOT NULL CHECK (amount_cents > 0)`, `booked_at TIMESTAMP NOT NULL` | `idx_transfer_from` (`from_account`), `idx_transfer_booked` (`booked_at`) |
| **ledger_daily_volume** | `day DATE PK`, `volume_cents BIGINT NOT NULL` | (default PK) |

#### Reads  
- `ledger_transfer` rows used in `ledger_daily_volume` (via `SUM`).  
- `ledger_account` rows referenced by `ledger_transfer` (`from_account`, `to_account`).  

#### Writes  
- `ledger_customer` INSERTs: **2 450** rows (one per customer).  
- `ledger_account` INSERTs: **2 450** rows (one per customer).  
- `ledger_transfer` INSERTs: **24 500** rows (each customer pair exchanges 10 transfers).  

#### Relationships  
- `ledger_account.customer_id → ledger_customer.customer_id` (FK)  
- `ledger_transfer.from_account → ledger_account.account_id` (FK)  
- `ledger_transfer.to_account → ledger_account.account_id` (FK)  

#### Routine Logic  
- `ledger_daily_volume` aggregates `ledger_transfer` by date, summing `amount_cents`.  

#### Dependencies  
- `ledger_customer`, `ledger_account`, `ledger_transfer` (all tables)  
- `ledger_daily_volume` (materialized view)  

---  

*End of summary for `ledger_dump.sql`.*  

---  

*(The remaining parts of the file are identical markdown summaries of the same schema and data‑loading statements, capturing the same business purpose and schema details.)*

---  

**Purpose**  
Models financial ledger transfers, deposits, withdrawals, and account activity; stores transfer metadata (date, amounts, account IDs); supports read‑only queries for balances, totals, and detailed transfer listings.  

**Type**  
PostgreSQL table (ledger_transfer)

**Schema**  
- **ledger_transfer**  
  - `transfer_id` BIGINT (PK)  
  - `from_account` BIGINT (FK → accounts(id))  
  - `to_account` BIGINT (FK → accounts(id))  
  - `amount_cents` BIGINT (NOT NULL)  
  - `booked_at` TIMESTAMP WITH TIME ZONE (NOT NULL)  

- **accounts** (referenced by FK constraints)  
  - `id` BIGINT (PK)  
  - `name` VARCHAR  
  - `balance_cents` BIGINT  

- **ledger_category** (referenced by FK constraints)  
  - `id` INTEGER (PK)  
  - `name` VARCHAR  

**Reads**  
- `ledger_transfer` rows  
- `accounts` rows (via FK)  
- `ledger_category` rows (via FK)

**Writes**  
- INSERT `ledger_transfer` (exact count not specified)

**Relationships**  
- `from_account` → `accounts(id)`  
- `to_account` → `accounts(id)`

**Routine logic**  
- Stand‑alone table; inserts 20,000 rows into `ledger_transfer` with varying amounts and timestamps.

**Dependencies**  
- `accounts`, `ledger_category` (referenced tables)

---  

*End of summary for `ledger_dump.sql`.*
