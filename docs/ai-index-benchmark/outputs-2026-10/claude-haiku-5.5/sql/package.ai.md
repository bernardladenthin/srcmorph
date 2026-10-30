### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:41Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Multi-database SQL workspace covering a lending library system (PostgreSQL), a web shop order schema (MySQL 8), and a warehouse stock sync procedure (T-SQL).

#### Purpose
- Persistent schemas and business routines for three independent domains: library lending, web shop orders, and warehouse stock reconciliation.
- Each subfolder is a self-contained database artifact with its own DDL, routines, and test or migration scripts.

#### Responsibilities
- **Library lending (PostgreSQL)**: catalog (`author`, `book`), members, loans, and per-copy inventory (`book_copy`, `branch`); checkout/return routines, late fees, reporting views, and audit logging.
- **Web shop orders (MySQL 8)**: product catalog with stock, customer order headers, and line items; `add_order_item` reserves stock and recalculates totals.
- **Warehouse stock sync (T-SQL)**: nightly MERGE of aggregated supplier feed quantities into on-hand stock, with per-run audit logging and error handling.

#### Key units
- Library: `book`, `book_copy`, `loan`, `member`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `overdue_loans`, `popular_books`, `loan_audit`, `trg_loan_audit`.
- Shop: `product`, `customer_order`, `order_item`, `add_order_item`.
- Warehouse: `dbo.usp_SyncStock`, `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.

#### Data flow
- Library: checkout reads member block status and free copies, inserts `loan`, trigger writes `loan_audit`; return sets `loan.returned_on` and restores stock.
- Shop: `add_order_item` locks `product` row, inserts or increments `order_item`, decrements `stock`, recomputes `customer_order.total_cents`.
- Warehouse: `SupplierFeed` (since `@Since`, summed per warehouse and SKU) → MERGE → `StockLevel`; MERGE output counts → `SyncLog`.

#### Dependencies
- Library: PostgreSQL built-ins (`CURRENT_DATE`, `now()`, `generate_series`); migration v2 drops `book.copies_free`, which v1 routines still reference.
- Shop: MySQL 8 InnoDB, `SIGNAL SQLSTATE '45000'`, FKs `fk_item_order`, `fk_item_product`.
- Warehouse: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`; undeclared logical link `SupplierFeed` → `StockLevel`.

#### Cross-cutting
- Error handling: business-rule exceptions in each domain (PostgreSQL exceptions, MySQL `SIGNAL`, T-SQL `TRY/CATCH` with `THROW`).
- Transactions: explicit `BEGIN`/`COMMIT` (library scripts), single transaction around MERGE and log insert (warehouse).
- Concurrency: row locking via `SELECT ... FOR UPDATE` on `product` (shop).
- Integrity: CHECK constraints and FKs in library; unique SKU, composite PKs, cascade delete in shop; PK-only design in warehouse with no secondary indexes.
- Naming: `idx_`, `fn_`, `sp_`, `trg_` prefixes (library); `usp_` prefix (warehouse).
