### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:07:16Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas and procedures that manage library cataloging, e‑commerce orders, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server environments.

#### Purpose
- Persist and manipulate domain data for libraries, shops, and warehouses.
- Provide business logic via stored functions/procedures for checkout, order processing, and stock reconciliation.
- Expose reporting views and audit trails for operational insight.

#### Responsibilities
- **Data Modeling** – tables for catalog, members, loans, products, orders, stock levels, and supplier feeds.
- **Business Logic** – checkout/return functions, order‑item addition, late‑fee calculation, and stock merge.
- **Reporting & Auditing** – overdue loan view, member statistics, popular books materialized view, audit trail for loans, and sync logs for warehouse updates.
- **Migration & Seeding** – schema evolution scripts for branch support and initial data population.

#### Key units
- **Library**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; functions `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**: `product`, `customer_order`, `order_item`; procedure `add_order_item(p_order_id, p_sku, p_qty)`.
- **Warehouse**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock(@Since)`.

#### Data flow
1. **Library Checkout** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, logs audit.
2. **Library Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, triggers audit.
3. **Shop Order Item** – `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
4. **Warehouse Sync** – `usp_SyncStock` aggregates `SupplierFeed` rows after `@Since`, MERGE into `StockLevel`, records counts and errors in `SyncLog`.

#### Dependencies
- **Internal** – cross‑table FK constraints (`loan.member_id`, `order_item.order_id`, `order_item.product_id`, `StockLevel.WarehouseId`, `StockLevel.Sku`).
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL transaction/locking (`SELECT … FOR UPDATE`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).
- **Schema evolution** – migration script `04_migration_v2.sql` adds branch support to library schema.

#### Cross-cutting
- Referential integrity enforced via foreign keys across all schemas.
- Centralized audit mechanisms: `loan_audit` table with trigger, and `SyncLog` table for warehouse operations.
- Late‑fee logic encapsulated in `fn_late_fee` for reuse.
- Concurrency control: MySQL `FOR UPDATE` locking, SQL Server MERGE within a single transaction with rollback on error.
- Reporting views use standard SQL joins and aggregates; materialized view `popular_books` for performance.
