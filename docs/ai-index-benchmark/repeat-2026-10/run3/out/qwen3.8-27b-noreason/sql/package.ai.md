### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:51:16Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides SQL-based data models and transactional logic for library lending, e-commerce order processing, and warehouse stock synchronization.

#### Purpose
- Implements domain-specific database schemas, stored procedures, and views for three distinct operational contexts: library management, web shop ordering, and warehouse inventory.
- Enforces data integrity, concurrency control, and audit logging across all three domains using database-native features.

#### Responsibilities
- **Library Management**: Handles book inventory (v1 counters vs. v2 row-per-copy), member accounts, loan lifecycle (checkout/return), late-fee calculation, and audit trails.
- **E-commerce Ordering**: Manages product catalog, customer orders, and line items with atomic stock validation and total recalculation.
- **Warehouse Synchronization**: Ingests supplier feed data, merges quantities into on-hand stock levels, and logs sync outcomes.

#### Key units
- **Library (PostgreSQL)**:
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
  - Functions/Procedures: `fn_checkout_book`, `fn_late_fee`, `sp_return_book`.
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
  - Migration: `04_migration_v2.sql` (shifts from `book.copies_*` counters to `book_copy` rows).
- **Shop (MySQL)**:
  - Tables: `product`, `customer_order`, `order_item`.
  - Procedure: `add_order_item` (atomic stock check and order update).
- **Warehouse (SQL Server)**:
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
  - Procedure: `dbo.usp_SyncStock` (MERGE-based stock update with logging).

#### Data flow
- **Library**: `fn_checkout_book` validates member/book, inserts `loan`, triggers `trg_loan_audit`; `sp_return_book` updates loan status and inventory, triggering audit log.
- **Shop**: `add_order_item` locks `product` row (`FOR UPDATE`), validates stock, updates `order_item` and `product.stock`, recalculates `customer_order.total_cents`.
- **Warehouse**: `dbo.usp_SyncStock` aggregates `dbo.SupplierFeed` since cutoff, MERGEs into `dbo.StockLevel` (update/insert), and logs results to `dbo.SyncLog`.

#### Dependencies
- **Library**: PostgreSQL dialect (`PL/pgSQL`, `generate_series`, materialized views); internal dependencies on core tables and v2 migration scripts.
- **Shop**: MySQL 8 InnoDB engine for row-level locking and transactional support.
- **Warehouse**: SQL Server T-SQL features (`MERGE`, `SYSUTCDATETIME`, `THROW`); logical relationships via `WarehouseId` + `Sku` without enforced foreign keys.

#### Cross-cutting
- **Concurrency Control**: Shop uses `FOR UPDATE` locking; Warehouse uses transactional MERGE; Library relies on constraint checks.
- **Audit & Logging**: Library uses immutable `loan_audit` table with triggers; Warehouse logs every sync execution to `dbo.SyncLog`.
- **Error Handling**: Shop raises SQLSTATE '45000' for stock issues; Warehouse uses `THROW` and rollback on failure; Library uses constraint violations.
- **Data Integrity**: Foreign keys with cascade deletes (Shop), CHECK constraints (Library), and transactional safety (Warehouse).
