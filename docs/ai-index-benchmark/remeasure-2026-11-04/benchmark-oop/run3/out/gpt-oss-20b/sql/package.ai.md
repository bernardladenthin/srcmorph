### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:57:11Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database‑centric modules that manage library lending, e‑commerce order lines, and warehouse stock synchronization, each exposing stored procedures, functions, and audit mechanisms for transactional integrity and reporting.  

#### Purpose
- Persist and enforce business rules for library catalog, member, and loan data.  
- Handle order line creation, stock reservation, and order total updates in a web shop.  
- Synchronize warehouse inventory with supplier feeds and log each sync run.  

#### Responsibilities
- **Library** – data definition, business logic (checkout/return, late fees), reporting views, schema evolution, and seeding.  
- **Shop** – order line CRUD, stock reservation with row locking, and order total recalculation.  
- **Warehouse** – ingest supplier feed, reconcile stock levels, and audit sync runs.  

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since)`.  

#### Data flow
1. **Library** – `fn_checkout_book` validates member/copy, inserts `loan`, updates `book.copies_free`, triggers `loan_audit`. `sp_return_book` updates `loan.returned_on`, restores `book.copies_free`, triggers audit. `fn_late_fee` calculates overdue charges. Views aggregate loan data for reporting.  
2. **Shop** – `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recomputes `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` since a timestamp, merges into `StockLevel`, records counts, and logs the run in `SyncLog`.  

#### Dependencies
- **Internal** – cross‑module tables (`author`, `book`, `member`, `loan`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`).  
- **External** – PostgreSQL built‑in functions (`now()`, `CURRENT_DATE`), MySQL 8 features (`SELECT … FOR UPDATE`, `ON DUPLICATE KEY`, `SIGNAL`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  

#### Cross-cutting
- **Audit** – `loan_audit` table and trigger log every checkout/return; `SyncLog` records each warehouse sync.  
- **Constraints & Indexes** – foreign keys, uniqueness, check constraints (e.g., `copies_free <= copies_total`), and indexes (`idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`).  
- **Concurrency** – row‑level locking on `product` in Shop; transactional integrity in Warehouse sync.  
- **Error handling** – MySQL `SIGNAL` for stock errors; SQL Server error capture in `SyncLog` and rethrow.  
- **Schema evolution** – Library migration v2 normalizes branches and book copies, replacing aggregate copy counts.
