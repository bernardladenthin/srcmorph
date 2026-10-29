### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:00:47Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database systems for managing inventory and transactional lifecycles across library lending, e-commerce retail, and warehouse synchronization.

#### Purpose
* Provide specialized data management for three distinct domains: library book loans, e-commerce order processing, and warehouse stock synchronization.
* Ensure data integrity and concurrency control for inventory-heavy operations.

#### Responsibilities
* **Library Management**: Handles book inventory, member registrations, loan lifecycles (checkout/return), and overdue penalty calculations.
* **E-commerce Operations**: Manages product catalogs, customer order headers, line-item processing, and atomic stock reservations.
* **Warehouse Logistics**: Synchronizes master stock levels using supplier feed data and maintains operational sync logs.

#### Key units
* **Library**: `author`, `book`, `member`, `loan` (core entities); `fn_checkout_book` and `sp_return_book` (loan logic); `fn_late_fee` (penalty calculation); `loan_audit` (event logging).
* **Shop**: `product` (SKU/stock); `customer_order` (headers); `order_item` (line items); `add_order_item` (transactional orchestration).
* **Warehouse**: `dbo.StockLevel` (on-hand quantities); `dbo.SupplierFeed` (landing zone); `dbo.usp_SyncStock` (merge logic); `dbo.SyncLog` (audit trail).

#### Data flow
* **Lending Pipeline**: `fn_checkout_book` $\rightarrow$ member/book validation $\rightarrow$ `loan` creation $\rightarrow$ `trg_loan_audit_fn` $\rightarrow$ `loan_audit`.
* **Order Pipeline**: `add_order_item` $\rightarrow$ `product` stock lock $\rightarrow$ `order_item` update $\rightarrow$ `product` stock decrement $\rightarrow$ `customer_order` total update.
* **Sync Pipeline**: `dbo.SupplierFeed` $\rightarrow$ aggregation by Warehouse/SKU $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ `dbo.SyncLog`.

#### Dependencies
* **Internal**: Cross-table foreign key dependencies within each domain (e.g., `order_item` to `product`, `loan` to `member`).
* **External**: PostgreSQL / PL/pgSQL (Library/Shop) and SQL Server/T-SQL (Warehouse).

#### Cross-cutting
* **Concurrency & Locking**: Use of `FOR UPDATE` row-level locking in Shop and `MERGE` statements in Warehouse to prevent race conditions.
* **Audit & Logging**: Automated event tracking via triggers in Library (`loan_audit`) and explicit logging tables in Warehouse (`SyncLog`).
* **Integrity Constraints**: Heavy reliance on `CHECK` constraints for business rules (e.g., birth year ranges, stock levels) and `TRY...CATCH` blocks for error handling.
* **Schema Evolution**: Transition from aggregate counters to normalized entity tracking (e.g., `book_copy` and `branch` in Library).
