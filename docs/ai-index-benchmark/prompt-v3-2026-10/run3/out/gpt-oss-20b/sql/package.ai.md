### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:47:27Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A set of SQL modules that manage library lending, e‑commerce orders, and warehouse inventory, each with its own data model, business logic, and audit trail.

#### Purpose
- Persist and enforce business rules for library catalog, shop orders, and warehouse stock.
- Provide reporting, audit logging, and error handling across the three domains.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, late‑fee calculation, audit trail, reporting views.
- **Shop** – product catalog, order processing, stock reservation, order total calculation.
- **Warehouse** – nightly supplier feed ingestion, inventory merge, sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Functions: `fn_checkout_book`, `fn_late_fee`.  
  - Procedure: `sp_return_book`.  
  - Triggers: `trg_loan_audit`, `trg_loan_audit_fn`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books` (materialized).  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock`.

#### Data flow
- **Library**: `fn_checkout_book` → validates → inserts `loan` → logs via trigger; `sp_return_book` → updates `loan`/`book_copy` → logs. `fn_late_fee` computes fees on demand. Views aggregate loan data for reporting.  
- **Shop**: `add_order_item` locks `product`, validates stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
- **Warehouse**: `dbo.usp_SyncStock` aggregates `SupplierFeed` since `@Since`, merges into `StockLevel`, counts actions, logs run in `SyncLog`, rolls back on error.

#### Dependencies
- **Internal**: Each module references its own tables and audit logs.  
- **External**: PostgreSQL built‑ins (`now()`, `INTERVAL`), MySQL 8 features (`FOR UPDATE`, ENUM, auto‑increment), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **Schema changes**: Library migration `04_migration_v2.sql` adds `branch`/`book_copy`, alters `loan`/`member`, removes copy counts from `book`.

#### Cross-cutting
- **Constraints & Indexes**: FK, uniqueness on ISBN/barcode, status checks, overdue indexes.  
- **Error handling**: Functions raise errors for blocked members, unavailable copies, duplicate returns; procedures use `TRY…CATCH` with rollback and audit logging.  
- **Audit trails**: `loan_audit` records every checkout/return; `SyncLog` records each stock sync run and errors.  
- **Transactional safety**: Shop procedure locks rows; warehouse procedure uses a single transaction for merge and logging.  
- **Configuration**: UTC timestamps (`SYSUTCDATETIME()`), numeric sizing (`UNSIGNED`, `INT`, `SMALLINT`).
