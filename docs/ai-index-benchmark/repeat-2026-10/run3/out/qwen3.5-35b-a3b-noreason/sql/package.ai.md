### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:41Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides a unified data layer for inventory management, lending operations, and supply chain synchronization across library, retail, and warehouse domains.

#### Purpose
- Orchestrates inventory lifecycles, transactional lending logic, and stock synchronization for multi-branch and multi-warehouse environments.
- Ensures data integrity through strict schema constraints, atomic transactions, and immutable audit trails.

#### Responsibilities
- **Library Lifecycle Management**: Models authors, books, members, and loans with support for multi-branch operations and per-copy tracking.
- **Retail Order Processing**: Atomically adds line items to orders, validates stock, and recalculates totals using row-level locking.
- **Warehouse Synchronization**: Merges nightly supplier feeds into stock levels and logs synchronization metrics.
- **Audit & Reporting**: Maintains immutable transaction logs and provides read-optimized views for business intelligence.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy`: Core PostgreSQL entities for library inventory and multi-location tracking.
- `loan_audit`: Immutable log table capturing `CHECKOUT` and `RETURN` actions via triggers.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`: PostgreSQL functions/procedures handling lending workflows and fee calculations.
- `overdue_loans`, `member_loan_stats`, `popular_books`: SQL views and materialized views for operational and analytical reporting.
- `add_order_item`: MySQL stored procedure for atomic order line insertion and stock reservation.
- `product`, `customer_order`, `order_item`: MySQL tables managing retail inventory and order aggregation.
- `dbo.usp_SyncStock`: T-SQL procedure merging supplier feeds into `dbo.StockLevel`.
- `dbo.SupplierFeed`, `dbo.SyncLog`: Source and audit tables for warehouse synchronization tracking.

#### Data flow
- **Library**: Schema migrations evolve single-branch models to multi-branch; triggers intercept loan state changes to write to `loan_audit`; views aggregate data for reporting.
- **Retail**: Procedure locks `product` rows, decrements stock, updates `order_item`, and recalculates `customer_order.total_cents` in a single transaction.
- **Warehouse**: `@Since` parameter filters `SupplierFeed`; aggregated data drives `MERGE` operations on `StockLevel`; row counts and errors are written to `SyncLog`.

#### Dependencies
- **Internal**: `loan` table is central to library logic; `product` and `customer_order` drive retail transactions; `StockLevel` is the target for warehouse sync.
- **External**: PostgreSQL native types (`BIGSERIAL`, `TIMESTAMPTZ`); MySQL `INT UNSIGNED` for monetary precision; T-SQL system functions (`SYSUTCDATETIME`, `ERROR_MESSAGE`).
- **Migration**: `04_migration_v2.sql` depends on `01_schema.sql` to alter the library model.

#### Cross-cutting
- **Constraint Enforcement**: Heavy use of `CHECK` constraints (e.g., `copies_free <= copies_total`, `due_on > loaned_on`) and `UNIQUE` constraints.
- **Concurrency Control**: `SELECT ... FOR UPDATE` in MySQL prevents race conditions; `TRY...CATCH` blocks in T-SQL ensure atomicity.
- **Audit Pattern**: Dedicated `loan_audit` table and trigger mechanism for libraries; `SyncLog` table for warehouse operations.
- **State Management**: `book_copy` status column enforces `AVAILABLE`, `ON_LOAN`, `LOST` states; `order_item` handles quantity updates.
