### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:26:15Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Multi-dialect SQL examples for library lending circulation, web-shop ordering, and warehouse stock synchronization.
#### Purpose
- Provide reference inventory/ordering schemas and operational routines across PostgreSQL, MySQL 8, and SQL Server.
- Demonstrate checkout/return, order placement with stock reservation, and feed-driven sync with auditing.
#### Responsibilities
- Library circulation (PostgreSQL): `author`/`book`/`member`/`loan` catalog and loans, `branch`/`book_copy` v2 migration, `overdue_loans`/`member_loan_stats`/`popular_books` reporting, `fn_checkout_book`/`sp_return_book`/`fn_late_fee` routines, `loan_audit` trail, seed dataset.
- Shop ordering (MySQL 8 InnoDB): `product` inventory with `sku`/`stock`, `customer_order` headers with `total_cents`, `order_item` lines via `add_order_item` with locking and total recompute.
- Warehouse sync (T-SQL): aggregate `SupplierFeed` since cutoff, `MERGE` into `StockLevel`, audit runs in `SyncLog` via `usp_SyncStock`.
#### Key units
- `author`, `book`, `member`, `loan`: library catalog and circulation base tables.
- `branch`, `book_copy`: library location and per-copy `barcode`/`status` inventory.
- `loan_audit`, `overdue_loans`, `member_loan_stats`, `popular_books`: checkout/return log and overdue/popularity aggregates.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit`: availability checks, copy counters, fee cap, lifecycle logging.
- `product`, `customer_order`, `order_item`: shop catalog, order header with `status`, order line with `quantity`/`unit_price_cents`.
- `add_order_item(p_order_id, p_sku, p_qty)`: SKU lock, stock validation, line upsert, `stock` decrement, total update.
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`: warehouse on-hand target, inbound feed source, run audit.
- `dbo.usp_SyncStock`: transactional sync with `@Since` cutoff, `@Changes` output, `RowsInserted`/`RowsUpdated` logging.
#### Data flow
- Library checkout: `member.is_blocked` + `copies_free` check → decrement → `INSERT loan` → `loan_audit CHECKOUT`; return → `returned_on` mark → increment → `RETURN` log + `fn_late_fee`.
- Library reporting: `loan` ⋈ `member`/`book`/`author` → overdue/member-stats/popular-books views; v2 migration: `generate_series` → `book_copy` inserts → `loan.copy_id` → drop `copies_*`.
- Shop order: `p_order_id/p_sku/p_qty` → `SELECT product FOR UPDATE` → `INSERT order_item ON DUPLICATE KEY UPDATE` → `UPDATE stock` → `SUM` → `UPDATE total_cents`.
- Warehouse sync: `@Since` + `SYSUTCDATETIME()` → `SUM(Quantity)` per `WarehouseId,Sku` → `MERGE` → `OUTPUT $action` → `SyncLog` insert/`COMMIT`; failure → `ROLLBACK` + error log + `THROW`.
#### Dependencies
- Internal: `book→author`, `loan→book/member/book_copy`, `book_copy→book/branch`, `member→branch`, `loan_audit→loan`; `order_item→customer_order/product`; `StockLevel↔SupplierFeed` on `(WarehouseId,Sku)` without FKs.
- External: PostgreSQL `BIGSERIAL`, `PL/pgSQL`, `generate_series`, `CURRENT_DATE`; MySQL 8 InnoDB `AUTO_INCREMENT`, `ENUM`, `SIGNAL SQLSTATE`, `utf8mb4`; SQL Server `IDENTITY`, `MERGE`, `DATETIME2`, `SYSUTCDATETIME()`, `$action`, `@@TRANCOUNT`.
#### Cross-cutting
- Key/index conventions: surrogate `BIGSERIAL`/`AUTO_INCREMENT`/`IDENTITY` PKs, `UNIQUE isbn/email/barcode/sku`, `CHECK` on library dates/copies/status, partial `idx_loan_open`, `uk_product_sku`, `PK_StockLevel(WarehouseId,Sku)`.
- Transactional stock handling: copy-counter updates, `FOR UPDATE` reservation, `MERGE` upsert with `BEGIN/COMMIT`.
- Error handling: `RAISE` on blocked/empty library stock, `SIGNAL SQLSTATE '45000'` on bad SKU/insufficient shop stock, `TRY/CATCH` + `SyncLog` + `THROW`/`ROLLBACK` in warehouse sync.
