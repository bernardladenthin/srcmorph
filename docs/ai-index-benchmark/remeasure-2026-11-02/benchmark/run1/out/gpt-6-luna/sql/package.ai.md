### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:30:10Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library circulation, webshop order fulfillment, and warehouse stock replenishment.

#### Purpose
- Defines schemas, routines, reporting, and synchronization workflows for three distinct domains: library lending, online sales, and warehouse inventory.
- Enforces domain rules while updating inventory and recording operational outcomes.

#### Responsibilities
- **Library catalog and circulation:** Manage authors, books, members, branches, copies, loans, checkout and return operations, late fees, audit events, and circulation reports.
- **Shop orders and stock:** Maintain products, customer orders, and order items; add requested quantities while checking and decrementing stock and recalculating totals.
- **Warehouse replenishment:** Aggregate recent supplier-feed quantities by warehouse and SKU, merge them into stock levels, and log synchronization results.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, branch inventory, and lending records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and overdue-fee workflows.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and first-time return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library circulation reports, including a materialized popular-books ranking.
- `product`, `customer_order`, `order_item`, `add_order_item(p_order_id, p_sku, p_qty)` — webshop catalog, order lines, and stock-checked item addition with total recalculation.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since)` — supplier input, warehouse stock, synchronization logging, and transactional stock updates.

#### Data flow
- **Library:** Seed catalog and member data; checkout validates eligibility and availability, creates a loan, and triggers an audit event. Return updates the loan and availability, triggers a return audit event, and feeds reporting views.
- **Shop:** `add_order_item` matches a SKU and verifies stock; on success it inserts or increments an order line, decrements stock, and recalculates the order total.
- **Warehouse:** `dbo.usp_SyncStock` filters supplier rows by `@Since`, aggregates quantities by warehouse and SKU, merges balances into `dbo.StockLevel`, logs counts, and commits; failures roll back, log the error, and rethrow.

#### Dependencies
- **Library PostgreSQL:** Workflows and reports use `author`, `book`, `member`, and `loan`; branch and copy migration connects `branch`, `book_copy`, members, and loans.
- **Shop MySQL 8:** `order_item` references `customer_order` and `product`; order-item deletion cascades from its order.
- **Warehouse SQL Server T-SQL:** `dbo.usp_SyncStock` operates on `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`; the stock primary key matches warehouse and SKU.

#### Cross-cutting
- Database constraints and indexes support uniqueness, referential integrity, validation, and lookup patterns across the domain schemas.
- Inventory changes are coordinated with business operations: library copy availability and shop stock are updated during circulation or ordering; warehouse stock updates run transactionally.
- Error handling is domain-specific: the shop routine signals unknown SKU or insufficient stock with SQLSTATE `45000`; warehouse synchronization rolls back, logs `ERROR_MESSAGE()`, and rethrows.
