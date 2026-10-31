### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:52:10Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Multi-domain SQL persistence package implementing transactional inventory and order workflows for library circulation (PostgreSQL), e-commerce sales (MySQL), and warehouse stock synchronization (SQL Server), each with atomic stored procedures and audit trails.

#### Purpose
- Define relational schemas and transactional routines for three inventory-centric business domains—library lending, retail order management, and warehouse stock sync—across three SQL dialects
- Provide atomic stored procedures for state transitions (checkout/return, add-to-order, stock merge) with integrated audit logging and reporting views

#### Responsibilities
- **Library (PostgreSQL)**: multi-branch lending schema with `author`, `book`, `branch`, `book_copy`, `member`, `loan`, `loan_audit` tables; loan lifecycle via `fn_checkout_book` and `sp_return_book`; late-fee calculation via `fn_late_fee`; trigger-based audit via `trg_loan_audit`; reporting views `overdue_loans`, `member_loan_stats`, `popular_books`; deterministic seed data for testing
- **Shop (MySQL)**: product catalog and order management with `product`, `customer_order`, `order_item` tables; status ENUM workflow (`NEW`/`PAID`/`SHIPPED`/`CANCELLED`); `add_order_item` procedure for atomic line-item upsert, stock decrement, and order total recalculation
- **Warehouse (SQL Server)**: nightly incremental sync merging `dbo.SupplierFeed` deltas into `dbo.StockLevel` via `dbo.usp_SyncStock`, persisting per-run metrics and errors to `dbo.SyncLog`

#### Key units
- PostgreSQL tables: `author`, `book`, `branch`, `book_copy`, `member`, `loan`, `loan_audit`; views: `overdue_loans`, `member_loan_stats`, `popular_books` (materialized); routines: `fn_checkout_book(p_member_id, p_book_id, p_days)`, `sp_return_book(p_loan_id)`, `fn_late_fee(p_due_on, p_returned_on)`, trigger `trg_loan_audit`
- MySQL tables: `product` (unique `uk_product_sku`), `customer_order` (indexed `idx_order_customer`), `order_item` (composite PK); procedure: `add_order_item(p_order_id, p_sku, p_qty)`
- SQL Server tables: `dbo.StockLevel` (composite PK `WarehouseId`, `Sku`), `dbo.SupplierFeed`, `dbo.SyncLog`; procedure: `dbo.usp_SyncStock(@Since DATETIME2(0))`

#### Data flow
- Library checkout: `fn_checkout_book` validates `member.is_blocked` → decrements availability (`book.copies_free` v1 or `book_copy` status v2) → inserts `loan` with `due_on = CURRENT_DATE + p_days` → `trg_loan_audit` inserts `loan_audit` row ('CHECKOUT')
- Library return: `sp_return_book` sets `loan.returned_on` → trigger logs 'RETURN' to `loan_audit` → restores copy availability; `fn_late_fee` computes `LEAST(GREATEST(returned_on - due_on, 0) * 50, 2000)` cents
- Shop order: `add_order_item` locks `product` via `SELECT ... FOR UPDATE` → validates `stock >= p_qty` (else `SIGNAL SQLSTATE '45000'`) → upserts `order_item` → decrements `product.stock` → updates `customer_order.total_cents` from `SUM(quantity * unit_price_cents)`
- Warehouse sync: `usp_SyncStock` aggregates `SupplierFeed` where `ReceivedAt >= @Since` by `WarehouseId`/`Sku` → `MERGE` into `StockLevel` (increment `OnHand` or insert new) → captures `$action` counts → writes success/error row to `SyncLog`

#### Dependencies
- Database platforms: PostgreSQL (PL/pgSQL, partial indexes, materialized views, `generate_series`), MySQL 8 (InnoDB, utf8mb4, `SIGNAL SQLSTATE`, `ON DUPLICATE KEY UPDATE`, `FOR UPDATE`), SQL Server (T-SQL `MERGE` with `OUTPUT $action`, `SYSUTCDATETIME()`, `THROW`, `@@TRANCOUNT`)
- Internal FK chains: `book.author_id`→`author`, `loan.member_id`→`member` (CASCADE), `loan.copy_id`→`book_copy`, `book_copy.branch_id`→`branch`, `member.home_branch_id`→`branch`, `order_item`→`customer_order`/`product` (CASCADE), logical join `SupplierFeed`→`StockLevel` on `WarehouseId`+`Sku`
- External tooling: seed generator script `docs/ai-index-benchmark/tools/generate-seed-sql.py` for library fixtures

#### Cross-cutting
- Transactional boundaries: explicit `BEGIN`/`COMMIT` (PostgreSQL/MySQL) and `BEGIN TRANSACTION`/`TRY...CATCH` with `ROLLBACK`/`THROW` (SQL Server) wrapping multi-table mutations
- Audit/state patterns: trigger-based immutable logging (`trg_loan_audit`→`loan_audit`), execution-log table (`SyncLog` with `RowsInserted`/`RowsUpdated`/`ErrorMessage`), and status ENUM transitions on `customer_order`
- Inventory grain: per-copy physical tracking (`book_copy` with barcode/status), aggregate counters (`book.copies_total`/`copies_free`), per-product stock (`product.stock`), and per-warehouse/SKU on-hand (`StockLevel.OnHand`)
- Concurrency control: row-level locking (`FOR UPDATE`) and conditional stock/availability checks to prevent overselling or blocked-member checkouts
- Monetary representation: integer cents throughout (`price_cents`, `total_cents`, `unit_price_cents`, late fees capped at 2000 cents)
- Schema evolution: library v1→v2 migration from aggregate copy counters to normalized `book_copy` rows with branch assignment and barcode backfill via `generate_series`
