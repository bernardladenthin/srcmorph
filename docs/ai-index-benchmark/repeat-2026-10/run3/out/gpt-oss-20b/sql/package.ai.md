### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:59:13Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database‑centric modules that manage library cataloging, e‑commerce order processing, and warehouse inventory synchronization, all implemented with stored procedures, triggers, and SQL scripts.

#### Purpose
- Persist and manipulate domain data for libraries, shops, and warehouses.
- Enforce business rules and maintain audit trails within the database.
- Provide reporting and synchronization capabilities.

#### Responsibilities
- **Library** – catalog definition, loan processing, late‑fee calculation, audit logging, and reporting views.
- **Shop** – order line item management, stock reservation, and order total recalculation.
- **Warehouse** – nightly supplier feed ingestion, inventory merging, and sync audit logging.

#### Key units
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.  
  - Triggers: `trg_loan_audit`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
  - Tables: `product`, `customer_order`, `order_item`.
- **Warehouse**  
  - Procedure: `usp_SyncStock(@Since)`.  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member and copy availability, inserts a `loan`, updates `book_copy.copies_free`, and triggers `loan_audit`. `sp_return_book` updates `returned_on`, restores `copies_free`, and logs the return. Views aggregate loan data for reporting.
2. **Shop** – `add_order_item` locks the product row, validates stock, inserts/updates an `order_item`, decrements `product.stock`, and recomputes `customer_order.total_cents`.
3. **Warehouse** – `usp_SyncStock` aggregates recent `SupplierFeed` rows, merges them into `StockLevel` with `MERGE`, counts changes, and records the outcome in `SyncLog`.

#### Dependencies
- **Internal** – cross‑module tables (`author`, `book`, `product`, etc.) and shared types (`BIGSERIAL`, `TIMESTAMPTZ`).  
- **External** – PostgreSQL and SQL Server system functions, transaction control, and materialized view refresh mechanisms.

#### Cross‑cutting
- Referential integrity via foreign keys across all tables.  
- Indexes (`idx_book_author`, `idx_loan_member`, `idx_order_customer`) for query performance.  
- Transactional safety: each procedure runs in a single transaction with proper error handling (`TRY…CATCH`, `ROLLBACK`).  
- Consistent auditability: timestamps in UTC, audit tables, and triggers capture all state changes.
