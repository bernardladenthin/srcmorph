### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T22:47:20Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Relational database schemas and stored procedures for three operational business domains: library lending, web-shop order placement, and warehouse stock synchronisation.

#### Purpose
- Provides complete database-layer implementations (DDL + DML routines) for three independent business systems, each targeting a different SQL engine.
- Encapsulates domain business rules (availability, stock reservation, late fees, sync idempotency) inside stored procedures and triggers rather than application code.

#### Responsibilities
- **Library lending** (`library/`) — full loan lifecycle (checkout → return), late-fee calculation, multi-branch migration, audit trail, and reporting views on PostgreSQL.
- **Web-shop ordering** (`shop/`) — product catalog, order headers, line-item upserts, and atomic stock-reserving procedure on MySQL.
- **Warehouse stock sync** (`warehouse/`) — nightly MERGE of supplier feed quantities into per-warehouse SKU stock levels with sync logging on SQL Server.

#### Key units
- `fn_checkout_book` / `sp_return_book` / `fn_late_fee` — PostgreSQL loan-lifecycle routines with blocked-member and availability enforcement.
- `trg_loan_audit` — PostgreSQL trigger writing CHECKOUT/RETURN events to `loan_audit`.
- `overdue_loans` / `member_loan_stats` / `popular_books` — PostgreSQL reporting views (last one materialized).
- `branch` / `book_copy` — v2 multi-branch entities replacing v1 `copies_total`/`copies_free` counters.
- `add_order_item` — MySQL stored procedure; `FOR UPDATE` row lock, idempotent upsert, stock decrement, total recalculation.
- `product` / `customer_order` / `order_item` — MySQL order-placement tables; integer-cents pricing.
- `dbo.usp_SyncStock` — SQL Server parameterised procedure; MERGEs aggregated `SupplierFeed` rows into `dbo.StockLevel`, logs to `dbo.SyncLog`.
- `dbo.StockLevel` / `dbo.SupplierFeed` / `dbo.SyncLog` — SQL Server warehouse stock tables.

#### Data flow
- **Library**: caller → `fn_checkout_book` → validate `member.is_blocked` + `book.copies_free` → INSERT `loan` → trigger → `loan_audit`; return path mirrors via `sp_return_book`.
- **Shop**: caller → `add_order_item` → `FOR UPDATE` lock on `product` → upsert `order_item` → decrement `product.stock` → recalculate `customer_order.total_cents`.
- **Warehouse**: `SupplierFeed` (filtered by watermark) → aggregate `SUM(Quantity)` per `(WarehouseId, Sku)` → MERGE into `StockLevel` → capture action counts → INSERT `SyncLog`.

#### Dependencies
- **Engines**: PostgreSQL (PL/pgSQL, materialized views, partial indexes), MySQL (InnoDB, `ON DUPLICATE KEY UPDATE`, `SIGNAL`), SQL Server (T-SQL, `MERGE`, `DATETIME2`).
- **Internal**: each sub-package is self-contained; no cross-references between library, shop, and warehouse.
- **External**: `customer_order.customer_id` references an external customer table not defined in this package.

#### Cross-cutting
- **Concurrency control**: pessimistic row locks (`FOR UPDATE` in shop; implicit serialisation via `BIGSERIAL` + CHECK in library); warehouse relies on single-transaction MERGE.
- **Error handling**: `RAISE EXCEPTION` (PostgreSQL), `SIGNAL SQLSTATE '45000'` (MySQL), `TRY/CATCH` + re-throw (SQL Server) — each engine's native mechanism for business-rule violations.
- **Monetary representation**: integer cents throughout (`price_cents`, `total_cents`, `unit_price_cents`) to avoid floating-point drift.
- **Auditability**: library uses a trigger-based append-only `loan_audit`; warehouse uses a per-run `SyncLog` with row counts and error messages.
- **Idempotency**: shop uses `ON DUPLICATE KEY UPDATE` for repeated line-item calls; warehouse is idempotent only if the caller advances the `@Since` watermark (re-running with same watermark double-counts).
- **Versioning**: library has an explicit v1→v2 migration path (`04_migration_v2.sql`) introducing branch-level copy tracking.
