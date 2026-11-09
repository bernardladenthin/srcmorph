### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:53Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A collection of SQL packages that manage library cataloging, e‑commerce orders, and warehouse inventory, each with dedicated data models, business logic, and audit/reporting capabilities.

#### Purpose
- Persist and manipulate domain data for libraries, shops, and warehouses.
- Provide transactional procedures for checkout, order item addition, and stock synchronization.
- Offer reporting views and audit trails for operational insight.

#### Responsibilities
- **Library** – catalog, member, loan, and audit management with reporting on overdue and popular items.
- **Shop** – product catalog, order processing, and inventory reservation with a single add‑item procedure.
- **Warehouse** – nightly supplier feed ingestion, stock reconciliation, and sync run logging.

#### Key units
- **Library**: tables `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`; procedure `sp_return_book`; trigger `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`.
- **Shop**: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- **Warehouse**: tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Data flow
1. **Library**: `fn_checkout_book` → checks status → updates `book_copy` → inserts `loan` → logs audit; `sp_return_book` → updates `loan` → restores `book_copy` → logs audit; `fn_late_fee` → calculates fee; views aggregate for reporting.
2. **Shop**: `add_order_item` → locks `product` → validates stock → inserts/updates `order_item` → decrements `product.stock` → recalculates `customer_order.total_cents`.
3. **Warehouse**: `dbo.usp_SyncStock(@Since)` → aggregates `SupplierFeed` → `MERGE` into `StockLevel` → records counts → inserts `SyncLog` with run details.

#### Dependencies
- **Internal**: cross‑package tables referenced only within their own package (e.g., `book_copy` in Library, `order_item` in Shop, `StockLevel` in Warehouse).
- **External**: PostgreSQL built‑ins (`CURRENT_DATE`, `now()`, `INTERVAL`); MySQL 8 features (`ENUM`, `FOR UPDATE`, `ON DUPLICATE KEY UPDATE`); SQL Server system functions (`SYSUTCDATETIME()`, `ERROR_MESSAGE()`).

#### Cross-cutting
- **Audit & Logging**: Library `loan_audit`; Warehouse `SyncLog`.
- **Concurrency Control**: Shop uses row locking; Warehouse operations wrapped in single transactions.
- **Integrity**: Foreign keys, cascading deletes, and status checks enforce consistency across all packages.
- **Indexes & Constraints**: Strategic indexes (`idx_book_author`, `idx_loan_member`, etc.) and check constraints (`copies_free <= copies_total`) optimize performance and data validity.
