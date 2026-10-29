### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:10:47Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Multi-database inventory and transaction management system handling library circulation, e-commerce order processing, and warehouse stock reconciliation.

#### Purpose
- Implements distinct database schemas and business logic routines for three separate domains: a multi-branch lending library, a web shop, and a warehouse inventory system.
- Provides atomic transactional integrity for stock adjustments, loan circulation, and order total calculations across PostgreSQL, MySQL, and SQL Server environments.

#### Responsibilities
- **Library Management (PostgreSQL)**: Manages book inventory, member accounts, and loan circulation with support for branch-aware copy tracking, late fee calculation, and audit logging.
- **E-Commerce (MySQL)**: Handles product catalog, customer orders, and line items with atomic stock reservation and order total recalculation.
- **Warehouse Reconciliation (SQL Server)**: Synchronizes on-hand stock levels from supplier feeds using merge operations and maintains an audit trail of sync executions.

#### Key units
- **Library**:
    - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
    - Routines: `fn_checkout_book` (validates and decrements inventory), `sp_return_book` (processes returns), `fn_late_fee` (calculates penalties), `trg_loan_audit` (logs events).
    - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**:
    - Tables: `product`, `customer_order`, `order_item`.
    - Procedure: `add_order_item` (locks stock, validates quantity, updates totals).
- **Warehouse**:
    - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
    - Procedure: `dbo.usp_SyncStock` (merges feed data into stock levels and logs results).

#### Data flow
- **Library Checkout/Return**: Member/Book IDs trigger validation and inventory updates in `fn_checkout_book` or `sp_return_book`, followed by automatic audit logging via triggers.
- **Shop Order Item**: `add_order_item` locks the `product` row, validates stock, updates `order_item` and `product.stock`, and recalculates `customer_order.total_cents` atomically.
- **Warehouse Sync**: `dbo.usp_SyncStock` aggregates `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`, merges them into `dbo.StockLevel`, and records execution metrics in `dbo.SyncLog`.

#### Dependencies
- **Library**: PostgreSQL dialect features (`PL/pgSQL`, `generate_series`, partial indexes, materialized views).
- **Shop**: MySQL 8 InnoDB engine, utf8mb4 character set.
- **Warehouse**: SQL Server built-ins (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`).

#### Cross-cutting
- **Concurrency Control**: Uses row locking (`FOR UPDATE` in MySQL) and transactional isolation to prevent race conditions during stock decrements and merges.
- **Error Handling**: Implements specific error signaling (SQLSTATE `45000` in MySQL, `TRY...CATCH` with `THROW` in SQL Server) and audit logging for failures.
- **Audit Trails**: Maintains persistent logs of state changes via `loan_audit` (library) and `dbo.SyncLog` (warehouse).
- **Data Integrity**: Enforces referential integrity, check constraints for valid states (e.g., `copies_free <= copies_total`, `stock >= 0`), and atomic transaction boundaries for all multi-table updates.
