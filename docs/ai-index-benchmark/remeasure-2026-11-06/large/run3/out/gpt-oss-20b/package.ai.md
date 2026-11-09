### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:21:06Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: A5839999
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified data‑management layer that centralises error codes, product inventory, and payment‑ledger test data for an e‑commerce platform.

#### Purpose
- Provide a single source of truth for error handling, product cataloging, and ledger data seeding.
- Enable consistent data access and validation across the application.

#### Responsibilities
- **Error Catalog** – define and expose platform‑wide error codes.
- **Product Inventory** – model categories, products, pricing, and multi‑warehouse stock in XML.
- **Ledger Seeding** – bulk‑load customers, accounts, transfers, and holds into PostgreSQL and expose daily volume metrics.

#### Key units
- `PlatformErrorCode` enum – numeric code, message, reverse lookup.
- XML catalog (`catalog`, `category`, `product`, `inv:stock`, `inv:recall`) – self‑contained product data.
- PostgreSQL tables: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- View: `ledger_daily_volume` – aggregates transfer volume per day.
- Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error handling**: callers use `PlatformErrorCode.byNumber(code)` to retrieve error details for logging or UI.
2. **Product catalog**: the XML file is parsed once at startup to populate in‑memory structures for inventory checks, order processing, and dashboards.
3. **Ledger loading**: sequential bulk INSERTs populate customers → accounts → transfers → holds; the view then aggregates transfer amounts per day for reporting.

#### Dependencies
- Java collections (`Map`, `HashMap`) for the error enum.
- XML namespaces `urn:example:catalog` and `urn:example:inventory` for the catalog.
- PostgreSQL foreign keys linking accounts to customers, transfers to accounts, and holds to accounts.
- Indexes on transfer source, booking timestamp, and hold account for query performance.

#### Cross-cutting
- Immutable, thread‑safe error enum instances.
- Consistent naming conventions (`<DOMAIN>_<NNNNN>` for errors, `cat-XXX` for categories, `W1`‑`W9` for warehouses).
- Uniform attribute and element naming across XML (`id`, `sku`, `warehouse`, `batch`).
- All prices use `currency="EUR"`; stock quantities are integers.
- Bulk INSERTs and reverse lookup maps for efficient data access.
