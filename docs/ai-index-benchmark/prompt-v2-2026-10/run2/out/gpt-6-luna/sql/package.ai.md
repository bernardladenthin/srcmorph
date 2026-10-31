### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:48:08Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Manages lending-library operations, shop order fulfillment, and warehouse stock synchronization through relational data models and transactional SQL workflows.

#### Purpose
- Define and operate data workflows for library borrowing, shop inventory and orders, and warehouse stock updates.
- Provide reporting, inventory changes, and operational audit or synchronization records.

#### Responsibilities
- **Library catalog and borrowing:** Maintain authors, books, members, loans, branch locations, and individually tracked book copies; support checkout, return, late-fee calculation, auditing, and loan reports.
- **Shop orders and stock reservation:** Store products, customer orders, and order lines; add or increase order items while reserving stock and recalculating totals.
- **Warehouse synchronization:** Aggregate recent supplier feed quantities into stock levels and log synchronization counts or failures.

#### Key units
- **Library:** `author`, `book`, `member`, `loan` — catalog, membership, and borrowing records; `branch`, `book_copy` — locations and per-copy inventory.
- **Library operations and reports:** `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — checkout, return, and late-fee workflows; `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan events; `overdue_loans`, `member_loan_stats`, `popular_books` — overdue, member activity, and popular-book reporting.
- **Shop:** `product`, `customer_order`, `order_item` — product inventory, orders, and order lines; `add_order_item` — validates stock, updates order lines and inventory, and recalculates totals.
- **Warehouse:** `dbo.usp_SyncStock(@Since)` — applies recent supplier quantities and logs outcomes; `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — feed, stock, and synchronization records; `@Changes` — captures merge actions for row counts.

#### Data flow
- **Library:** Seed catalog and member records, then loans; checkout validates member and copy availability, creates a loan, and triggers audit; return closes a loan, restores availability, and triggers audit. Reports derive from loan and related records; migration creates a default branch and copy rows from existing book counts.
- **Shop:** `add_order_item` locks and checks a SKU’s stock, inserts or increments its order line, deducts inventory, and recalculates the order total; insufficient stock or an unknown SKU raises an error.
- **Warehouse:** `dbo.usp_SyncStock` filters supplier feed rows by `@Since`, aggregates by warehouse and SKU, merges quantities into stock levels, and records insert/update counts; errors roll back, log details, and rethrow.

#### Dependencies
- Library views and routines use `loan`, `member`, and `book`; audit triggers write to `loan_audit`. The branch/copy migration references `book`, `loan`, and `member`, and uses PostgreSQL `generate_series`.
- Shop `order_item` references `customer_order` and `product`; its order foreign key cascades on delete.
- Warehouse `dbo.usp_SyncStock` operates on `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`, matching stock by `(WarehouseId, Sku)`.
- The package contains PostgreSQL library SQL, MySQL 8 shop SQL, and SQL Server T-SQL warehouse logic.

#### Cross-cutting
- Transactions protect library seed and migration work, warehouse stock updates and success logging, and database-specific workflows.
- Inventory changes are explicit: library availability/copy status, shop product stock, and warehouse on-hand quantities.
- Integrity and concurrency mechanisms vary by domain: library constraints and foreign keys, shop composite order-line keys and `FOR UPDATE` stock checks, and warehouse transactional `MERGE` with error rollback and logging.
