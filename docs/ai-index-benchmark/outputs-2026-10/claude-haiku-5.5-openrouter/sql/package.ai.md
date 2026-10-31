### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:48Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi-database SQL workspace covering a PostgreSQL lending library, a MySQL web-shop order system, and a SQL Server nightly warehouse stock reconciliation.

#### Purpose
- Groups three independent SQL domains: library lending (`library/`), web-shop orders (`shop/`), and warehouse inventory sync (`warehouse/`).
- Each subdirectory is a self-contained schema-plus-routines unit targeting a different engine (PostgreSQL, MySQL 8, SQL Server).

#### Responsibilities
- Library lending: authors, books, copies, members, loans, late fees, overdue/popularity reporting, audit trail, and a v2 per-copy migration (`library/`).
- Web-shop ordering: product catalog with stock, customer orders and lines, and stock-reserving order placement (`shop/`).
- Warehouse reconciliation: folding supplier feed receipts into on-hand stock with per-run audit logging (`warehouse/`).

#### Key units
- Library: `loan`, `book`, `book_copy`, `member`, `branch`, `loan_audit`; `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit`; views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`.
- Shop: `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- Warehouse: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Data flow
- Library: schema, then seed; checkout inserts loans and logs audit; return sets `returned_on` and restocks; reporting views read core tables.
- Shop: caller passes order, SKU and quantity to `add_order_item`, which row-locks product, upserts the line, decrements stock and recomputes the order total.
- Warehouse: `SupplierFeed` aggregated since a cutoff, MERGEd into `StockLevel`, counts written to `SyncLog`.

#### Dependencies
- Library: PostgreSQL built-ins (`CURRENT_DATE`, `generate_series`, `FILTER`); internal ordering `01` before `02`/`03`, with `04` migration and `05` seed.
- Shop: MySQL 8 InnoDB; FKs `fk_item_order`, `fk_item_product`.
- Warehouse: SQL Server built-ins (`MERGE`, `OUTPUT $action`, `THROW`, `SYSUTCDATETIME()`); logical join on `(WarehouseId, Sku)`, no declared FK.

#### Cross-cutting
- Error handling: library routines raise exceptions; shop signals SQLSTATE `45000`; warehouse rolls back, logs `ErrorMessage`, then rethrows.
- Money stored as integer cents in library fees and shop totals.
- Timestamps: library uses `CURRENT_DATE`/`now()`; warehouse uses UTC `SYSUTCDATETIME()`.
- Known incompatibility: library `03_routines.sql` references `book.copies_free`, which `04_migration_v2.sql` drops; routines need updating after migration.
- Concurrency: shop uses `FOR UPDATE`; library and warehouse locking not detailed in summaries.
