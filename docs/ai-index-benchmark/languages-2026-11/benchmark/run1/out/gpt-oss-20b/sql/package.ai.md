### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:22:22Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of SQL schemas and procedures that manage catalog, inventory, and order data across a library, e‑commerce shop, and warehouse system.

#### Purpose
- Persist and manipulate catalog, loan, order, and inventory data.
- Provide transactional business logic for checkout, order item addition, and nightly stock sync.
- Expose analytical views for reporting and audit trails.

#### Responsibilities
- **Data Definition** – Tables, indexes, and constraints for library, shop, and warehouse domains.
- **Business Logic** – Stored procedures/functions for checkout/return, order item addition, and stock synchronization.
- **Reporting & Auditing** – Views and audit tables for overdue loans, member stats, popular books, and sync logs.
- **Migration & Seeding** – Scripts to evolve schema and populate initial data.

#### Key units
- **Library**  
  - `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit` tables.  
  - `fn_checkout_book`, `sp_return_book`, `fn_late_fee` functions/procedures.  
  - `trg_loan_audit` trigger.  
  - `overdue_loans`, `member_loan_stats`, `popular_books` views.
- **Shop**  
  - `product`, `customer_order`, `order_item` tables.  
  - `add_order_item(p_order_id, p_sku, p_qty)` procedure.
- **Warehouse**  
  - `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` tables.  
  - `dbo.usp_SyncStock(@Since)` procedure.

#### Data flow
1. **Library Checkout/Return** – `fn_checkout_book` → inserts `loan`, updates `book.copies_free`, triggers `trg_loan_audit`; `sp_return_book` → updates `loan.returned_on`, restores `book.copies_free`, logs `RETURN`.  
2. **Shop Order Item** – `add_order_item` locks `product`, validates stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
3. **Warehouse Sync** – `dbo.usp_SyncStock` aggregates `SupplierFeed` after `@Since`, MERGE into `StockLevel`, logs counts and errors in `SyncLog`.

#### Dependencies
- Internal: cross‑table foreign keys within each domain (e.g., `loan.member_id → member.id`, `order_item.product_id → product.id`).  
- External: PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), MySQL 8 transactional guarantees, SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_order_customer`, `idx_stocklevel_sku`, etc.

#### Cross-cutting
- Referential integrity enforced via foreign keys across all schemas.  
- Consistent audit mechanisms: `loan_audit` table with trigger, `SyncLog` table for warehouse sync.  
- Transactional safety: each procedure runs in a single transaction, with explicit rollback on error.  
- Concurrency control: `SELECT … FOR UPDATE` in shop, `MERGE` with `$action` in warehouse, trigger‑based logging in library.  
- Business rule constraints (e.g., `copies_free <= copies_total`, stock sufficiency checks).
