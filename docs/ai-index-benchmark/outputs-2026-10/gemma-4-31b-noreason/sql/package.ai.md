### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T09:56:13Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas for managing inventory and transactional lifecycles across library lending, e-commerce retail, and warehouse synchronization domains.

#### Purpose
* Provide specialized relational models for tracking physical assets (books, retail products, warehouse stock).
* Automate state transitions for loans, customer orders, and supplier inventory updates.

#### Responsibilities
* **Lending Management (Library)**: Handles book inventories, member registrations, loan checkouts/returns, and late fee calculations.
* **Retail Transaction Processing (Shop)**: Manages product catalogs, customer order headers, and atomic order-item processing.
* **Inventory Synchronization (Warehouse)**: Aggregates supplier feed data into consolidated warehouse stock levels with execution logging.

#### Key units
* **Library**: `author`, `book`, `member`, `loan` (core entities); `fn_checkout_book` and `sp_return_book` (lifecycle logic); `fn_late_fee` (penalty calculation); `loan_audit` (event logging).
* **Shop**: `product` (SKU/pricing); `customer_order` (order tracking); `order_item` (line items); `add_order_item` (atomic stock and total update procedure).
* **Warehouse**: `dbo.StockLevel` (current on-hand totals); `dbo.SupplierFeed` (incoming updates); `dbo.SyncLog` (execution metrics); `dbo.usp_SyncStock` (aggregation and merge logic).

#### Data flow
* **Library**: Checkout/Return requests $\rightarrow$ validation/inventory update $\rightarrow$ `loan` record $\rightarrow$ `loan_audit` trigger $\rightarrow$ reporting views (`overdue_loans`, `popular_books`).
* **Shop**: `add_order_item` $\rightarrow$ product stock validation $\rightarrow$ `order_item` insertion $\rightarrow$ `product.stock` decrement $\rightarrow$ `customer_order.total_cents` recalculation.
* **Warehouse**: `dbo.SupplierFeed` $\rightarrow$ date-filtered aggregation $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ `dbo.SyncLog` summary.

#### Dependencies
* **Internal**: Cross-table foreign key constraints across all three domains.
* **External**: PostgreSQL (PL/pgSQL) for library/shop; T-SQL for warehouse.

#### Cross-cutting
* **Audit & Logging**: Use of dedicated audit tables (`loan_audit`, `dbo.SyncLog`) and triggers to track state changes and process execution.
* **Atomicity & Integrity**: Implementation of stored procedures for complex updates, `CHECK` constraints for data validation, and T-SQL TRY/CATCH blocks for error handling.
* **State Tracking**: Use of timestamps and barcodes/SKUs to maintain precise inventory windows and unique asset identification.
