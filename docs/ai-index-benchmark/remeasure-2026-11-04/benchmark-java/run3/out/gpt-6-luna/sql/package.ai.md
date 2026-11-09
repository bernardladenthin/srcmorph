### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:47:12Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Defines database workflows for library lending, retail order fulfillment, and warehouse stock synchronization.

#### Purpose
- Store and manage catalog, membership, lending, product, order, and warehouse inventory data.
- Provide operational SQL for loans, order-item stock reservation, and supplier-driven stock updates.

#### Responsibilities
- **Library lending and reporting:** Catalog authors and books, manage members and loans, report borrowing activity, audit loan events, and evolve inventory to branch-level copies.
- **Shop orders and inventory:** Store products, customer orders, and order lines; add or increment order items while reserving stock and recalculating totals.
- **Warehouse synchronization:** Aggregate recent supplier quantities by warehouse and SKU, merge them into stock levels, and log run outcomes.

#### Key units
- `author`, `book`, `member`, `loan` — library catalog, membership, and lending records.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member-activity, and popular-title reports.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and capped late-fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record library checkout and first-return events.
- `branch`, `book_copy` — branch locations and individually tracked library copies.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, orders, line items, and stock-reserving line updates.
- `dbo.usp_SyncStock(@Since)` — transactionally merges recent supplier quantities into warehouse stock and logs the result.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse quantity input, current stock, and synchronization outcome records.

#### Data flow
- Library seed data populates catalog, members, and loans; checkout and return operations update loan and availability records and trigger audit events, while views derive borrowing reports. The migration converts aggregate book counts into branch-associated `book_copy` records.
- Shop `add_order_item` looks up a product by SKU, validates stock, inserts or increments an order line, decrements inventory, and recalculates the order total.
- Warehouse `usp_SyncStock` filters supplier rows by `@Since`, aggregates quantities by warehouse and SKU, merges them into `StockLevel`, and records insert/update counts or an error in `SyncLog`.

#### Dependencies
- **Library:** PostgreSQL tables, constraints, indexes, `generate_series`, and foreign-key relationships connect catalog, member, loan, branch, and copy data.
- **Shop:** MySQL foreign keys connect `order_item` to `customer_order` and `product`; `add_order_item` reads and updates those tables.
- **Warehouse:** SQL Server T-SQL uses `SYSUTCDATETIME()` for timestamps and `ERROR_MESSAGE()` for error details; `usp_SyncStock` reads `SupplierFeed` and `StockLevel` and writes `StockLevel` and `SyncLog`.

#### Cross-cutting
- The package spans PostgreSQL, MySQL, and SQL Server; each subpackage uses database-native constraints and operations.
- Operations validate unavailable or invalid inputs: library loan routines raise exceptions, shop item updates signal SQLSTATE `45000`, and warehouse synchronization rolls back, logs failures, and rethrows.
- Inventory is represented differently by domain: library copies are branch-associated after migration, shop stock is held on products, and warehouse stock is keyed by `(WarehouseId, Sku)`.
