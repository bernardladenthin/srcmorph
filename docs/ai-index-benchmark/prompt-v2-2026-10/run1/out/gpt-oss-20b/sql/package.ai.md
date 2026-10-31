### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:43:39Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas and stored procedures that manage library, shop, and warehouse data, enforce business rules, and provide reporting and audit trails.

#### Purpose
- Persist and manipulate domain data for libraries, e‑commerce shops, and warehouses.
- Enforce transactional integrity, business rules, and audit logging.
- Offer reporting views and migration support for evolving schemas.

#### Responsibilities
- **Schema definition** – tables, constraints, indexes for authors, books, members, loans, products, orders, inventory, and supplier feeds.
- **Business logic** – stored functions/procedures for checkout/return, order item addition, and stock synchronization.
- **Reporting & analytics** – views for overdue loans, member statistics, popular books, and materialized popular‑books counts.
- **Migration & evolution** – branch‑based copy model, book copy table, and loan reference updates.
- **Audit & logging** – loan audit trail, sync logs, and transaction‑level error handling.

#### Key units
- **Library**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`; functions `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- **Warehouse**: tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Data flow
1. **Library checkout/return**  
   - `fn_checkout_book` validates member and availability, updates `book.copies_free`, inserts `loan`, and triggers audit.  
   - `sp_return_book` updates `loan.returned_on`, restores `book.copies_free`, and triggers audit.
2. **Shop order item addition**  
   - `add_order_item` locks `product`, checks stock, upserts `order_item`, decrements stock, and recalculates `customer_order.total_cents`.
3. **Warehouse sync**  
   - `dbo.usp_SyncStock` aggregates `SupplierFeed` rows, merges into `StockLevel`, counts actions, and records a `SyncLog` entry.

#### Dependencies
- **Internal**: cross‑package tables (`author`, `book`, `member`, `loan`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`).  
- **External**: PostgreSQL built‑ins (`now()`, `generate_series`), PL/pgSQL; MySQL 8 features (`ENUM`, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`); SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross‑cutting
- **Integrity** – PK/FK constraints, check constraints, unique indexes, cascade deletes, and transaction isolation.  
- **Audit** – `loan_audit` and `SyncLog` capture every state change.  
- **Concurrency** – row‑level locking (`SELECT … FOR UPDATE`) and upsert patterns maintain consistency.  
- **Performance** – targeted indexes (`idx_loan_open`, foreign‑key indexes), materialized view for popular books, and merge operations for bulk updates.  
- **Configuration** – hard‑coded late‑fee rate/cap in `fn_late_fee`; enum status values in shop orders.
