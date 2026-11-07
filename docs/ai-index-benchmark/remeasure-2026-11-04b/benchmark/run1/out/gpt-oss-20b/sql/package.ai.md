### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:55:47Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A set of relational schemas and stored procedures that manage library lending, e‑commerce orders, and warehouse inventory, each with audit, concurrency, and reporting support.

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.
- Provide transactional routines that maintain data integrity and audit trails.
- Expose read‑only reporting views for overdue loans, popular books, and order totals.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, audit, and reporting.
- **Shop** – product catalog, order processing, stock reservation, and total calculation.
- **Warehouse** – nightly supplier feed ingestion, inventory aggregation, and sync logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.  
  - Trigger: `trg_loan_audit`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.
- **Warehouse**  
  - Tables: `StockLevel`, `SupplierFeed`, `SyncLog`.  
  - Procedure: `usp_SyncStock(@Since)`.

#### Data flow
1. **Library**  
   - `fn_checkout_book` validates availability → inserts `loan` → trigger logs `CHECKOUT`.  
   - `sp_return_book` updates `loan.returned_on` → trigger logs `RETURN`.  
   - `fn_late_fee` calculates overdue charges for application use.  
   - Views aggregate loan data for reporting.
2. **Shop**  
   - `add_order_item` locks product row, validates stock, inserts/updates `order_item`, decrements `product.stock`, and recomputes `customer_order.total_cents`.
3. **Warehouse**  
   - `usp_SyncStock` aggregates `SupplierFeed` rows, merges into `StockLevel`, and records metrics in `SyncLog`.

#### Dependencies
- **Internal** – cross‑package tables referenced by triggers, procedures, and views (`author`, `book`, `member`, `loan`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`).  
- **External** – PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), PL/pgSQL, MySQL 8 InnoDB, SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross-cutting
- **Audit & immutability** – `loan_audit` and `SyncLog` provide change history.  
- **Concurrency control** – `SELECT … FOR UPDATE` in shop, transaction blocks in warehouse, and trigger‑based logging in library.  
- **Constraints & indexes** – enforce data integrity and performance across all schemas.  
- **Reporting layers** – read‑only views expose aggregated insights without affecting transactional logic.
