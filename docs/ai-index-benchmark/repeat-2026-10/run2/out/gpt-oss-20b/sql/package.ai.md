### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:18:25Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A set of relational schemas and stored procedures that manage library cataloging, webshop orders, and warehouse inventory, each with integrated business logic, reporting, and audit trails.  

#### Purpose  
- Persist and enforce business rules for library, shop, and warehouse data.  
- Provide reporting views and audit logs.  
- Expose single‑entry stored procedures for common transactional operations.  

#### Responsibilities  
- **Library** – catalog, member, loan management; checkout/return logic; overdue and popularity reporting.  
- **Shop** – product catalog, order processing, stock reservation, order total calculation.  
- **Warehouse** – nightly supplier feed ingestion, inventory aggregation, sync audit logging.  

#### Key units  
- **Library**  
  - Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Views: `overdue_loans`, `member_loan_stats`, `popular_books`.  
  - Functions/Procedures: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.  
  - Trigger: `trg_loan_audit` on `loan`.  
- **Shop**  
  - Tables: `product`, `customer_order`, `order_item`.  
  - Procedure: `add_order_item(p_order_id, p_sku, p_qty)`.  
- **Warehouse**  
  - Tables: `StockLevel`, `SupplierFeed`, `SyncLog`.  
  - Procedure: `usp_SyncStock(@Since)`.  

#### Data flow  
1. **Library** – `fn_checkout_book` validates member, updates `book.copies_free`, inserts `loan`, triggers audit. `sp_return_book` updates `loan.returned_on`, restores copies, triggers audit. Views aggregate loan data for reporting.  
2. **Shop** – `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.  
3. **Warehouse** – `usp_SyncStock` aggregates `SupplierFeed` rows by `(WarehouseId, Sku)`, merges into `StockLevel`, logs run details in `SyncLog`.  

#### Dependencies  
- **Internal** – cross‑package tables referenced by procedures and triggers.  
- **External** – PostgreSQL system catalogs, MySQL 8 ENUM and auto‑increment, SQL Server system functions (`SYSUTCDATETIME`, `ERROR_MESSAGE`).  
- **Migration** – Library schema evolves with `branch` and `book_copy` tables.  

#### Cross‑cutting  
- **Audit & Logging** – `loan_audit` and `SyncLog` capture every state change.  
- **Constraints & Indexes** – PKs, FKs, checks, and indexes (`idx_book_author`, `idx_loan_member`, `idx_order_customer`).  
- **Error Handling** – Stored procedures raise errors for invalid SKUs, insufficient stock, or missing data; SQL Server `TRY…CATCH` logs failures.  
- **Transactional Integrity** – All critical updates occur within explicit transactions, ensuring atomicity.  
- **Late Fee Logic** – Library `fn_late_fee` caps fees at $20.00 with 50¢ per overdue day.  
- **Branch & Copy Model** – Library migration replaces aggregate copy counters with explicit `book_copy` rows linked to `branch`.
