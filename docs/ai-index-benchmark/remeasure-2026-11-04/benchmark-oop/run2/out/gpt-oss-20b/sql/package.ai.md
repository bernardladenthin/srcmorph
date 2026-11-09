### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:42:32Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of SQL packages that manage library lending, e‑commerce orders, and warehouse inventory, each with dedicated schemas, business logic, and audit trails.

#### Purpose
- Persist and enforce business rules for library catalog, shop orders, and warehouse stock.
- Provide reporting, audit logging, and transactional integrity across each domain.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, branch‑aware lending, reporting, and audit.
- **Shop** – product catalog, order processing, stock reservation, and order total calculation.
- **Warehouse** – supplier feed ingestion, stock level maintenance, and sync run auditing.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions: `fn_checkout_book`, `fn_late_fee`.  
  - Procedure: `sp_return_book`.  
  - Trigger: `trg_loan_audit` on `loan`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock`.

#### Data flow
- **Library** – checkout → `fn_checkout_book` → `loan` insert + audit; return → `sp_return_book` → `loan` update + audit; reporting views aggregate loan/member/book data.  
- **Shop** – `add_order_item` locks product, validates stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
- **Warehouse** – `usp_SyncStock` aggregates new `SupplierFeed` rows, merges into `StockLevel`, logs counts/errors in `SyncLog`.

#### Dependencies
- **Internal** – cross‑package tables referenced by procedures and triggers.  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL 8 features (`FOR UPDATE`, ENUM), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **Indexes** – e.g., `idx_book_author`, `idx_loan_member`, `idx_order_customer`, `idx_book_copy_book`.

#### Cross-cutting
- **Integrity & Constraints** – data validity enforced via checks (e.g., `copies_free <= copies_total`, `due_on > loaned_on`).  
- **Audit Trails** – `loan_audit` and `SyncLog` capture every state change.  
- **Concurrency Control** – row locks in `add_order_item`; transactional boundaries in `usp_SyncStock`.  
- **Materialized View Refresh** – `popular_books` requires periodic refresh.  
- **Branch‑aware Lending** – post‑migration loans reference `book_copy` tied to `branch`.
