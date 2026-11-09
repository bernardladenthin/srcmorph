### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:53:09Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database‑centric modules that manage library cataloging, e‑commerce ordering, and warehouse inventory synchronization, each with its own schema, business logic, and audit mechanisms.

#### Purpose
- Persist and enforce rules for library loans, shop orders, and warehouse stock.
- Provide reporting, audit trails, and concurrency‑safe operations across three distinct domains.

#### Responsibilities
- **Library** – catalog, member, loan data; checkout/return logic; overdue reporting; audit logging.
- **Shop** – product catalog, customer orders, order items; stock reservation; order total calculation.
- **Warehouse** – nightly supplier feed ingestion; inventory upsert; sync audit logging.

#### Key units
- **Library**: `author`, `book`, `member`, `loan`, `loan_audit`; `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; `overdue_loans`, `member_loan_stats`, `popular_books` views.
- **Shop**: `product`, `customer_order`, `order_item`; `add_order_item` procedure.
- **Warehouse**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; `dbo.usp_SyncStock` procedure.

#### Data flow
1. **Library**: `fn_checkout_book` → validates → updates `book.copies_free` → inserts `loan` → triggers `loan_audit`.  
   `sp_return_book` → updates `loan.returned_on` → increments `book.copies_free` → triggers `loan_audit`.  
   Views aggregate loan/member/book data for reporting.
2. **Shop**: `add_order_item` locks `product` → checks stock → inserts/updates `order_item` → decrements `product.stock` → recalculates `customer_order.total_cents`.
3. **Warehouse**: `usp_SyncStock` aggregates `SupplierFeed` → `MERGE` into `StockLevel` (insert/update) → logs run in `SyncLog` with row counts and errors.

#### Dependencies
- **Internal**: each module references its own tables and audit tables; cross‑module dependencies are none.  
- **External**: PostgreSQL (functions, triggers, materialized views) for Library; MySQL 8 (ENUM, `FOR UPDATE`) for Shop; SQL Server system functions (`SYSUTCDATETIME`, `ERROR_MESSAGE`) for Warehouse.

#### Cross‑cutting
- **Audit patterns**: `loan_audit` and `SyncLog` capture state changes; Shop relies on FK cascades and unique SKU constraints.  
- **Concurrency**: Library functions use transactional updates to `copies_free`; Shop procedure locks product rows; Warehouse uses `MERGE` within a transaction.  
- **Error handling**: Functions/procedures raise explicit errors for blocked members, insufficient stock, or missing SKUs; Warehouse rolls back and logs errors.  
- **Constraints & indexes**: Each schema enforces referential integrity, uniqueness, and performance indexes on key columns.
