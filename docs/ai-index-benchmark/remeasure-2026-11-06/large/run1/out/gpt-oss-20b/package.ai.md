### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:09:52Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 676FB499
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified system for managing error codes, retail product catalogs, and payment ledger data across a multi‑domain application.

#### Purpose
- Centralize platform error codes for consistent handling.
- Store and expose a comprehensive retail product catalog with inventory.
- Persist payment entities and provide daily transfer volume reporting.

#### Responsibilities
- **Error handling** – Define, store, and lookup numeric error codes.
- **Catalog management** – Organize products by category, SKU, price, and per‑warehouse stock.
- **Payment persistence** – Store customers, accounts, transfers, fraud holds, and aggregate daily transfer volume.

#### Key units
- **`PlatformErrorCode` enum** – Immutable constants with numeric ID and message; fast `byNumber` lookup.
- **XML catalog** – Root `catalog` element containing 34 `category` nodes, each with many `product` entries (SKU, title, price EUR, `inv:stock` per warehouse).
- **PostgreSQL tables** – `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`; view `ledger_daily_volume`; indexes `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error codes**: Application references `PlatformErrorCode` constants or calls `byNumber(code)` to retrieve details.
2. **Catalog**: Retail app parses the XML file, iterates categories → products, extracts SKU, title, price, and inventory per warehouse, then populates catalog and inventory databases.
3. **Payments**: Bulk inserts populate the four ledger tables; `ledger_daily_volume` aggregates `ledger_transfer.amount_cents` per day for reporting.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.
- XML: `urn:example:catalog` and `urn:example:inventory` namespaces.
- PostgreSQL: foreign keys linking accounts to customers, transfers to accounts, holds to accounts; view depends on `ledger_transfer`.

#### Cross-cutting
- Immutable, thread‑safe design of `PlatformErrorCode`.
- Consistent use of SKU as unique product identifier; all prices in EUR.
- Strict non‑null and check constraints on ledger tables; integer primary keys.
- Indexes target common query patterns (source account, booking date, hold account).
