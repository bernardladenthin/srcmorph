### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:38:08Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified repository that centralises error codes, product catalog data, and payment‑ledger test data for a multi‑service platform.

#### Purpose
- Provide a single source of truth for error codes, product metadata, and ledger test data.
- Enable downstream services to consume consistent, read‑only data without duplication.

#### Responsibilities
- **Error code catalogue** – immutable numeric codes and messages for billing, storage, network, report, import, auth, and ledger‑lock modules.
- **Product catalog** – XML‑based product, price, and inventory information for e‑commerce services.
- **Payments ledger seed** – PostgreSQL tables, view, and indexes that populate and expose a realistic test ledger.

#### Key units
- **`PlatformErrorCode` enum** – constants with `int number`, `String message`; static `byNumber(int)` for reverse lookup.
- **XML catalog** – root `<catalog>` (namespace `urn:example:catalog`), `<category>`, `<product>` (SKU, title, `<price currency="EUR">`, `<inv:stock warehouse="…">`).
- **PostgreSQL objects**  
  - Tables: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.  
  - View: `ledger_daily_volume` (daily transfer volume).  
  - Indexes: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error codes** – loaded at class init; clients call `PlatformErrorCode.byNumber(code)` to obtain message or number.
2. **Product catalog** – XML parsed by consumers; traversal `<category>` → `<product>` → `<price>` / `<inv:stock>` yields product details and stock levels.
3. **Payments ledger** – data inserted in order: customers → accounts → transfers → holds; `ledger_daily_volume` aggregates `ledger_transfer` by booking date for analytics.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap` for error‑code lookup.
- XML: namespaces `urn:example:catalog` and `urn:example:inventory` (prefix `inv`).
- PostgreSQL: foreign keys linking accounts to customers, transfers to accounts, holds to accounts; view depends on `ledger_transfer`.

#### Cross‑cutting
- Immutable, thread‑safe error‑code enum instances.
- Consistent use of identifiers (`id`, `sku`, `warehouse`) across XML elements.
- Monetary values expressed in EUR in XML and as `BIGINT` cents in SQL.
- Referential integrity and indexing target common query patterns across the ledger tables.
