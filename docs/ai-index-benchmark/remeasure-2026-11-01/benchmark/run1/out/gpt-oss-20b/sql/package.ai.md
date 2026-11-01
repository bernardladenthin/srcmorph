### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:29:54Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A set of relational schemas and stored procedures that manage catalog, inventory, and order data across a library, an online shop, and a warehouse, each with its own business rules and audit mechanisms.  

#### Purpose
- Persist and enforce domain rules for library lending, e‑commerce orders, and warehouse stock.  
- Provide audit trails, reporting, and data‑integration utilities.  

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, overdue reporting, and branch‑level copy tracking.  
- **Shop** – product catalog, order placement, stock reservation, and total calculation.  
- **Warehouse** – nightly supplier feed ingestion, inventory merging, and sync audit logging.  

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.  
  - Trigger: `trg_loan_audit`.  
  - Views: `overdue_loans`, `member_loan_stats`; materialized view: `popular_books`.  

- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  

- **Warehouse**  
  - Tables: `StockLevel`, `SupplierFeed`, `SyncLog`.  
  - Procedure: `usp_SyncStock(@Since)`.  

#### Data flow
1. **Library** – `fn_checkout_book` validates member, updates book/copy status, inserts `loan`, logs audit; `sp_return_book` updates loan, restores copy, logs audit; `fn_late_fee` calculates penalties; views aggregate loan data for reporting.  
2. **Shop** – `add_order_item` locks product row, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates new `SupplierFeed` rows, merges into `StockLevel` (increment or insert), counts changes, writes a `SyncLog` entry, all within a single transaction.  

#### Dependencies
- **Internal** – cross‑package tables referenced by procedures (`loan_audit`, `order_item`, `SyncLog`).  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`), MySQL row‑level locking (`FOR UPDATE`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- **Indexes** – e.g., `idx_book_author`, `idx_loan_member`, `idx_popular_books_id`, `fk_item_order`, `fk_item_product`.  

#### Cross-cutting
- Referential integrity via foreign keys and constraints.  
- Centralized audit via triggers (`trg_loan_audit`) and log tables (`SyncLog`).  
- Consistent error handling: `SIGNAL` in MySQL, `RAISE`/`TRY…CATCH` in PostgreSQL, `ROLLBACK` with error logging in SQL Server.  
- Transactional atomicity: library functions use implicit transactions; shop procedure uses row locks; warehouse procedure wraps merge and log in a single transaction.  
- Business rule enforcement in functions/procedures (e.g., late‑fee caps, stock availability, copy availability).
