### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:44Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi-database SQL bundle covering three operational domains: PostgreSQL library circulation, MySQL web-shop ordering with atomic stock reservation, and T-SQL warehouse stock synchronisation.

#### Purpose
- Collection of three independent database packages — library (PostgreSQL), shop (MySQL), warehouse (T-SQL) — each defining its own schema, routines, views, and data.
- No shared code across subpackages; each is self-contained for its business vertical.

#### Responsibilities
- **library/** — lending-library schema (`author`, `book`, `member`, `loan`), reporting views, circulation routines (checkout/return/late fee), audit trigger, v2 migration introducing per-copy branch inventory (`branch`, `book_copy`), and seed data.
- **shop/** — e-commerce model: `product` catalog, `customer_order` headers, `order_item` lines, plus a single stored procedure `add_order_item` wrapping stock validation, decrement, line upsert, and order-total recalculation.
- **warehouse/** — T-SQL stock sync: `StockLevel` current on-hand against `SupplierFeed` incoming quantities, merged by `dbo.usp_SyncStock` with `SyncLog` audit trail and error capture.

#### Key units
- library: tables `author`, `book`, `member`, `loan`, `branch`, `book_copy`; views `overdue_loans`, `member_loan_stats`, materialized `popular_books`; routines `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit` + `trg_loan_audit_fn`.
- shop: `product` (UNIQUE SKU, price in cents, stock), `customer_order` (status ENUM, total), `order_item` (composite PK on order+product), procedure `add_order_item` (IN order_id, sku, qty; `FOR UPDATE` lock; `SIGNAL` 45000 errors).
- warehouse: `dbo.StockLevel` (composite PK `WarehouseId`+`Sku`), `dbo.SupplierFeed` (identity `FeedRowId`), `dbo.SyncLog` (run stats + nullable `ErrorMessage`), `dbo.usp_SyncStock @Since`.

#### Data flow
- library: checkout validates member → decrements `copies_free` → inserts loan → audit trigger logs; return sets `returned_on` and increments count; views read base tables; v2 migration backfills one `book_copy` row per `copies_total` via `generate_series` then drops old counters.
- shop: `add_order_item` looks up product by SKU `FOR UPDATE` → aborts on unknown SKU or insufficient stock → upserts/ increments line → decrements stock → recomputes order total via SUM over lines.
- warehouse: `SupplierFeed` rows with `ReceivedAt >= @Since` grouped by `WarehouseId`/`Sku` (SUM Quantity) → MERGE into `StockLevel` (match: add to OnHand, update `UpdatedAt`; no match: insert) → `$action` counts write one `SyncLog` row; catch path rolls back and logs a 0/0 row with `ERROR_MESSAGE()` then rethrows.

#### Dependencies
- Internal: library routines/views reference `author`, `book`, `member`, `loan`, `loan_audit`; migration alters core tables and creates `branch`/`book_copy`; shop relies on FKs `fk_item_order` (cascade delete) and `fk_item_product`; warehouse MERGE reads `SupplierFeed` and writes `StockLevel`/`SyncLog`.
- External: PostgreSQL features — `BIGSERIAL`/`SMALLSERIAL`, partial indexes, `FILTER`, `CURRENT_DATE`, `INTERVAL`, `generate_series`, `CROSS JOIN LATERAL`, PL/pgSQL triggers, transactional DDL; MySQL 8 dialect — InnoDB, utf8mb4, `DELIMITER`, `ENUM`, `SIGNAL SQLSTATE '45000'`; T-SQL — `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, MERGE with `$action`.

#### Cross-cutting
- Domain invariants enforced declaratively: CHECK constraints and partial index in library, ENUM statuses and unsigned-integer money in shop, composite PKs and UTC timestamps in warehouse.
- Money stored as integer cents throughout library (`copies`, fees) and shop (`price_cents`, `total_cents`).
- Multi-statement scripts wrapped in transactions (`BEGIN`/`COMMIT` in library; explicit commit/rollback in warehouse procedure).
- Consistent PK patterns: `BIGSERIAL` in library, identity columns in warehouse, composite PKs for line/stock rows.
- Business-rule failures surfaced via DB-level mechanisms: `SIGNAL` in MySQL, `ERROR_MESSAGE()` rethrow in T-SQL, blocked-member validation in PL/pgSQL.
