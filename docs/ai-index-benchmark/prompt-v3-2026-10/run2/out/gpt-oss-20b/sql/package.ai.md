### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:23:06Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of database schemas and stored procedures that manage inventory, sales, and supplier feeds across a library, online shop, and warehouse system.

#### Purpose
- Persist and enforce business rules for library lending, e‑commerce orders, and warehouse stock.
- Provide reporting, audit trails, and data ingestion pipelines.

#### Responsibilities
- **Library** – catalog, member, loan, reporting, audit, and branch‑aware inventory.
- **Shop** – product catalog, order processing, stock reservation, and total calculation.
- **Warehouse** – nightly supplier feed ingestion, stock reconciliation, and sync audit logging.

#### Key units
- **Tables**  
  - Library: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.  
  - Shop: `product`, `customer_order`, `order_item`.  
  - Warehouse: `StockLevel`, `SupplierFeed`, `SyncLog`.  
- **Views / Materialized Views** – `overdue_loans`, `member_loan_stats`, `popular_books`.  
- **Functions / Procedures** – `fn_checkout_book`, `fn_late_fee`, `sp_return_book`, `add_order_item`, `usp_SyncStock`.  
- **Triggers / Indexes** – `trg_loan_audit`, `idx_popular_books_id`, `idx_book_copy_book`.

#### Data flow
1. **Library** – `fn_checkout_book` creates a loan, updates `book_copy.status`, logs `CHECKOUT`; `sp_return_book` marks return, updates status, logs `RETURN`; `fn_late_fee` calculates overdue charges.  
2. **Shop** – `add_order_item` locks the product row, verifies stock, inserts/updates an order line, decrements stock, and recalculates the order total.  
3. **Warehouse** – `usp_SyncStock` aggregates new supplier feed rows, merges into `StockLevel`, counts inserts/updates, and records the run in `SyncLog`.

#### Dependencies
- **Internal** – foreign keys among `loan`, `book_copy`, `member`, `branch`; `order_item` ↔ `customer_order` ↔ `product`; `StockLevel` ↔ `SupplierFeed`.  
- **External** – PostgreSQL built‑ins (`CURRENT_DATE`, `now()`); MySQL transactional locking (`FOR UPDATE`); SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).  

#### Cross‑cutting
- **Audit & Logging** – `loan_audit` and `SyncLog` capture every change.  
- **Branch/Location Awareness** – `branch` and `book_copy` tables replace aggregate copy counts.  
- **Transactional Integrity** – all critical operations run in a single transaction with row‑level locking and error handling.  
- **Seeding & Reporting** – development data via `05_seed_data.sql`; reporting views provide overdue and popularity metrics.
