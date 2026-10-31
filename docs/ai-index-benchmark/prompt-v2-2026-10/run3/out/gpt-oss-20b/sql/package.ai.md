### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:12:57Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of SQL packages that manage library lending, e‑commerce orders, and warehouse inventory, each with dedicated stored procedures, audit logging, and data‑flow pipelines.

#### Purpose
- Persist and enforce business rules for library loans, shop orders, and warehouse stock.
- Provide audit trails, reporting views, and transactional integrity across each domain.

#### Responsibilities
- **Library** – catalog, member, loan management; checkout/return logic; audit and reporting.
- **Shop** – product catalog, order processing; single procedure for adding items with stock checks.
- **Warehouse** – nightly supplier feed ingestion; inventory merge; sync logging.

#### Key units
- **Library**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`; procedure `sp_return_book`; trigger `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- **Warehouse**: tables `StockLevel`, `SupplierFeed`, `SyncLog`; procedure `usp_SyncStock`.

#### Data flow
1. **Library**: `fn_checkout_book` → validates → updates `book.copies_free` → inserts `loan` → trigger logs → `sp_return_book` updates loan and book copies → audit logs.
2. **Shop**: `add_order_item` locks product row → checks stock → inserts/updates `order_item` → decrements `product.stock` → recalculates `customer_order.total_cents`.
3. **Warehouse**: `usp_SyncStock` aggregates `SupplierFeed` since `@Since` → MERGE into `StockLevel` (insert or update) → counts rows → inserts a `SyncLog` record; all in one transaction.

#### Dependencies
- **Internal**: cross‑package tables (`author`, `book`, `product`, etc.) and procedures.
- **External**: PostgreSQL built‑ins (`CURRENT_DATE`, `now()`, `INTERVAL`); MySQL 8 features (ENUM, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`); SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross‑cutting
- **Audit & Logging**: triggers (`trg_loan_audit`) and log tables (`SyncLog`) capture all state changes.
- **Constraints & Checks**: enforce data integrity (e.g., `copies_free <= copies_total`, stock limits).
- **Error Handling**: procedures raise explicit errors for blocked members, insufficient stock, or missing products; warehouse sync logs errors and rolls back.
- **Concurrency Control**: `SELECT … FOR UPDATE` in shop; single‑transaction merges in warehouse; trigger‑based audit ensures atomicity.
- **Configuration**: default branch for library, default stock values, and UTC timestamps for warehouse logs.
