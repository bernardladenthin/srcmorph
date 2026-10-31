### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:21:45Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> The package provides three independent database systems: a lending library (managing books, authors, members, loans), a web shop (managing products, orders), and a warehouse (syncing supplier feeds to stock levels).

#### Purpose
- Library: manages books, authors, members, loans with copy tracking and audit logs.
- Shop: handles product catalog and order placement with automatic stock reservation.
- Warehouse: performs nightly MERGE synchronization of supplier feed quantities into stock levels with error logging.

#### Responsibilities
- Library: schema design, reporting views, loan business logic, migration to branch-based copy tracking, seed data.
- Shop: define product/order tables, implement `add_order_item` procedure for stock reservation and total recalculation.
- Warehouse: ingest supplier feed data, update stock via MERGE, log row counts and errors in a sync audit table.

#### Key units
- Library: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`; functions `fn_checkout_book`, `fn_late_fee`, trigger function `trg_loan_audit_fn`, trigger `trg_loan_audit`.
- Shop: `product`, `customer_order`, `order_item`; stored procedure `add_order_item(p_order_id, p_sku, p_qty)`.
- Warehouse: stored procedure `dbo.usp_SyncStock(@Since DATETIME2(0))`; tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; local table variable `@Changes`.

#### Data flow
- Library: seed data initializes data; `fn_checkout_book` adjusts free copies and logs audit on checkout; `sp_return_book` updates return date and increments free count; views query `loan`, `member`, `book`, `author` for reports; migration transforms copy tracking model.
- Shop: `add_order_item` reads `product` (stock check), `order_item` (subtotal), `customer_order` (total), writes to `product` (decrement stock), `order_item` (insert/update quantity), `customer_order` (update total).
- Warehouse: `usp_SyncStock` reads `SupplierFeed` rows since `@Since`, groups quantities, MERGEs into `StockLevel`, outputs counts via `@Changes`, inserts success log or error log with message.

#### Dependencies
- Library: internal tables `author`, `book`, `member`, `loan`, `loan_audit`; PostgreSQL built-ins (`CURRENT_DATE`, `now()`, `generate_series`, `'365 days'`).
- Shop: internal tables `product`, `customer_order`, `order_item`; MySQL built-ins (`CURRENT_TIMESTAMP`, `AUTO_INCREMENT`).
- Warehouse: internal tables `StockLevel`, `SupplierFeed`, `SyncLog`; system functions `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Library: constraints enforce `book_free_le_total`, loan due date logic, unique ISBN/email; indexes support foreign-key lookups; audit pattern via `trg_loan_audit_fn`; copy tracking evolved from per-book counts to `book_copy` rows with `status`; time calculations use `CURRENT_DATE` and `INTERVAL '365 days'`.
- Shop: uses `ON DUPLICATE KEY UPDATE` for idempotent `order_item` insertion; primary/foreign keys enforce integrity; index `idx_order_customer` for query performance; stock reservation is atomic with total recalculation.
- Warehouse: single transaction for MERGE and logging; MERGE uses `$action` output to track inserts vs updates; timestamp filtering via `@Since` and `ReceivedAt`; error handling rolls back, logs `ErrorMessage`, re-throws exception; explicit primary keys (`IDENTITY`, composite key).

> *(Note: All statements are based solely on the provided per-file summaries. No files or content beyond those summaries are referenced.)*
