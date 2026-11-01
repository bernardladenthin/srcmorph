### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:44:48Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A unified data‑management toolkit that models a multi‑branch library, an e‑commerce shop, and a warehouse inventory system, providing schema, business logic, and synchronization routines across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Deliver a cohesive set of database objects for library lending, online sales, and warehouse stock management.
- Enable consistent data integrity, reporting, and transactional operations across three distinct domains.

#### Responsibilities
- **Library** – schema, reporting views, checkout/return logic, audit trail, and copy‑based inventory migration.
- **Shop** – product catalog, order tracking, atomic order‑item addition with stock reservation.
- **Warehouse** – nightly supplier feed ingestion, stock level upsert, and sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions: `fn_checkout_book`, `fn_late_fee`.  
  - Procedure: `sp_return_book`.  
  - Trigger: `trg_loan_audit` (calls `trg_loan_audit_fn`).  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, triggers audit; `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, logs audit; `fn_late_fee` computes penalties; views aggregate loan data for reporting.  
2. **Shop** – `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows since `@Since`, merges into `StockLevel` (update or insert), counts actions, logs the run in `SyncLog`, and rolls back on error.

#### Dependencies
- **Internal** – cross‑package tables (`author`, `book`, `product`, `StockLevel`, etc.) and shared functions (`fn_late_fee`).  
- **External** – PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), MySQL 8 features (`ENUM`, `SIGNAL`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **Indexes** – `idx_book_author`, `idx_loan_member`, `idx_popular_books_id`, `idx_book_copy_book`, and shop/warehouse specific indexes.

#### Cross‑cutting
- Strong referential integrity with `ON DELETE CASCADE` and foreign keys.  
- Central audit mechanisms (`loan_audit`, `SyncLog`).  
- Transactional consistency: each procedure runs in a single transaction, rolling back on failure.  
- Consistent naming conventions (`*_id`, `*_cents`, `*_qty`) and type safety across schemas.  
- Migration strategy in the library replaces legacy copy counters with a normalized `book_copy` table, preserving branch awareness.
