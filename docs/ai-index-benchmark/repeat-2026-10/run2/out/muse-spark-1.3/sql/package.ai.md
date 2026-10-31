### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:58Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Multi-domain SQL reference schemas for library circulation, web-shop ordering, and warehouse inventory synchronization.
#### Purpose
- Provide self-contained SQL domain models for lending-library management, e-commerce order placement, and stock synchronization
- Demonstrate constraints, routines, and reporting/audit patterns across PostgreSQL, MySQL 8, and T-SQL
#### Responsibilities
- Lending library circulation: `author/book/member/loan` schema, branch holdings via `branch/book_copy`, checkout/return/fee routines, overdue/popularity reporting, audit and seed fixture
- Web shop ordering: `product/customer_order/order_item` catalog and orders, stock-reserving `add_order_item` line placement with total maintenance
- Warehouse sync: nightly `SupplierFeed` to `StockLevel` merge with `SyncLog` run auditing
#### Key units
- `author`, `book(isbn, copies_total/copies_free)`, `member(is_blocked)`, `loan(due_on/returned_on/copy_id)`: loan lifecycle core
- `branch`, `book_copy(barcode, status AVAILABLE/ON_LOAN/LOST)`: per-copy branch inventory
- `loan_audit`, `overdue_loans(days_overdue)`, `member_loan_stats`, `popular_books(times_loaned)`: audit trail and circulation reports
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit`: checkout, return, fee, audit trigger
- `product(sku/price_cents/stock)`, `customer_order(status/total_cents)`, `order_item(order_id/product_id/quantity/unit_price_cents)`: shop catalog and order lines
- `add_order_item(p_order_id,p_sku,p_qty)`: locked stock check, upsert line, total recalc
- `dbo.usp_SyncStock(@Since)`, `dbo.StockLevel(OnHand)`, `dbo.SupplierFeed(Quantity/ReceivedAt)`, `dbo.SyncLog(RowsInserted/RowsUpdated/ErrorMessage)`: sync procedure and tables
#### Data flow
- Library checkout: `member.is_blocked` + `book.copies_free` check → decrement + `INSERT loan` → `trg_loan_audit CHECKOUT`; return reverses to `RETURN`
- Library migration/reporting: `generate_series` expands aggregates to `book_copy` + `branch/home_branch_id/copy_id`; `loan JOIN member/book/author` feeds `overdue_loans/member_loan_stats/popular_books`
- Shop order: `sku/stock FOR UPDATE` → `INSERT ... ON DUPLICATE KEY UPDATE order_item` → decrement `product.stock` → `SUM(quantity*unit_price_cents)` to `customer_order.total_cents`
- Warehouse sync: `SupplierFeed WHERE ReceivedAt>=@Since` → `SUM(Quantity) GROUP BY WarehouseId,Sku` → `MERGE INTO StockLevel OUTPUT $action` → counts to `SyncLog`
#### Dependencies
- Internal: `book→author`, `loan→book/member/book_copy`, `book_copy→book/branch`, `member→branch`, `loan_audit→loan`; `order_item→product/customer_order ON DELETE CASCADE`; `usp_SyncStock` ↔ `StockLevel/SupplierFeed/SyncLog`
- External: PostgreSQL `BIGSERIAL/PL/pgSQL/partial idx_loan_open/generate_series/CURRENT_DATE`; MySQL 8 InnoDB `utf8mb4/DELIMITER`; T-SQL `MERGE/SYSUTCDATETIME/TRY/CATCH/@@TRANCOUNT`
#### Cross-cutting
- Constraint-driven integrity: `UNIQUE isbn/email/barcode/sku`, `CHECK` on dates/copies/status/price, `FK REFERENCES`, composite `PRIMARY KEY(order_id,product_id)` and `WarehouseId,Sku` keys
- Indexing/locking: `idx_book_author/idx_loan_member/idx_loan_open/idx_order_customer`, `SELECT ... FOR UPDATE`, `SET NOCOUNT ON`, UTC timestamps
- Error/audit pattern: `SIGNAL SQLSTATE '45000'` on stock failure, `TRY/CATCH ROLLBACK+THROW` with `SyncLog.ErrorMessage`, `AFTER INSERT OR UPDATE` loan audit, deterministic `BEGIN/COMMIT` seed inserts
