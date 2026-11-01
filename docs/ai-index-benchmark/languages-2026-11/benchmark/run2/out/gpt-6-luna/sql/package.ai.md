### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:39:55Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements relational data workflows for library lending, online orders and inventory, and warehouse stock synchronization.

#### Purpose
- Defines database schemas, operations, reporting, and audit flows for three business areas: library circulation, shop order fulfillment, and warehouse inventory.
- Supports transactional updates that validate or reserve stock and record operational outcomes.

#### Responsibilities
- **Library catalog and circulation:** Models authors, books, members, branches, individual copies, and loans; provides checkout, return, overdue-fee, audit, and reporting operations.
- **Shop orders and inventory:** Models products, customer orders, and order lines; adds or increments items while reserving stock and recalculating order totals.
- **Warehouse feed synchronization:** Aggregates recent supplier quantities by warehouse and SKU, merges them into stock balances, and logs each run’s outcome.

#### Key units
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate and process library borrowing, returns, and capped overdue charges.
- `author`, `book`, `member`, `branch`, `book_copy`, `loan`, `loan_audit` — library catalog, branch inventory, lending, and event records.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member activity, and popular-title reports.
- `add_order_item(p_order_id, p_sku, p_qty)` — validates product stock, updates an order line, reserves inventory, and recalculates the order total.
- `product`, `customer_order`, `order_item` — shop catalog, order headers, and order lines.
- `dbo.usp_SyncStock(@Since)` — transactionally applies supplier-feed quantities and logs success or failure.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse feed, current balances, and synchronization audit records.

#### Data flow
- Library seed data populates catalog, member, and loan records; checkout and return routines update loans and copy availability, with triggers recording loan events and views deriving reports.
- Shop item additions resolve a SKU, lock and validate available stock, insert or increment an order line, decrement stock, and recalculate `customer_order.total_cents`.
- Warehouse synchronization filters feed rows by `ReceivedAt >= @Since`, aggregates quantities by warehouse and SKU, merges balances into `dbo.StockLevel`, and logs counts; failures roll back stock changes, log the error, and rethrow it.

#### Dependencies
- The library schema links `book` to `author`, `loan` to `book` and `member`, and `book_copy` and `member` to `branch`; it uses PostgreSQL routines, triggers, views, and built-ins.
- The shop uses MySQL 8 tables with foreign keys from `order_item` to `customer_order` and `product`; order deletion cascades to its lines.
- The warehouse uses SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`, plus `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses database constraints and relational keys to enforce data integrity across catalog, inventory, orders, and audit records.
- Protects multi-step updates with transactions or row locks; operational failures are surfaced through SQL exceptions or error signaling.
- Records operational history through library loan-audit triggers and warehouse synchronization logs.
