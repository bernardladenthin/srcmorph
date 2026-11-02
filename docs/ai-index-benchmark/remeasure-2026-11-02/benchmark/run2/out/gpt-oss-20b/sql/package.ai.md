### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:51Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database‑centric modules that manage library lending, e‑commerce order lines, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.
- Provide transactional integrity, audit trails, and reporting across three distinct data stores.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, late fee calculation, and reporting views.
- **Shop** – atomic order line updates, inventory reservation, and order total recalculation.
- **Warehouse** – nightly supplier feed ingestion, stock level updates, and sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Views: `overdue_loans`, `member_loan_stats`, materialized `popular_books`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, trigger `trg_loan_audit`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`; `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, trigger logs to `loan_audit`; `fn_late_fee` computes overdue charges.  
2. **Shop** – `add_order_item` locks `product` row, checks stock, inserts/updates `order_item`, updates `product.stock`, recalculates `customer_order.total_cents`.  
3. **Warehouse** – `dbo.usp_SyncStock` aggregates new `SupplierFeed` rows, merges into `StockLevel`, records insert/update counts, logs run in `SyncLog`, all within a single transaction.

#### Dependencies
- **Internal** – each module relies on its own tables and views; cross‑module dependencies are none.  
- **External** – PostgreSQL (PL/pgSQL, materialized views), MySQL 8 (row‑level locking, `ON DUPLICATE KEY UPDATE`, `SIGNAL`), SQL Server (T‑SQL, `MERGE`, system functions).

#### Cross-cutting
- Transactional integrity: all critical updates wrapped in transactions.  
- Audit trails: `loan_audit` and `SyncLog` capture every change.  
- Concurrency control: MySQL row‑level locks on `product`; PostgreSQL constraints enforce data consistency.  
- Error handling: MySQL `SIGNAL` for stock errors; SQL Server rollback and error logging in `SyncLog`.  
- Indexing: strategic indexes on foreign keys and frequently queried columns (e.g., `idx_loan_member`, `idx_book_author`).
