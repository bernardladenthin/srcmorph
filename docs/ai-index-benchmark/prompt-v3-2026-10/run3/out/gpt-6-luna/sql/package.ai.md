### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:39:01Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements SQL-backed workflows for library lending, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines domain data models and operational database routines for borrowing, inventory reservation, and supplier-driven stock updates.
- Provides reporting, auditing, migration, and execution logging for these workflows.

#### Responsibilities
- **Library catalog and lending:** Stores authors, books, members, loans, branch copies, and borrowing history; supports checkout, return, late-fee calculation, auditing, and loan reports.
- **Shop ordering and inventory:** Stores products, customer orders, and order items; adds items while reserving stock and recalculating order totals.
- **Warehouse synchronization:** Aggregates recent supplier-feed quantities by warehouse and SKU, applies stock updates, and logs synchronization outcomes.

#### Key units
- `author`, `book`, `member`, `loan` — library catalog, borrower, and loan records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and late-fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return transitions.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library operational and lending reports.
- `branch`, `book_copy`, `04_migration_v2.sql` — branch-based copy inventory and migration from book-level counts.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, orders, line items, and stock-reserving item processing.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — supplier inputs, warehouse stock, sync logging, and synchronization procedure.

#### Data flow
- **Library:** Seed data populates catalog, members, and loans; checkout and return update loan and availability state, audit triggers record transitions, and views report loan activity. Migration creates branch copies and connects members and loans to branch-aware inventory.
- **Shop:** `add_order_item` validates and locks eligible product stock, inserts or increments a line item, decrements inventory, and recalculates the order total.
- **Warehouse:** `usp_SyncStock` filters supplier rows by `@Since`, aggregates quantities by warehouse and SKU, merges them into stock levels, and logs counts; failures are rolled back, logged, and rethrown.

#### Dependencies
- Library views, routines, and audit triggers operate on `loan`, `member`, `book`, and `author`; branch inventory links `book_copy` to `book` and `branch`.
- Shop `order_item` references `customer_order` and `product`; totals derive from item quantities and unit prices.
- Warehouse `usp_SyncStock` depends on `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- Database-specific features include PostgreSQL date and sequence built-ins, MySQL InnoDB and row locking, and SQL Server `MERGE` and error-handling built-ins.

#### Cross-cutting
- Constraints and foreign keys enforce record relationships and domain validity; shop SKU uniqueness and library copy and loan constraints protect data integrity.
- Operations handle invalid or unavailable resources explicitly: library routines raise exceptions, shop item processing signals a SQL exception, and warehouse synchronization rolls back, logs, and rethrows failures.
- Transactions protect library seed and inventory migration operations, and warehouse stock updates and success logging.
