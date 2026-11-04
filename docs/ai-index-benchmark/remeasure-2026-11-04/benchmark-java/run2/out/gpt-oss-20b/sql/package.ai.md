### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:35:02Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi‑tenant data platform that manages library catalogs, e‑commerce orders, and warehouse inventory, each with its own SQL schema, business logic, and reporting layers.

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.
- Provide read‑only reporting views and audit trails for operational insight.
- Synchronize external supplier feeds into warehouse inventory.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, late‑fee calculation, audit, reporting.
- **Shop** – product catalog, order lifecycle, line‑item management, stock reservation, total calculation.
- **Warehouse** – nightly feed ingestion, inventory merge, sync audit logging.

#### Key units
- **Library**
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
  - Functions: `fn_checkout_book`, `fn_late_fee`.
  - Procedure: `sp_return_book`.
  - Trigger: `trg_loan_audit` → `trg_loan_audit_fn()`.
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**
  - Tables: `product`, `customer_order`, `order_item`.
  - Procedure: `add_order_item`.
- **Warehouse**
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
  - Procedure: `dbo.usp_SyncStock`.

#### Data flow
1. **Library**
   - `fn_checkout_book` validates member, updates `book.copies_free`, inserts `loan`, trigger logs to `loan_audit`.
   - `sp_return_book` updates `loan.returned_on`, restores `book.copies_free`, trigger logs audit.
   - `fn_late_fee` calculates overdue fee for reporting or application use.
2. **Shop**
   - `add_order_item` locks product row, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
3. **Warehouse**
   - `usp_SyncStock` aggregates `SupplierFeed` rows since a timestamp, merges into `StockLevel` via `MERGE`, records counts and errors in `SyncLog` within a single transaction.

#### Dependencies
- **Internal** – cross‑package tables (`author`, `book`, `product`, etc.) and audit tables.
- **External** – PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), MySQL row locking (`FOR UPDATE`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).
- **Indexes** – e.g., `idx_book_author`, `idx_loan_member`, `idx_product_sku`, `idx_stocklevel_key`.

#### Cross‑cutting
- **Audit** – library uses a trigger‑based audit table; warehouse logs sync runs.
- **Constraints & checks** – enforce data integrity (e.g., `copies_free <= copies_total`, stock ≥ quantity).
- **Transactions** – shop and warehouse procedures wrap critical updates in atomic transactions.
- **Error handling** – shop raises errors for missing products or insufficient stock; warehouse captures `ERROR_MESSAGE()` in `SyncLog`.
- **Seeding & migration** – library includes migration scripts to introduce branch‑aware copies and seed data.
