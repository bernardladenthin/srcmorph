### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:38:55Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified data‑management layer that catalogs platform error codes, e‑commerce product inventory, and payment ledger records, exposing lookup APIs, XML schemas, and SQL views for reporting.

#### Purpose
- Centralize error code definitions for platform-wide use.
- Persist and expose product catalog, pricing, inventory, and recall data.
- Seed and report on a payments ledger with customers, accounts, holds, and transfers.

#### Responsibilities
- **Error Code Management** – define, store, and reverse‑lookup platform error codes.
- **Product Cataloging** – structure product data, inventory levels, and recall batches in XML.
- **Payments Ledger Seeding & Reporting** – populate customers, accounts, holds, transfers, and provide a daily volume view.

#### Key units
- `PlatformErrorCode` (enum) – numeric ID, message, static reverse‑lookup map.
- `catalog.xml` – XML root with `<category>` and `<product>` elements; `<inv:stock>` and `<inv:recall>` child nodes.
- PostgreSQL objects:
  - Tables: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
  - View: `ledger_daily_volume`.
  - Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error codes**: class loading populates `BY_NUMBER`; callers use `byNumber()` to obtain enum instances.
2. **Catalog**: XML parsed → categories → products → stock and recall nodes mapped to warehouses and batches; data persisted or served for queries.
3. **Ledger**: customers → accounts → transfers → holds; `ledger_daily_volume` aggregates transfers by day for reporting.

#### Dependencies
- Java: `java.util.Map`, `HashMap` for error code lookup.
- XML: namespaces `urn:example:catalog` and `urn:example:inventory`.
- PostgreSQL: foreign keys linking accounts to customers, transfers and holds to accounts; view reads from `ledger_transfer`.

#### Cross-cutting
- Immutable enum instances ensure thread safety for error codes.
- Consistent namespace and attribute conventions in XML simplify parsing and mapping.
- Referential integrity and targeted indexes in SQL enforce data consistency and optimize common queries.
