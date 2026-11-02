### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:45Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas and stored routines that manage library cataloging, e‑commerce orders, and warehouse inventory synchronization.

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.
- Provide reporting views, audit trails, and transactional procedures for common operations.

#### Responsibilities
- **Library** – catalog, member, loan data; checkout/return logic; overdue and popularity reporting.
- **Shop** – product catalog, customer orders, line items; single procedure to add items with stock reservation.
- **Warehouse** – nightly supplier feed ingestion, stock reconciliation, sync audit logging.

#### Key units
- **Tables** – `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`, `product`, `customer_order`, `order_item`, `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **Views** – `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Functions / Procedures** – `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`, `add_order_item`, `dbo.usp_SyncStock`.

#### Data flow
1. **Library** – `fn_checkout_book` → loan insert, copy decrement, audit log; `sp_return_book` → loan update, copy increment, audit log; `fn_late_fee` calculates fees; views aggregate loan/member/book data.
2. **Shop** – `add_order_item` receives order ID, SKU, quantity; locks product row, validates stock, inserts/updates line item, decrements stock, recalculates order total.
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows after a timestamp, merges into `StockLevel`, logs counts and errors in `SyncLog`.

#### Dependencies
- **Internal** – cross‑references among tables (`loan` ↔ `book_copy`, `order_item` ↔ `product`, `usp_SyncStock` ↔ `StockLevel`/`SupplierFeed`).
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `INTERVAL`), MySQL 8 features (`SELECT … FOR UPDATE`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross-cutting
- **Integrity** – foreign keys, check constraints (`book_free_le_total`, `author.birth_year`, `book_copy.status` enum).
- **Audit** – `loan_audit` table with trigger, `SyncLog` table for warehouse syncs.
- **Concurrency** – `SELECT … FOR UPDATE` in shop, transactional MERGE in warehouse, trigger‑based logging in library.
- **Performance** – indexes on key columns (`idx_book_author`, `idx_loan_member`, `idx_popular_books_id`, primary keys on all tables).
