### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:30Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of SQL packages that manage inventory, lending, and e‑commerce data across PostgreSQL, MySQL, and SQL Server, each providing schema, business logic, and audit capabilities for their domain.

#### Purpose
- Persist and manipulate domain data (library catalog, shop orders, warehouse stock).
- Expose stored routines that enforce business rules and maintain consistency.
- Provide reporting and audit trails for operational insight.

#### Responsibilities
- **Schema definition** – tables, views, and materialized views for catalog, orders, and inventory.
- **Business logic** – stored functions/procedures for checkout, return, order‑item addition, and nightly stock sync.
- **Reporting & audit** – views for overdue loans, member stats, popular books; audit tables and triggers for loan actions; sync logs for warehouse updates.
- **Data integrity** – foreign keys, constraints, row‑level locking, and transactional control.

#### Key units
- **Library (PostgreSQL)**
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- **Shop (MySQL)**
  - Tables: `product`, `customer_order`, `order_item`.
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.
- **Warehouse (SQL Server)**
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
  - Procedure: `dbo.usp_SyncStock`.

#### Data flow
1. **Library checkout/return**
   - `fn_checkout_book` validates, decrements `book.copies_free`, inserts `loan`, trigger logs `CHECKOUT`.
   - `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, trigger logs `RETURN`.
2. **Shop order item addition**
   - `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
3. **Warehouse sync**
   - `usp_SyncStock` aggregates `SupplierFeed` since `@Since`, merges into `StockLevel`, logs run in `SyncLog`.

#### Dependencies
- **Internal** – cross‑package tables and views referenced by stored routines.
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL 8 features (`FOR UPDATE`, `ON DUPLICATE KEY`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).
- **Migration** – PostgreSQL migration scripts add `branch` and `book_copy`, altering loan/member relationships.

#### Cross-cutting
- **Integrity** – foreign keys, CHECK constraints, unique indexes, and materialized view refreshes.
- **Audit** – `loan_audit` with trigger, `SyncLog` for warehouse runs.
- **Concurrency** – row‑level locking in MySQL, transactional MERGE in SQL Server, trigger‑based audit in PostgreSQL.
- **Seeding** – initial data scripts populate core tables for demo/testing.
