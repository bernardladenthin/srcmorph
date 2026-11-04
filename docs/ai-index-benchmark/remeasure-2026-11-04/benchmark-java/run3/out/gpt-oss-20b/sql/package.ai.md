### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:50:10Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of relational schemas and stored procedures that manage library cataloging, e‑commerce orders, and warehouse inventory, each with business‑rule enforcement, audit logging, and reporting capabilities.

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.
- Provide audit trails and reporting views for each domain.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, late fee calculation, audit, and reporting.
- **Shop** – product catalog, order processing, stock reservation, and total calculation.
- **Warehouse** – nightly supplier feed ingestion, inventory merge, and sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
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
- **Library**: checkout → `fn_checkout_book` → loan record + audit; return → `sp_return_book` → loan update + audit; late fee → `fn_late_fee`; reporting via views.
- **Shop**: `add_order_item` receives order ID, SKU, qty → locks product, validates stock, updates/creates order item, decrements stock, recalculates order total.
- **Warehouse**: `usp_SyncStock(@Since)` aggregates new feed rows → MERGE into `StockLevel` (insert/update) → counts logged in `SyncLog`.

#### Dependencies
- **Internal**: cross‑table foreign keys (e.g., `order_item` → `customer_order`, `book_copy` → `book`), indexes (`idx_book_author`, `idx_order_customer`), and audit tables.
- **External**: PostgreSQL built‑ins for library, MySQL row locking for shop, SQL Server system functions (`SYSUTCDATETIME`, `ERROR_MESSAGE`) for warehouse.

#### Cross-cutting
- Referential integrity enforced via foreign keys and cascade deletes.
- Concurrency control: row locking (`FOR UPDATE`) in shop, single‑transaction merge in warehouse.
- Audit logging: `loan_audit` and `SyncLog` capture every state change.
- Indexing and constraints maintain performance and data validity across all schemas.
