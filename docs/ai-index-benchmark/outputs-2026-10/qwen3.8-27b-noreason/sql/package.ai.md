### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T10:01:30Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi-dialect SQL package providing domain-specific data models and transactional logic for library lending, e-commerce order processing, and warehouse inventory synchronization.

#### Purpose
- Implements three distinct inventory and transaction management systems using PostgreSQL, MySQL, and T-SQL.
- Provides atomic business logic for loan lifecycle, order item addition with stock reservation, and nightly stock feed merging.

#### Responsibilities
- **Library Management (PostgreSQL)**: Manages book catalog, member accounts, and loan transactions with multi-branch support, automated late fees, and immutable audit logging.
- **E-Commerce Orders (MySQL)**: Handles product inventory, customer orders, and line items with concurrent-safe stock decrement and total recalculation.
- **Warehouse Sync (T-SQL)**: Ingests supplier feeds, merges quantity updates into per-warehouse stock levels, and logs synchronization outcomes.

#### Key units
- **Library**: `fn_checkout_book` (loan initiation), `sp_return_book` (loan closure), `fn_late_fee` (charge calculation), `loan_audit` (trigger-based log), `book_copy` (v2 granular inventory).
- **Shop**: `add_order_item` (atomic stock/order update), `product` (SKU/stock), `customer_order` (status/total), `order_item` (line items).
- **Warehouse**: `dbo.usp_SyncStock` (MERGE-based sync), `dbo.StockLevel` (current inventory), `dbo.SupplierFeed` (raw input), `dbo.SyncLog` (audit trail).

#### Data flow
- **Library**: Checkout validates member/book status, decrements `book_copy`/`book` inventory, inserts `loan`, and triggers `loan_audit`; Return reverses inventory and logs the action.
- **Shop**: `add_order_item` locks `product` row, validates stock, upserts `order_item`, decrements `product.stock`, and recalculates `customer_order.total_cents`.
- **Warehouse**: `dbo.usp_SyncStock` aggregates `dbo.SupplierFeed` by `WarehouseId`/`Sku`, MERGEs into `dbo.StockLevel`, and writes success/error metrics to `dbo.SyncLog` within a transaction.

#### Dependencies
- **PostgreSQL**: Relies on core tables (`author`, `book`, `member`, `loan`), PL/pgSQL triggers, and built-in functions (`generate_series`, `now()`).
- **MySQL**: Depends on InnoDB engine for row locking (`FOR UPDATE`), foreign keys between `order_item` and `product`/`customer_order`, and SQLSTATE error signaling.
- **T-SQL**: Uses SQL Server built-ins (`SYSUTCDATETIME`, `ERROR_MESSAGE`), explicit transactions, and logical joins on `WarehouseId`/`Sku` without explicit foreign keys.

#### Cross-cutting
- **Concurrency Control**: MySQL uses `FOR UPDATE` row locks; T-SQL uses explicit transactions with rollback; PostgreSQL relies on constraint checks and trigger-based consistency.
- **Audit & Logging**: All three domains maintain immutable or append-only logs (`loan_audit`, `SyncLog`) to track state changes and execution outcomes.
- **Error Handling**: Consistent use of business-logic exceptions (SQLSTATE `45000` in MySQL, `THROW` in T-SQL, PL/pgSQL exceptions) for validation failures.
- **Inventory Granularity**: Library migrates from aggregate counters to granular `book_copy` rows; Shop and Warehouse maintain per-SKU stock levels with strict non-negative constraints.
