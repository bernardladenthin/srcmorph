### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T23:42:16Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Orchestrates inventory management, member circulation, and order processing across library, retail, and warehouse domains using relational database operations.

#### Purpose
*   Consolidate inventory and transaction management for library, shop, and warehouse subsystems.
*   Implement persistent storage and business logic for lending, ordering, and stock synchronization.

#### Responsibilities
*   Define relational schemas for authors, books, members, products, orders, and stock levels.
*   Execute business logic for book checkout, returns, order placement, and stock synchronization.
*   Provide reporting views for overdue items, popular books, and member statistics.
*   Maintain audit logs for loan actions and synchronization metrics.
*   Support multi-branch inventory tracking and supplier feed ingestion.

#### Key units
*   `library`: `author`, `book`, `member`, `loan`, `book_copy`, `branch`, `overdue_loans`, `popular_books`.
*   `library`: `fn_checkout_book`, `sp_return_book`, `trg_loan_audit_fn`.
*   `shop`: `product`, `customer_order`, `order_item`, `add_order_item`, `idx_order_customer`.
*   `warehouse`: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `dbo.usp_SyncStock`.

#### Data flow
*   `library`: `01_schema.sql` creates tables, `03_routines.sql` modifies data, `02_views.sql` aggregates.
*   `shop`: `add_order_item` locks `product`, validates, inserts `order_item`, updates totals.
*   `warehouse`: `usp_SyncStock` filters `SupplierFeed`, `MERGE`s `StockLevel`, logs to `SyncLog`.

#### Dependencies
*   `library`: PostgreSQL DDL/PL/pgSQL, internal tables (`loan` depends on `book`, `member`).
*   `shop`: MySQL 8 dialect, internal tables (`product`, `customer_order`, `order_item`).
*   `warehouse`: SQL Server features (`MERGE`, `DATETIME2`), internal tables (`StockLevel`, `SupplierFeed`, `SyncLog`).

#### Cross-cutting
*   Transactional integrity enforced via row-level locking (`FOR UPDATE`) and rollback on error.
*   Foreign key constraints enforce referential integrity between related tables.
*   Audit logging tracks state changes (`loan_audit`, `SyncLog`).
*   Status constraints applied to inventory items (`book_copy`, `product.stock`).
*   Timezone handling via `SYSUTCDATETIME()` and `DATETIME2(0)`.
