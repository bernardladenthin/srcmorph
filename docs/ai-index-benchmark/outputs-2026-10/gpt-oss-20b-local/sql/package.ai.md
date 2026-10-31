### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:24:26Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database‑centric packages that provide core data persistence, business logic, and reporting for a library, an e‑commerce shop, and a warehouse inventory system.

#### Purpose
- Persist and enforce domain rules for library catalog, shop orders, and warehouse stock.
- Expose read‑only reporting views and audit trails.
- Coordinate nightly data ingestion and inventory reconciliation.

#### Responsibilities
- **Library** – schema, checkout/return logic, late‑fee calculation, audit trail, and reporting views.
- **Shop** – order‑item CRUD, inventory reservation, and order total recalculation via a stored procedure.
- **Warehouse** – nightly supplier feed ingestion, stock reconciliation, and sync audit logging.

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
1. **Library** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts `loan`, triggers `loan_audit`; `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, triggers audit; `fn_late_fee` computes charges; reporting views aggregate loan, member, book, and author data.  
2. **Shop** – `add_order_item` locks the `product` row, checks stock, inserts/updates `order_item`, decrements `product.stock`, and recomputes `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows after a timestamp, `MERGE`s into `StockLevel`, records inserted/updated counts, logs the run in `SyncLog`, and ensures atomicity with a single transaction.

#### Dependencies
- Internal tables and triggers within each package.  
- PostgreSQL core functions for the library; MySQL 8 for the shop; SQL Server system functions for the warehouse.  
- Cross‑package: none; each operates on its own schema.

#### Cross-cutting
- **Integrity** – foreign keys, check constraints, and unique indexes enforce data validity across all packages.  
- **Audit** – `loan_audit` and `SyncLog` capture every state change for traceability.  
- **Concurrency** – `FOR UPDATE` locking in the shop, transaction control in the warehouse, and trigger‑based audit in the library prevent race conditions.  
- **Business rules** – fixed late‑fee policy in the library, stock reservation logic in the shop, and atomic sync in the warehouse are encapsulated in stored functions/procedures.
