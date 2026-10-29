### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:58:22Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides a unified data layer for multi-branch library lending, atomic shop order processing, and batch warehouse inventory synchronization with strict transactional integrity and audit capabilities.

#### Purpose
- Implements relational schemas and business logic for library inventory management, e-commerce order fulfillment, and supply chain stock synchronization.
- Ensures data consistency through atomic transactions, concurrency controls, and automated audit logging across distinct operational domains.

#### Responsibilities
- **Library Lifecycle Management**: Models authors, books, members, and loans while supporting multi-branch physical copy tracking and late fee calculations.
- **Order Processing**: Validates stock availability, merges duplicate order lines, and atomically updates inventory and order totals.
- **Batch Synchronization**: Ingests supplier feeds, aggregates quantities, and merges changes into warehouse stock levels with error reporting.
- **Schema Evolution**: Executes migration scripts to transition legacy single-branch models to granular, branch-specific inventory structures.

#### Key units
- `author`, `book`, `member`, `loan`: Core library entities storing metadata and transactional history.
- `branch`, `book_copy`: New entities introduced in v2 to track physical inventory location and status.
- `loan_audit`: Log table recording chronological checkout and return actions via triggers.
- `fn_checkout_book`, `sp_return_book`: Functions/procedures managing loan state transitions and availability updates.
- `fn_late_fee`: Logic for calculating overdue penalties with maximum thresholds.
- `overdue_loans`, `member_loan_stats`, `popular_books`: Views and materialized views for reporting and analytics.
- `add_order_item`: MySQL procedure for atomic order line insertion and stock reservation.
- `product`, `customer_order`, `order_item`: Tables managing SKU pricing, order totals, and line items.
- `dbo.usp_SyncStock`: Stored procedure orchestrating nightly supplier feed ingestion and stock updates.
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`: Tables for current inventory, raw feeds, and execution auditing.

#### Data flow
- **Library**: Seed data populates base tables; checkout/return procedures validate status, update counts, and trigger audit logs; views aggregate data for reporting.
- **Shop**: Input parameters trigger row-level locking on products; valid requests merge order items and decrement stock while recalculating order totals.
- **Warehouse**: Filtered supplier feed records are aggregated by warehouse and SKU; a `MERGE` operation updates or inserts stock levels; results and errors are logged to `SyncLog`.

#### Dependencies
- **Internal**: `loan` references `book` and `member`; `order_item` depends on `product` and `customer_order`; `SyncLog` depends on `StockLevel` and `SupplierFeed`.
- **External**: `generate_series` for library migrations; `SYSUTCDATETIME()` and `ERROR_MESSAGE()` for warehouse logging.
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`.

#### Cross-cutting
- **Constraint Enforcement**: Uses `CHECK` constraints for inventory limits, date validity, and status values across all domains.
- **Concurrency Control**: Employs `FOR UPDATE` locking in shop procedures and `MERGE` semantics in warehouse batches to prevent race conditions.
- **Auditability**: Automatic trigger-based logging for library loans and explicit transactional logging for warehouse syncs.
- **Transaction Integrity**: Relies on database transaction blocks to ensure atomicity of inventory updates and log entries.
