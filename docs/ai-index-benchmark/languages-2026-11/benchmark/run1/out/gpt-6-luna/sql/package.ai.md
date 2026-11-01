### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:17:29Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides SQL data models and transactional workflows for library lending, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines schemas, routines, reports, migrations, and seed data for three operational domains: library circulation, retail orders, and warehouse inventory.
- Tracks inventory changes alongside related lending, purchasing, or synchronization activity.

#### Responsibilities
- **Library lending:** Catalog books and members, process checkouts and returns, calculate late fees, report borrowing activity, audit loan events, and migrate inventory to branch-assigned copies.
- **Shop ordering:** Manage products, customer orders, and order lines; add items while reserving stock and recalculating order totals.
- **Warehouse synchronization:** Aggregate recent supplier quantities by warehouse and SKU, merge them into stock levels, and log synchronization outcomes.

#### Key units
- `author`, `book`, `member`, `loan` — library catalog, member, and borrowing records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and overdue-charge operations.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member activity, and popular-title reports.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record library checkout and return events.
- `branch`, `book_copy` — branch and individual-copy inventory introduced by the library migration.
- `product`, `customer_order`, `order_item` — shop catalog, orders, and order lines.
- `add_order_item` — validates and reserves product stock, updates an order line, and recalculates the order total.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — synchronizes aggregated supplier quantities into warehouse stock and logs results.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse input quantities, on-hand stock, and synchronization outcomes.

#### Data flow
- Library seed data populates catalog, member, and loan records; reports derive results from them. Checkout and return operations update loans and inventory, with triggers writing audit events. Migration converts book-level counts into branch-assigned copies and links loans and members to inventory locations.
- Shop `add_order_item` locks a matching product with sufficient stock, inserts or increments an order line, decrements stock, and recalculates the order total.
- Warehouse `usp_SyncStock` filters supplier feeds by `@Since`, aggregates quantities by warehouse and SKU, merges them into `StockLevel`, and logs insert/update counts; on failure it rolls back, logs the error, and rethrows.

#### Dependencies
- Library routines and reports use `author`, `book`, `member`, and `loan`; the migration also uses `branch`, `book_copy`, and PostgreSQL `generate_series`.
- Shop `add_order_item` reads and updates `product`, `customer_order`, and `order_item`; order lines reference orders and products.
- Warehouse `dbo.usp_SyncStock` reads `dbo.SupplierFeed`, updates `dbo.StockLevel`, and writes `dbo.SyncLog`.

#### Cross-cutting
- Each domain uses database transactions or locking to protect inventory updates: library seed and migration transactions, MySQL InnoDB row locks for shop stock, and a warehouse synchronization transaction with rollback and error logging.
- The package spans PostgreSQL (library), MySQL 8 with InnoDB and `utf8mb4` (shop), and SQL Server-style `dbo` objects and `DATETIME2` (warehouse).
