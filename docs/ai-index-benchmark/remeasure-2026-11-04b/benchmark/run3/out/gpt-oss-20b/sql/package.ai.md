### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:12:18Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas and stored procedures that manage library lending, e‑commerce orders, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Persist and enforce business rules for library loans, online shop orders, and warehouse stock.
- Provide audit trails, reporting, and transactional integrity across three distinct domains.

#### Responsibilities
- **Library** – catalog, member, loan, audit, reporting, and migration.
- **Shop** – product catalog, order lifecycle, line‑item handling, and stock reservation.
- **Warehouse** – nightly supplier feed ingestion, inventory upsert, and run logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions: `fn_checkout_book`, `fn_late_fee`.  
  - Procedure: `sp_return_book`.  
  - Trigger: `trg_loan_audit` (logs to `loan_audit`).
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member, updates `book.copies_free`, inserts `loan`, logs audit; `sp_return_book` updates `loan.returned_on`, restores copies, logs audit; `fn_late_fee` calculates penalties; views aggregate data for reporting.
2. **Shop** – `add_order_item` locks `product`, verifies stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
3. **Warehouse** – `usp_SyncStock` aggregates new `SupplierFeed` rows, merges into `StockLevel`, records counts, logs run metadata and errors.

#### Dependencies
- **Internal** – cross‑package tables referenced by procedures and views (`author`, `book`, `member`, `loan`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`).
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL ENUM and `FOR UPDATE`, SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross‑cutting
- **Audit & Logging** – library audit table, warehouse sync log, and trigger‑based audit capture.
- **Concurrency Control** – `SELECT … FOR UPDATE` in shop, transaction scopes in warehouse and library procedures.
- **Error Handling** – shop procedure raises errors for missing SKU or insufficient stock; warehouse procedure captures `ERROR_MESSAGE()` into `SyncLog`.
- **Schema Evolution** – library migration adds `branch` and `book_copy`, removes copy counter from `book`.
- **Indexes & Constraints** – foreign keys, unique constraints, check constraints, and indexes on foreign keys and materialized views to support performance and data integrity.
