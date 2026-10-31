### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:19:07Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Multi-domain SQL data model covering library lending circulation, web-shop ordering with stock reservation, and warehouse inventory synchronization.
#### Purpose
- Provide reference SQL schemas and routines for lending-library circulation, shop order placement, warehouse stock sync
#### Responsibilities
- Library circulation: `author`/`book`/`member`/`loan` entities, branch inventory migration to `branch`/`book_copy`, checkout/return/fee routines, overdue/popularity reporting, demo seeding
- Shop ordering: `product` catalog, `customer_order` headers, `order_item` lines, stock-checked `add_order_item` upsert with total maintenance
- Warehouse sync: `SupplierFeed` aggregation, `MERGE` into `StockLevel`, per-run auditing in `SyncLog` via `usp_SyncStock`
#### Key units
- `author`, `book`, `member`, `loan`: library core with `isbn`/`email` uniqueness, `copies_total`/`copies_free`, `due_on`/`returned_on`
- `branch`, `book_copy`: branch inventory with `barcode`, `status` AVAILABLE/ON_LOAN/LOST, `loan.copy_id`, `member.home_branch_id`
- `loan_audit`, `trg_loan_audit`: loan lifecycle log for CHECKOUT/RETURN actions
- `overdue_loans`, `member_loan_stats`, `popular_books`: overdue with `days_overdue`, per-member totals/open/last, most-loaned materialized view
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`: checkout with blocked/stock guards, return with `copies_free` increment, capped per-day fee calc
- `product`, `customer_order`, `order_item`: shop catalog with `sku`/`price_cents`/`stock`, order header with `status`/`total_cents`, lines keyed by `(order_id, product_id)`
- `add_order_item(p_order_id,p_sku,p_qty)`: `FOR UPDATE` stock reservation, line upsert, stock decrement, total recompute
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`: warehouse on-hand by `WarehouseId,Sku` with `OnHand`/`UpdatedAt`, inbound `Quantity`/`ReceivedAt`, sync audit with `RowsInserted`/`RowsUpdated`/`ErrorMessage`
- `dbo.usp_SyncStock @Since`: transactional `MERGE` + `SyncLog` insert with `TRY/CATCH`, `THROW`
#### Data flow
- Library checkout: `member.is_blocked` + `copies_free` check -> decrement -> `loan` INSERT with `due_on` -> `loan_audit` CHECKOUT; return validates open loan -> sets `returned_on` -> increments -> logs RETURN; `due_on`/`returned_on` -> `fn_late_fee`
- Library migration v2: `book.copies_total` x `generate_series` -> `book_copy` INSERT -> `loan.copy_id`/`home_branch_id` backfill -> drop aggregate counters
- Shop order: `p_sku` lookup `FOR UPDATE` + stock check -> `order_item` upsert with `unit_price_cents` -> `product.stock` decrement -> `customer_order.total_cents = SUM(quantity*unit_price_cents)`
- Warehouse sync: `SupplierFeed` filtered by `ReceivedAt >= @Since` -> `SUM(Quantity)` by `WarehouseId,Sku` -> `MERGE` matched update `OnHand+Quantity` / not-matched insert -> `$action` counts -> `SyncLog`
#### Dependencies
- Internal: `book.author_id->author`, `loan.book_id->book`, `loan.member_id->member`, `loan.copy_id->book_copy`, `book_copy.branch_id->branch`, `loan_audit.loan_id->loan`; `order_item.order_id->customer_order`, `order_item.product_id->product`; `StockLevel`<-`SupplierFeed`->`SyncLog`
- PostgreSQL library: `BIGSERIAL`/`SMALLSERIAL`, `PL/pgSQL`, `CURRENT_DATE`/`now()`/`INTERVAL`, `COUNT FILTER`, `generate_series`, partial index `WHERE returned_on IS NULL`
- MySQL 8 shop: `ENGINE=InnoDB`, `CHARSET=utf8mb4`, `AUTO_INCREMENT`, `ENUM('NEW','PAID','SHIPPED','CANCELLED')`, `ON DUPLICATE KEY UPDATE`, `SIGNAL SQLSTATE '45000'`
- T-SQL warehouse: `MERGE`, `$action`, `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`, `DATETIME2(0)`
#### Cross-cutting
- Integrity via `CHECK`/`UNIQUE`/`FK`: `birth_year` range, `copies_free<=copies_total`, `due_on>loaned_on`, `isbn`/`email`/`barcode`/`sku` uniqueness, `ON DELETE CASCADE`
- Guard/error pattern: blocked-member / insufficient-stock / already-returned rejection via `SIGNAL`, `THROW`, PL/pgSQL exceptions
- Audit pattern: `FOR EACH ROW` trigger on `returned_on` transition, `SyncLog` per-run counts, order `total_cents` maintenance
- Concurrency/transaction: `SELECT ... FOR UPDATE` stock lock, `COMMIT`/`ROLLBACK` with `@@TRANCOUNT` in sync, idempotent upserts
