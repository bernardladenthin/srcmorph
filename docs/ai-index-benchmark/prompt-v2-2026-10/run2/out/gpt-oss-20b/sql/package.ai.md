### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:56:53Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi‑tenant data layer that manages library catalogs, shop orders, and warehouse inventory, each with its own SQL schema, stored procedures, and reporting mechanisms.  

#### Purpose
- Persist and enforce business rules for library lending, e‑commerce orders, and warehouse stock.  
- Provide audit trails, reporting views, and transactional integrity across all domains.

#### Responsibilities
- **Library** – catalog, member, loan processing, overdue reporting, and audit logging.  
- **Shop** – product catalog, order lifecycle, line‑item handling, stock reservation, and total calculation.  
- **Warehouse** – nightly supplier feed ingestion, inventory merge, and sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `branch`, `book_copy`, `member`, `loan`, `loan_audit`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions: `fn_checkout_book`, `fn_late_fee`.  
  - Procedure: `sp_return_book`.  
  - Trigger: `trg_loan_audit` → `trg_loan_audit_fn`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member and copy, creates `loan`, updates `book_copy.status`, logs `CHECKOUT`; `sp_return_book` updates `loan.returned_on`, restores `book_copy.status`, logs `RETURN`; `fn_late_fee` computes penalties; reporting views aggregate loan data.  
2. **Shop** – `add_order_item` locks the product row, verifies stock, inserts/updates `order_item`, decrements `product.stock`, and recalculates `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates new `SupplierFeed` rows, `MERGE` into `StockLevel`, counts inserts/updates, and writes a `SyncLog` entry; all within a single transaction.

#### Dependencies
- **Internal** – cross‑table FK constraints within each domain (e.g., `loan.book_copy_id`, `order_item.product_id`).  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL 8 features (`FOR UPDATE`, `ENUM`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **Migration** – `04_migration_v2.sql` updates library schema for branches and copies.

#### Cross‑cutting
- **Integrity & Constraints** – unique ISBN, SKU, foreign keys, check constraints on copy counts and stock.  
- **Audit & Logging** – `loan_audit` and `SyncLog` record every state change.  
- **Error Handling** – functions/procedures raise or signal errors for blocked members, unavailable copies, insufficient stock, or sync failures.  
- **Concurrency** – `FOR UPDATE` locks in shop, single‑transaction sync in warehouse, trigger‑based audit in library.  
- **Performance** – indexes on foreign keys, overdue loan queries, and materialized view for popular books.
