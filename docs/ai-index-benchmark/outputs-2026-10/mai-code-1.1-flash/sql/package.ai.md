### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:49:10Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> This package covers three SQL-backed operational domains: library circulation and overdue management, e-commerce order fulfillment, and warehouse stock synchronization.

#### Purpose
- Provide schema, business logic, and reconciliation routines for library lending, shop order processing, and warehouse stock updates.
- Enforce transactional integrity, reporting, and operational auditing across each SQL domain.

#### Responsibilities
- Library catalog and circulation: `author`, `book`, `member`, `loan`, overdue reporting, checkout/return flows, and lifecycle auditing.
- Shop catalog and order lifecycle: `product`, `customer_order`, `order_item`, stock reservation, and order total recalculation.
- Warehouse stock reconciliation: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, and nightly merge-based sync from supplier feed to live stock.
- Data migration and seeding: branch-aware inventory migration in the library, plus realistic operational seed data and defaults across the SQL objects.

#### Key units
- `author` — library author record with identity and metadata for catalog entries.
- `book` — catalog edition with `isbn`, `title`, `author_id`, and inventory counts/edition metadata.
- `member` — borrower identity with email, name, join date, and blocking status.
- `loan` — borrowing transaction linking books, members, and dates for due/return tracking.
- `overdue_loans` — view listing open loans past due with borrower and title context.
- `member_loan_stats` — view aggregating per-member borrowing activity and last-loan timing.
- `popular_books` — materialized view ranking books by recent loan volume.
- `fn_checkout_book` — PostgreSQL function validating member status and available copies before loan creation.
- `sp_return_book` — procedure marking a loan returned and restoring inventory availability.
- `fn_late_fee` — function computing overdue charges with zero-floor and cap logic.
- `trg_loan_audit` / `trg_loan_audit_fn` — trigger-based audit writes for `CHECKOUT` and `RETURN` events.
- `branch` / `book_copy` — branch-based inventory model replacing per-book counters in the migration.
- `product` — catalog item with `sku`, `name`, `price_cents`, and stock quantity.
- `customer_order` — order header with lifecycle status and `total_cents` summary.
- `order_item` — order-line join table with quantity and unit price.
- `add_order_item` — MySQL procedure validating SKU and stock, reserving quantity, and recalculating totals.
- `dbo.usp_SyncStock` — SQL Server stored procedure merging supplier feed deltas into live stock and logging results.
- `dbo.StockLevel` — aggregate warehouse stock table keyed by `(WarehouseId, Sku)`.
- `dbo.SupplierFeed` — supplier event feed table for quantity deltas by warehouse and item.
- `dbo.SyncLog` — sync audit table recording row counts and error details.
- `@Changes` — table variable capturing `INSERT`/`UPDATE` actions during the merge.
- `@Since` — cutoff timestamp limiting the sync to supplier updates in scope.

#### Data flow
- Library flow: base schema definitions establish `author`, `book`, `member`, and `loan` relationships; checkout creates `loan` records while inventory counts adjust; return marks completion and restores availability; reporting views read the same relational model for overdue and trend insights; `loan_audit` records `CHECKOUT` and `RETURN` activity.
- Shop flow: `add_order_item` receives `p_order_id`, `p_sku`, and `p_qty`; it selects the matching `product`, checks stock under row locking, and rejects invalid or insufficient requests; on success it inserts or updates `order_item`, decrements `product.stock`, and recomputes `customer_order.total_cents`.
- Warehouse flow: `dbo.SupplierFeed` rows newer than `@Since` are aggregated by `WarehouseId` and `Sku`; `MERGE` matches them to `dbo.StockLevel`, adds deltas to `OnHand`, and inserts missing stock rows; `OUTPUT $action INTO @Changes` tracks inserts and updates; `dbo.SyncLog` writes totals for the run and captures rollback/error records on failure.

#### Dependencies
- Internal:
  - Library: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`, `overdue_loans`, `member_loan_stats`, `popular_books`.
  - Shop: `product`, `customer_order`, `order_item`.
  - Warehouse: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `@Changes`, `@Since`.
- External / platform:
  - PostgreSQL schema DDL and PL/pgSQL routines, including triggers and procedural SQL.
  - MySQL transactional stock validation with `FOR UPDATE` and `SIGNAL`.
  - SQL Server T-SQL with `MERGE`, `OUTPUT $action`, `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`, and `BEGIN CATCH`/`THROW`.
  - SQL functions and expressions: `COUNT`, `MAX`, `FILTER`, `COALESCE`, `SUM`, `CURRENT_DATE`, `CURRENT_TIMESTAMP`, `SYSUTCDATETIME`, `ERROR_MESSAGE`, `generate_series`, and `INTERVAL`.

#### Cross-cutting
- Transactional integrity: library loan lifecycle and shop stock changes rely on validation and update rules; warehouse sync uses explicit transaction boundaries and rollback on error.
- State recomputation: `book.copies_free`, `customer_order.total_cents`, and `dbo.StockLevel.OnHand` are derived from child rows and not treated as independent sources of truth.
- Auditability: `loan_audit` and `dbo.SyncLog` capture lifecycle and sync events; invalid stock or processing failures are surfaced through SQL exception paths.
- Migration and model evolution: library inventory shifts from book-level counters to branch-aware copy tracking; warehouse and shop schemas rely on natural keys and defaults instead of deep inheritance.
- Validation and constraints: member blocking prevents checkout, stock checks prevent invalid order writes, due-date logic caps fees, and insert/update rules enforce consistent operational states.
- Defaulting patterns: `CURRENT_TIMESTAMP`, `status = 'NEW'`, `total_cents = 0`, `OnHand = 0`, and `UpdatedAt = SYSUTCDATETIME()` establish predictable initial state.
