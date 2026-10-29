### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:33:20Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas and logic for managing inventory, circulation, and order fulfillment across library, e-commerce, and warehouse domains.

#### Purpose
* Provide specialized inventory management systems for lending (library), retail (shop), and logistics (warehouse).
* Implement atomic transactional logic for stock updates, loan lifecycles, and supplier synchronization.

#### Responsibilities
* **Lending Management**: Handles book catalogs, member registrations, loan checkouts/returns, and overdue penalty calculations.
* **Retail Order Processing**: Manages product SKUs, customer order headers, and atomic stock reservation during item addition.
* **Logistics Synchronization**: Aggregates raw supplier feed data into consolidated warehouse stock levels with execution auditing.

#### Key units
* **Library**: `fn_checkout_book` and `sp_return_book` (loan lifecycle), `loan_audit` (transaction log), `book_copy` and `branch` (multi-site inventory), and `popular_books` (demand analytics).
* **Shop**: `add_order_item` (atomic stock/order update), `product` (SKU and pricing), and `customer_order` (order tracking).
* **Warehouse**: `dbo.usp_SyncStock` (aggregation orchestrator), `dbo.StockLevel` (master ledger), `dbo.SupplierFeed` (raw input), and `dbo.SyncLog` (sync history).

#### Data flow
* **Lending**: Member validation $\rightarrow$ Inventory decrement $\rightarrow$ Loan record creation $\rightarrow$ Triggered audit logging.
* **Retail**: Order item request $\rightarrow$ Row-level product lock $\rightarrow$ Stock decrement $\rightarrow$ Order total recalculation.
* **Logistics**: Supplier feed timestamp filter $\rightarrow$ SKU/Warehouse aggregation $\rightarrow$ `MERGE` into stock levels $\rightarrow$ Sync log update.

#### Dependencies
* **Internal**: Cross-table dependencies between orders/products (Shop), loans/books/members (Library), and feeds/stock levels (Warehouse).
* **External**: PostgreSQL/PL/pgSQL (Library) and T-SQL (Warehouse).

#### Cross-cutting
* **Atomicity & Consistency**: Use of T-SQL TRY/CATCH blocks (Warehouse), row-level locking (Shop), and database-level `CHECK` constraints (Library).
* **Auditability**: Implementation of dedicated logging mechanisms via triggers (`loan_audit` in Library) and execution logs (`SyncLog` in Warehouse).
* **Performance Optimization**: Use of B-tree and partial indexes for active loans (Library) and `MERGE` statements for bulk updates (Warehouse).
