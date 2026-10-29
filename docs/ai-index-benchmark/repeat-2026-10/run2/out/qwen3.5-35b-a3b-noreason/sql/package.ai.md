### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:11Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides domain-specific data persistence and transactional logic for library lending, retail order processing, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server environments.

#### Purpose
- Implements core data models and transactional procedures for multi-branch library operations, e-commerce order management, and supply chain stock synchronization.
- Enforces data integrity, inventory consistency, and audit trails through database-native constraints, triggers, and stored procedures.

#### Responsibilities
- **Library Operations**: Manages book inventory, patron loans, late fees, and historical schema evolution from global counts to physical copy tracking.
- **Retail Transactions**: Validates stock availability, reserves inventory, and updates order totals atomically during line item addition.
- **Supply Chain Sync**: Aggregates supplier feed data, merges stock levels, and logs execution metrics for nightly warehouse updates.
- **Audit & Reporting**: Maintains immutable logs for state changes and provides materialized views for analytics on overdue items and product popularity.

#### Key units
- `author`, `book`, `member`, `loan`: Core library entities for bibliographic and transactional data.
- `branch`, `book_copy`: Multi-branch extensions tracking physical locations and individual item status.
- `loan_audit`: Immutable log table capturing checkout and return actions via triggers.
- `fn_checkout_book`, `sp_return_book`: PostgreSQL procedures for validating members and processing loan lifecycle events.
- `fn_late_fee`: Function calculating overdue penalties with a 2000 cent cap.
- `overdue_loans`, `popular_books`: Views aggregating loan statistics and popularity rankings.
- `add_order_item`: MySQL procedure for atomic line item insertion, stock reservation, and total recalculation.
- `product`, `customer_order`, `order_item`: MySQL tables managing SKU stock, order status, and line details.
- `dbo.usp_SyncStock`: T-SQL procedure orchestrating supplier feed aggregation and stock merging.
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`: SQL Server tables for target stock, source feeds, and sync audit logs.

#### Data flow
- **Library Lifecycle**: `05_seed_data.sql` initializes reference data; `04_migration_v2.sql` evolves schema to `book_copy`; `fn_checkout_book` decrements stock and creates `loan` records; `trg_loan_audit` logs changes to `loan_audit`; `sp_return_book` reverses stock and updates return dates.
- **Order Processing**: `add_order_item` locks `product` rows, validates stock, upserts `order_item`, decrements `product.stock`, and recalculates `customer_order.total_cents`.
- **Warehouse Sync**: `dbo.usp_SyncStock` filters `SupplierFeed` by `@Since`, aggregates quantities, executes `MERGE` on `StockLevel`, and writes row counts to `SyncLog`.

#### Dependencies
- **Internal**: `loan` references `book` and `member`; `book_copy` references `book` and `branch`; `order_item` links `customer_order` and `product`; `SyncLog` captures outcomes of `StockLevel` merges.
- **External**: PostgreSQL functions (`CURRENT_DATE`, `generate_series`); MySQL storage engine locking; SQL Server system functions (`SYSUTCDATETIME`, `ERROR_MESSAGE`).
- **Legacy**: Migration scripts depend on legacy `copies_free` and `copies_total` columns in `book`.

#### Cross-cutting
- **Concurrency Control**: Row-level locking (`SELECT ... FOR UPDATE`) in MySQL; `MERGE` atomicity in SQL Server; transactional integrity in PostgreSQL.
- **Constraint Enforcement**: `CHECK` constraints for dates and inventory limits; foreign keys with cascading deletes; unique indexes on SKUs and IDs.
- **Audit Trails**: Trigger-based logging for library loans; explicit logging tables for warehouse sync outcomes.
- **State Management**: `book_copy` status (`AVAILABLE`, `ON_LOAN`, `LOST`) drives availability logic; `product.stock` decrements on order addition.
