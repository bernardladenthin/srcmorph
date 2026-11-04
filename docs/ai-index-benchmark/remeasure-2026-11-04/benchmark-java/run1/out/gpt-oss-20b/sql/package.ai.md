### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:19:28Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A PostgreSQL‑, MySQL‑, and SQL Server‑based data layer that manages library cataloging, e‑commerce orders, and warehouse inventory, each with its own set of tables, stored procedures, and reporting views.

#### Purpose
- Persist and enforce business rules for library lending, online shop orders, and warehouse stock.
- Provide audit trails, reporting, and data integrity across three distinct domains.

#### Responsibilities
- **Library** – catalog, member, loan lifecycle, branch‑level copy tracking, overdue reporting.
- **Shop** – product catalog, order processing, stock reservation, total calculation.
- **Warehouse** – nightly supplier feed ingestion, inventory reconciliation, sync logging.

#### Key units
- **Library**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; functions `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; trigger `trg_loan_audit_fn`; views `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Shop**: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- **Warehouse**: tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Data flow
- **Library**: checkout → `fn_checkout_book` → loan record + audit; return → `sp_return_book` → update loan + audit; reporting views aggregate loan, member, and book data.
- **Shop**: `add_order_item` locks product, validates stock, inserts/updates `order_item`, decrements `product.stock`, recalculates `customer_order.total_cents`.
- **Warehouse**: `usp_SyncStock` aggregates new feed rows, merges into `StockLevel`, counts inserts/updates, logs summary in `SyncLog`.

#### Dependencies
- Internal cross‑module: foreign keys among core tables (e.g., `loan.member_id → member.id`, `order_item.product_sku → product.sku`).
- External: PostgreSQL built‑ins (`now()`, `CURRENT_DATE`), MySQL ENUM, AUTO_INCREMENT, SQL Server `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Shared constraints and indexes enforce data integrity and performance across all packages.

#### Cross-cutting
- **Integrity & Constraints**: primary keys, foreign keys, unique SKU, stock limits, due date checks.
- **Audit & Logging**: library audit trigger, warehouse sync log, error handling via exceptions or error messages.
- **Concurrency**: shop procedure uses `FOR UPDATE`; warehouse merge runs in a single transaction.
- **Reporting**: library read‑only views and materialized view; shop totals recalculated on each item addition; warehouse sync log provides operational metrics.
