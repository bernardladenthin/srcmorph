### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:25Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database‑level modules that manage library cataloging, e‑commerce order lines, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Persist and enforce business rules for library lending, online orders, and warehouse stock.
- Provide transactional integrity, audit trails, and reporting across three distinct domains.

#### Responsibilities
- **Library** – catalog definition, loan processing, late‑fee calculation, reporting, and branch‑level copy migration.
- **Shop** – atomic order‑line updates, inventory reservation, and order total recalculation.
- **Warehouse** – nightly supplier feed ingestion, stock level merging, and sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Migration script: `04_migration_v2.sql`; seed script: `05_seed_data.sql`.
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since DATETIME2)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member, decrements copies, inserts `loan`, logs audit; `sp_return_book` updates loan, increments copies, logs audit; `fn_late_fee` computes fees; views aggregate loan data for reporting.
2. **Shop** – `add_order_item` locks product row, validates stock, inserts/updates `order_item`, decrements stock, recalculates `customer_order.total_cents`.
3. **Warehouse** – `usp_SyncStock` aggregates recent `SupplierFeed` rows, merges into `StockLevel` (update/insert), records counts or errors in `SyncLog`.

#### Dependencies
- **Internal** – cross‑module tables (`author`, `book`, `member`, `loan`, `branch`, `book_copy`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`).  
- **External** – PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), MySQL row‑locking (`SELECT … FOR UPDATE`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross‑cutting
- **Transactional integrity** – each procedure runs in a single transaction; rollback on error.  
- **Audit trails** – library `loan_audit`, warehouse `SyncLog`.  
- **Error handling** – functions raise explicit errors for blocked members or insufficient stock; warehouse logs errors before rethrowing.  
- **Concurrency control** – shop uses row‑level locking; library functions rely on foreign‑key constraints and triggers.  
- **Branch dimension** – library migration introduces `branch` and `book_copy` to replace book‑level copy counts.  
- **Reporting** – materialized view `popular_books` requires periodic refresh.
