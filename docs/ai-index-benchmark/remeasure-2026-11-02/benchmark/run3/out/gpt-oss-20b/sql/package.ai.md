### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:04:12Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database‑centric modules that manage library lending, e‑commerce order lines, and warehouse inventory synchronization across PostgreSQL, MySQL, and SQL Server.

#### Purpose
- Persist and enforce business rules for library catalog, member, and loan data.
- Atomically manage order line items, inventory reservations, and order totals in an e‑commerce context.
- Synchronize nightly supplier feeds with warehouse stock levels while maintaining an audit trail.

#### Responsibilities
- **Library** – data definition, borrowing logic, audit logging, reporting, migration, seeding.
- **Shop** – order line CRUD, inventory locking, total recalculation.
- **Warehouse** – supplier feed ingestion, stock reconciliation, sync logging.

#### Key units
- **Tables**  
  - `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy` (PostgreSQL).  
  - `product`, `customer_order`, `order_item` (MySQL).  
  - `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` (SQL Server).
- **Functions / Procedures**  
  - `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn` (PostgreSQL).  
  - `add_order_item(p_order_id, p_sku, p_qty)` (MySQL).  
  - `dbo.usp_SyncStock(@Since DATETIME2)` (SQL Server).
- **Views / Materialized Views**  
  - `overdue_loans`, `member_loan_stats`, `popular_books` (PostgreSQL).

#### Data flow
1. **Library** – checkout → validate member & copies → insert `loan` → trigger logs `CHECKOUT`; return → update `loan` → trigger logs `RETURN`.  
2. **Shop** – `add_order_item` locks `product`, validates stock, inserts/updates `order_item`, decrements `product.stock`, recomputes `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows, merges into `StockLevel` (insert or update), records counts and errors in `SyncLog`.

#### Dependencies
- **Internal** – foreign keys among library tables; MySQL tables linked by primary/foreign keys; SQL Server tables referenced by the sync procedure.  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`, `FILTER`, `HAVING`); MySQL 8 features (`SELECT … FOR UPDATE`, `ON DUPLICATE KEY UPDATE`, `ENUM`); SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`).

#### Cross-cutting
- **Audit & Logging** – `loan_audit` and `SyncLog` capture every state change.  
- **Constraints & Checks** – enforce copy counts, due dates, enum statuses, and birth year ranges.  
- **Transactionality** – each operation runs within a single transaction; MySQL procedure and SQL Server sync use implicit/explicit transaction control.  
- **Concurrency** – MySQL row‑level locking on `product`; PostgreSQL trigger ensures atomic audit logging.  
- **Refresh / Maintenance** – `popular_books` materialized view requires periodic refresh; migration script updates schema for branch and copy tracking.
