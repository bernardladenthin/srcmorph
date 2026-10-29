### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T21:46:36Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of inventory and resource management systems covering library loans, retail order processing, and warehouse stock synchronization.

#### Purpose
* Manage diverse inventory types including books, retail products, and warehouse stock.
* Handle transactional lifecycles for borrowing, ordering, and stock synchronization.

#### Responsibilities
* **Library Management**: Managing book inventory, member registration, loan lifecycles (checkouts, returns), and overdue reporting.
* **Retail Order Processing**: Tracking product SKUs, managing customer orders, and executing atomic stock reservations.
* **Warehouse Synchronization**: Aggregating supplier feed data into master stock records with execution logging.

#### Key units
* **Library**: `author`, `book`, `member`, `loan`, `branch`, and `book_copy` tables; `fn_checkout_book`, `sp_return_book`, and `fn_late_fee` routines; `overdue_loans`, `member_loan_stats`, and `popular_books` views.
* **Shop**: `product`, `customer_order`, and `order_item` tables; `add_order_item` procedure for stock validation and order updates.
* **Warehouse**: `dbo.StockLevel` (master stock), `dbo.SupplierFeed` (source updates), `dbo.SyncLog` (audit), and `dbo.usp_SyncStock` orchestration procedure.

#### Data flow
* **Library Loan Cycle**: `fn_checkout_book`/`sp_return_book` $\rightarrow$ `loan` state changes $\rightarrow$ `loan_audit` trigger $\rightarrow$ operational reporting views.
* **Retail Order Cycle**: `add_order_item` $\rightarrow$ `product` stock validation $\rightarrow$ `order_item` insertion $\rightarrow$ `customer_order` total update.
* **Warehouse Sync Cycle**: `dbo.SupplierFeed` (filtered by date) $\rightarrow$ Aggregated Quantities $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ `dbo.SyncLog`.

#### Dependencies
* External: PostgreSQL (library), MySQL 8 (shop), and T-SQL/SQL Server (warehouse).

#### Cross-cutting
* **Auditability**: Use of `loan_audit` triggers in library and `dbo.SyncLog` for warehouse synchronization.
* **Data Integrity**: Implementation of `CHECK` constraints (library), `ON DELETE CASCADE` (shop), and transactional rollbacks with `ERROR_MESSAGE()` logging (warehouse).
* **Inventory Logic**: Consistent patterns of stock validation and decrement/increment across all three domains.
