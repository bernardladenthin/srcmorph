### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:27:40Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of three relational database schemas that model a library, an online shop, and a warehouse, each exposing stored procedures, functions, and audit mechanisms for common business operations such as lending, ordering, and inventory syncing.

#### Purpose
- Persist and manage domain data for library lending, e‑commerce orders, and warehouse stock.
- Provide transactional business logic (checkout, return, order item addition, nightly stock sync).
- Offer read‑only reporting and audit trails for compliance and analytics.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, audit, and reporting.
- **Shop** – product catalog, order processing, stock reservation, and total calculation.
- **Warehouse** – nightly supplier feed ingestion, stock level maintenance, and sync logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions/Procedures: `fn_checkout_book`, `fn_late_fee`, `sp_return_book`, `trg_loan_audit_fn`.  
  - Trigger: `trg_loan_audit` on `loan`.

- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.

- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since)`.

#### Data flow
1. **Library**  
   - `fn_checkout_book` validates, creates a `loan`, updates `book.copies_free`, logs `CHECKOUT`.  
   - `sp_return_book` marks `loan.returned_on`, restores `book.copies_free`, logs `RETURN`.  
   - `fn_late_fee` computes overdue charges.  
   - Views aggregate loan/member/book data for reports.

2. **Shop**  
   - `add_order_item` locks the target `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.

3. **Warehouse**  
   - `usp_SyncStock` aggregates new `SupplierFeed` rows, merges into `StockLevel` (update or insert), captures row counts, and records a single entry in `SyncLog` with success or error details.

#### Dependencies
- **Internal** – foreign keys among core tables (`loan` ↔ `book`/`member`, `order_item` ↔ `product`/`customer_order`, `StockLevel` ↔ `SupplierFeed`).  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL 8 features (`ENUM`, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`), and standard SQL types.

#### Cross-cutting
- Consistent use of primary keys (`BIGSERIAL`/`IDENTITY`) and `CHECK` constraints for data integrity.  
- Centralized audit via `loan_audit` and `SyncLog`.  
- Transactional integrity: all business logic runs within a single transaction, with rollback on error.  
- Concurrency control: row‑level locking (`FOR UPDATE`) in the shop, `MERGE` with `OUTPUT` in the warehouse.  
- Indexing strategy: dedicated indexes on foreign keys and frequently queried columns (`idx_book_author`, `idx_loan_member`, `idx_popular_books_id`, etc.) to support reporting and routine operations.
