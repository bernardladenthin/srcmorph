### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:04:22Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A cross‑platform data‑management suite that handles library cataloging, e‑commerce order processing, and warehouse inventory synchronization, all built around SQL‑centric business logic and audit trails.  

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.  
- Provide reporting, audit, and synchronization capabilities across the three domains.  

#### Responsibilities
- **Library** – catalog, member, loan persistence; checkout/return logic; overdue and popularity reporting; audit trail.  
- **Shop** – order item CRUD, stock reservation, order total recalculation, and status management.  
- **Warehouse** – nightly supplier feed ingestion, stock level updates, and sync run logging.  

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions/Procedures: `fn_checkout_book`, `fn_late_fee`, `sp_return_book`.  
  - Trigger: `trg_loan_audit` on `loan`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
  - Procedure: `dbo.usp_SyncStock(@Since DATETIME2)`.  

#### Data flow
1. **Library**  
   - `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, logs `CHECKOUT`.  
   - `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, trigger logs `RETURN`.  
   - Views aggregate loan data for reporting.  
2. **Shop**  
   - `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recomputes `customer_order.total_cents`.  
3. **Warehouse**  
   - `usp_SyncStock` aggregates `SupplierFeed` since `@Since`, merges into `StockLevel` (increment or insert), logs counts and errors in `SyncLog`.  

#### Dependencies
- **Internal** – shared tables (`author`, `book`, `product`, `customer_order`, `order_item`, `StockLevel`, `SyncLog`).  
- **External** – PostgreSQL (PL/pgSQL), MySQL 8, SQL Server 2022, system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  

#### Cross‑cutting
- **Audit** – `loan_audit` and `SyncLog` capture every state change.  
- **Constraints & Checks** – referential integrity, uniqueness, business rule checks (`copies_free <= copies_total`, `due_on > loaned_on`).  
- **Error Handling** – PL/pgSQL exceptions, MySQL `SIGNAL`, T‑SQL `TRY…CATCH`.  
- **Concurrency** – row‑level locking (`SELECT … FOR UPDATE`, PostgreSQL row locks) ensures consistent stock/loan counts.  
- **Configuration** – migration scripts set default branches, barcode logic, and initial seed data.
