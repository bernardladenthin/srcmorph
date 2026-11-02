### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:11:02Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified platform for managing error codes, product catalog data, and a payments ledger with analytics.

#### Purpose
- Centralize platform error codes for fast numeric lookup.
- Provide a structured XML catalog for products, pricing, and inventory.
- Deliver a ready‑to‑use PostgreSQL payments ledger with seeded data and a daily volume view.

#### Responsibilities
- **Error handling** – enumerate and expose error codes with constant‑time retrieval.
- **Catalog management** – define products, categories, prices, and per‑warehouse stock in XML.
- **Ledger provisioning** – create tables, seed data, and expose analytics via a view.

#### Key units
- **`PlatformErrorCode` enum** – integer code + message; static `BY_NUMBER` map for O(1) lookup.
- **XML catalog** – root `catalog` with `category` and `product` elements; `inv:stock` for inventory.
- **PostgreSQL schema** – tables `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`; view `ledger_daily_volume`; indexes `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error codes**: static block populates `BY_NUMBER`; callers use `byNumber(code)` → enum → `getNumber()`, `getMessage()`.
2. **Catalog**: XML parsed → iterate `category` → iterate `product` → read `price` and all `inv:stock` entries → expose structured data to pricing/fulfillment services.
3. **Ledger**: seed `ledger_customer` & `ledger_account`; insert `ledger_transfer` & `ledger_hold`; query `ledger_daily_volume` for daily transfer totals.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`.
- XML: namespaces `urn:example:catalog` and `urn:example:inventory` (prefix `inv`).
- PostgreSQL: foreign keys linking accounts to customers, transfers/holds to accounts; indexes for efficient querying.

#### Cross-cutting
- Immutable, thread‑safe lookup map for error codes.
- Consistent use of identifiers (`sku`, `id`, `name`) and currency (`EUR`) in the catalog.
- Referential integrity and indexing in the ledger schema to support analytics and performance.
