### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:03:24Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library circulation, web-shop order and inventory management, and warehouse stock synchronization.

#### Purpose
- Defines relational data and operational SQL for lending, product ordering, and supplier-driven stock updates.
- Supports transactional changes, reporting, and audit or synchronization records across these domains.

#### Responsibilities
- **Library catalog and circulation (`library/`):** Models authors, books, members, branches, physical copies, and loans; handles checkout, returns, late fees, audit history, and circulation reports.
- **Shop orders and stock reservation (`shop/`):** Stores products, customer orders, and line items; validates and reserves product stock when adding items, then recalculates order totals.
- **Warehouse synchronization (`warehouse/`):** Aggregates recent supplier feed quantities by warehouse and SKU, applies them to stock levels, and logs synchronization results.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, circulation, and copy-level inventory tables.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and capped overdue-fee operations.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library circulation and borrowing reports.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record library checkout and return events.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, orders, order lines, and stock-reserving item addition.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since)` — warehouse feed, inventory, run log, and synchronization procedure.

#### Data flow
- **Library:** Catalog and member records are referenced by loans; checkout validates member and copy availability, creates a loan, and triggers audit recording. Return marks an open loan returned, restores availability, and triggers audit recording; reporting views summarize loan activity.
- **Shop:** `add_order_item` locks a product found by SKU, checks stock, inserts or increments an order line at the current price, decrements inventory, and recalculates the order total.
- **Warehouse:** `usp_SyncStock` filters supplier feed rows since `@Since`, aggregates quantities by warehouse and SKU, merges results into `StockLevel`, counts insert/update actions, and logs success or failure.

#### Dependencies
- Library routines, views, and triggers use `author`, `book`, `member`, `loan`, `book_copy`, `branch`, and `loan_audit`.
- Shop `order_item` references `customer_order` and `product`; `add_order_item` reads and updates all three order and catalog tables.
- Warehouse `usp_SyncStock` reads `dbo.SupplierFeed` and writes `dbo.StockLevel` and `dbo.SyncLog`.

#### Cross-cutting
- Relational constraints and indexes enforce identities, relationships, uniqueness, and valid statuses or counts.
- Operations protect inventory changes: shop product checks use `FOR UPDATE`; warehouse synchronization uses a transaction and rolls back on failure.
- Error handling varies by workflow: shop reports unknown SKU or insufficient stock, library rejects invalid checkout or return requests, and warehouse logs `ERROR_MESSAGE()` before rethrowing.
- Monetary values are stored in integer cents in the shop; warehouse timestamps use UTC, while library routines use current date/time values.
