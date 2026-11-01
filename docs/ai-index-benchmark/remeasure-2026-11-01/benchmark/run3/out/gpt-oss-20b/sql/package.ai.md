### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:09:07Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of SQL libraries that manage catalog, inventory, and order data across PostgreSQL, MySQL, and SQL Server, providing core business logic, reporting, and synchronization.

#### Purpose
- Persist and manipulate core entities (books, members, loans, products, orders, warehouse stock).
- Enforce business rules through stored procedures, triggers, and constraints.
- Deliver reporting views and audit trails for operational insight.

#### Responsibilities
- **Data definition** – tables, indexes, constraints for catalog, inventory, and order systems.
- **Business logic** – stored procedures/functions for checkout, return, order item addition, and stock synchronization.
- **Reporting & auditing** – views, materialized views, and audit tables for overdue loans, member stats, popular books, and sync logs.
- **Data migration & seeding** – scripts to evolve schemas and populate test data.

#### Key units
- **Library**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; functions `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**: `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- **Warehouse**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Data flow
1. **Library**: `fn_checkout_book` → checks member, updates `book_copy`, inserts `loan`, logs `loan_audit`; `sp_return_book` → updates `loan`, restores `book_copy`, logs `loan_audit`; reporting views aggregate across tables.
2. **Shop**: `add_order_item` locks `product`, validates stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
3. **Warehouse**: `dbo.usp_SyncStock` aggregates `SupplierFeed` since `@Since`, merges into `StockLevel`, counts changes, logs in `SyncLog`, commits or rolls back on error.

#### Dependencies
- Internal: cross‑table foreign keys within each package; shared audit tables (`loan_audit`, `SyncLog`).
- External: PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL 8 features (`FOR UPDATE`, `ON DUPLICATE KEY UPDATE`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross-cutting
- Referential integrity via foreign keys and unique constraints.
- Consistent error handling in procedures (raise errors for blocked members, insufficient stock, or sync failures).
- Concurrency control: row‑level locks (`FOR UPDATE`) in shop, transaction‑level atomicity in warehouse sync.
- Indexing on foreign keys and frequently queried columns to support reporting and lookup performance.
