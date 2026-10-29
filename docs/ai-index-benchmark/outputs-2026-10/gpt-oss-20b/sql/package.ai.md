### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T22:54:54Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi‑tenant data layer that manages library lending, e‑commerce orders, and warehouse inventory through PostgreSQL, MySQL, and SQL Server stored procedures, tables, and views.

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.
- Provide audit trails, reporting, and synchronization logs across three distinct domains.

#### Responsibilities
- **Library Management** – catalog, member, loan tables; checkout/return logic; late‑fee calculation; overdue and popularity reports.
- **Shop Order Processing** – order line insertion, stock reservation, total recalculation with row locking.
- **Warehouse Synchronization** – nightly supplier feed ingestion, stock reconciliation via MERGE, sync run logging.

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
  - Procedure: `dbo.usp_SyncStock(@Since DATETIME2)`.

#### Data flow
1. **Library** – `fn_checkout_book` validates member and book, updates `book.copies_free`, inserts `loan`, logs audit; `sp_return_book` updates loan, restores copies, logs audit; `fn_late_fee` computes fees; reporting views aggregate loan, member, and book data.
2. **Shop** – `add_order_item` locks `product`, checks stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
3. **Warehouse** – `dbo.usp_SyncStock` aggregates new `SupplierFeed` rows, MERGE into `StockLevel`, counts actions, inserts a `SyncLog` record, and handles errors transactionally.

#### Dependencies
- **Internal** – cross‑module tables (`author`, `book`, `member`, `loan`, `product`, `customer_order`, `order_item`, `StockLevel`, `SupplierFeed`, `SyncLog`).  
- **External** – PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), MySQL 8 features (`FOR UPDATE`, `SIGNAL`, `ENUM`), SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`), standard SQL constraints and indexes.

#### Cross-cutting
- **Audit & Logging** – `loan_audit`/`trg_loan_audit_fn` and `SyncLog` centralize change tracking.  
- **Constraints & Indexes** – enforce data integrity and performance across all tables.  
- **Error Handling** – functions raise errors for blocked members or insufficient stock; procedures signal or rollback on failures.  
- **Atomicity** – stored procedures encapsulate multi‑step updates within a single transaction.  
- **Configuration** – default status values, fee caps, and stock limits are defined in table defaults and constraints.
