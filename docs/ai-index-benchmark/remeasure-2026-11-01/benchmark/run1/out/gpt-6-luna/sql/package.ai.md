### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:24:05Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library lending, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines SQL schemas, stored routines, reporting, and audit or execution logging for three operational domains.
- Handles inventory changes and related records, including loans, order lines, and supplier-driven stock updates.

#### Responsibilities
- **Library lending (`library/`):** Model catalog, members, branches, copies, and loans; support checkout, returns, overdue fees, reports, sample data, and loan auditing.
- **Shop orders (`shop/`):** Store products and customer orders; add or increase order lines while reserving stock and recalculating totals.
- **Warehouse synchronization (`warehouse/`):** Aggregate recent supplier quantities into stock levels and log synchronization outcomes.

#### Key units
- `author`, `book`, `member`, `branch`, `book_copy`, `loan` — library catalog, membership, branch inventory, and borrowing records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and capped overdue-fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record library checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library loan and catalog reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, order records, and stock-reserving order-line processing.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — merge recent supplier quantities into warehouse stock and log the result.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse input, balance, and synchronization-log tables.

#### Data flow
- **Library:** Migrate book-level counts into branch-based `book_copy` inventory; checkout and return routines update loans and copy availability, while a trigger audits loan events. Views derive reports from catalog, member, and loan records.
- **Shop:** `add_order_item` finds and locks a stocked product by SKU, inserts or increments its order line, decrements stock, and recomputes the order total.
- **Warehouse:** `usp_SyncStock` filters supplier rows by `@Since`, aggregates quantities by warehouse and SKU, merges them into `StockLevel`, and logs inserted/updated counts; failures roll back, log the error, and rethrow.

#### Dependencies
- **Library:** PostgreSQL relationships connect books to authors, loans to books and members, and copies and members to branches; routines and reports operate on these records.
- **Shop:** MySQL `order_item` references `customer_order` and `product`; `add_order_item` reads and updates all three tables.
- **Warehouse:** SQL Server `dbo.usp_SyncStock` uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`, plus `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses different database platforms by domain: PostgreSQL for library lending, MySQL 8 for shop orders, and SQL Server for warehouse synchronization.
- Enforces domain constraints and handles operational errors: blocked members or unavailable copies, unknown SKUs or insufficient stock, and synchronization rollback with error logging.
- Uses transaction handling for library sample-data loading and warehouse synchronization; no explicit transaction handling is defined for the shop procedure.
