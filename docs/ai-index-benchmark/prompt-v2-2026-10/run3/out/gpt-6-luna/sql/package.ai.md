### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:01:30Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides SQL data models and operational workflows for library circulation, shop ordering, and warehouse inventory synchronization.

#### Purpose
- Defines three domain-specific database packages: lending-library management, product ordering, and warehouse stock updates.
- Combines catalog and transaction storage with routines for circulation, stock reservation, reporting, and supplier-feed synchronization.

#### Responsibilities
- **Library catalog and circulation:** Store authors, books, members, loans, branches, and individual copies; process checkouts and returns, calculate late fees, and audit loan changes.
- **Library reporting and migration:** Report overdue loans, member activity, and popular titles; migrate inventory from book-level counts to branch-level copies.
- **Shop inventory and orders:** Store products, customer orders, and order lines; add or increment order items while reserving stock and recalculating totals.
- **Warehouse synchronization:** Aggregate recent supplier quantities into stock levels and log synchronization counts or failures.

#### Key units
- `library.author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, circulation, and branch inventory records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate and process lending actions and calculate overdue fees.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan checkout and return transitions.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report overdue circulation, member borrowing, and frequently borrowed titles.
- `shop.product`, `customer_order`, `order_item`, `add_order_item` — manage shop catalog and orders, reserve stock, and update order totals.
- `dbo.usp_SyncStock(@Since)`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — apply recent supplier quantities to warehouse stock and record outcomes.

#### Data flow
- Library seed data populates catalog, members, and loans; checkout and return routines update circulation and inventory, with triggers recording audit events. Reports derive from loan and catalog records.
- The library v2 migration creates a Main branch and book copies from existing counts, then assigns branch and copy references.
- Shop `add_order_item` locks a product with sufficient stock, inserts or increments an order line, decrements inventory, and recomputes the order total.
- Warehouse `usp_SyncStock` filters supplier records by cutoff, groups quantities by warehouse and SKU, merges aggregates into stock levels, then logs counts; on failure it rolls back, logs the error, and rethrows.

#### Dependencies
- Library routines, reports, and migration coordinate `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, and `book_copy`; migration uses PostgreSQL `generate_series`.
- Shop `add_order_item` coordinates `product`, `order_item`, and `customer_order`; order lines reference products and orders.
- Warehouse `dbo.usp_SyncStock` coordinates `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`; timestamps use SQL Server `SYSUTCDATETIME()`.

#### Cross-cutting
- The package spans PostgreSQL library objects, a MySQL 8 shop schema and procedure, and SQL Server warehouse synchronization.
- Transactions and integrity constraints protect domain updates; shop product-row locking and warehouse rollback-on-failure handling address concurrent or failed operations.
- Library checkout and return routines use book-level free-copy counts, while the v2 migration replaces those counters with branch-level `book_copy` inventory.
