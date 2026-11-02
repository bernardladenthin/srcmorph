### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:02:17Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library lending, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines schemas, operational routines, reports, and audit logging for three domains: library circulation, shop orders, and warehouse inventory.
- Supports inventory changes driven by loans and returns, order-line additions, and supplier-feed synchronization.

#### Responsibilities
- **Library catalog and lending:** Stores authors, books, members, loans, branches, and individual book copies; provides checkout, return, overdue, borrowing-statistics, popular-book, and late-fee operations.
- **Shop orders and stock reservation:** Stores products, customer orders, and order lines; adds or increments lines while checking and reserving product stock and recalculating order totals.
- **Warehouse synchronization:** Aggregates recent supplier quantities by warehouse and SKU, merges them into stock levels, and records success or failure details.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, circulation, and branch-level inventory records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and capped overdue-fee operations.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, borrowing-activity, and popular-title reports.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — library loan lifecycle audit storage and trigger.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, order records, and stock-aware order-line updates.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since DATETIME2(0))` — supplier input, warehouse stock, synchronization log, and sync procedure.

#### Data flow
- Library checkout validates membership and availability, creates a loan, and records a checkout audit event; return closes an open loan, restores inventory, and records a return event. Reports derive activity and overdue status from loan records.
- Shop `add_order_item` locks a matching product with sufficient stock, inserts or increments an order line, subtracts stock, and recalculates the order total; unavailable stock or unknown SKU raises an error.
- Warehouse `dbo.usp_SyncStock` aggregates supplier quantities since the cutoff, merges them into `dbo.StockLevel`, and logs merge counts; on failure it rolls back active work, logs the error, and rethrows.

#### Dependencies
- Library routines and reports use `loan`, `member`, `book`, and `author`; migration introduces `branch` and `book_copy` and uses PostgreSQL `generate_series()`.
- Shop `add_order_item` reads and writes `product`, `order_item`, and `customer_order`; order lines reference orders and products.
- Warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Database integrity constraints enforce keys, relationships, uniqueness, and domain checks where defined; each domain uses its own SQL dialect and object conventions.
- Inventory is updated as part of each workflow: library checkout/return adjusts lending availability, shop order-line updates reserve stock under a row lock, and warehouse synchronization uses a transaction around stock changes and success logging.
- Error handling is explicit in operational routines: library rejects invalid checkout/return cases, shop signals unavailable or unknown products, and warehouse sync rolls back, logs, and rethrows failures.
