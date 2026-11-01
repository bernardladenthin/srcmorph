### ai
- H: 1.0
- C: 8B2A3ED5
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:23:59Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified data‑management layer that centralises error codes, product catalogues, and payment ledger data for an e‑commerce platform.

#### Purpose
- Provide a single source of truth for platform error codes, product inventory, and payment transactions.
- Expose these data sets to application code and reporting tools.

#### Responsibilities
- **Error handling** – define and lookup platform error codes.
- **Product cataloguing** – store and expose product, price, and inventory information.
- **Payments data warehousing** – persist customers, accounts, and transfers; aggregate daily transfer volume.

#### Key units
- **`PlatformErrorCode` enum** – ~4,200 constants mapping numeric codes to messages; static `BY_NUMBER` map for reverse lookup.
- **XML catalog schema** – root `<catalog>` (versioned), `<category>` (id, name), `<product>` (sku, title, price, `<inv:stock>` per warehouse).
- **PostgreSQL tables** – `ledger_customer`, `ledger_account`, `ledger_transfer`; view `ledger_daily_volume`; indexes `idx_transfer_from`, `idx_transfer_booked`.

#### Data flow
1. **Error codes**: enum constants created → `BY_NUMBER` map populated → callers use `byNumber(code)` to retrieve enum, then `getNumber()` / `getMessage()`.
2. **Product catalog**: XML parsed → categories → products → price and stock per warehouse extracted → data structure queried for listings, pricing, or availability.
3. **Payments**: inserts into `ledger_customer` → `ledger_account` → `ledger_transfer`; view `ledger_daily_volume` aggregates `amount_cents` per day; indexes speed source and booking‑time queries.

#### Dependencies
- Java SE collections for error‑code lookup.
- XML namespaces `urn:example:catalog` and `urn:example:inventory` for catalog parsing.
- PostgreSQL foreign keys and indexes linking customers → accounts → transfers; view depends on all three tables.

#### Cross-cutting
- Immutable, thread‑safe lookup map in `PlatformErrorCode`.
- Consistent attribute usage (`id`, `name`, `sku`, `currency="EUR"`, `warehouse`) across XML elements.
- All PostgreSQL tables use standard types and enforce referential integrity via foreign keys; no custom extensions or triggers.
