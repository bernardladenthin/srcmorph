### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:39:27Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Manages inventory and transactions across library lending, retail order fulfillment, and warehouse supplier-stock reconciliation.

#### Purpose
- Define SQL-backed workflows for catalog and copy availability, customer orders and stock reservation, and warehouse stock updates.
- Provide reporting, lifecycle auditing, schema migration, and synchronization outcome logging.

#### Responsibilities
- **Library lending:** Maintain authors, books, members, loans, branches, and individually tracked copies; enforce checkout and return rules, calculate late fees, audit loan events, and report overdue loans and borrowing activity.
- **Retail orders:** Store products, customers’ orders, and order lines; add or increment order items while reserving stock and recalculating order totals.
- **Warehouse reconciliation:** Aggregate recent supplier receipts by warehouse and SKU, merge quantities into stock balances, and log success or failure.
- **Schema and test data:** Seed library records and migrate library inventory from aggregate book counts to branch-assigned copies.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, borrowing, and branch-level inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit` — library loan lifecycle, fee calculation, and event auditing.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library loan and popularity reporting.
- `product`, `customer_order`, `order_item`, `add_order_item` — retail catalog, order records, and stock-reserving item updates.
- `dbo.usp_SyncStock`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse receipt reconciliation, stock balances, and synchronization logging.

#### Data flow
- Library checkout validates member eligibility and book availability, creates a loan, updates inventory, and records an audit event; returns close open loans, restore availability, and record return events. Reporting views derive loan and popularity information from the library tables.
- Retail `add_order_item` finds a product by SKU, checks and locks stock, inserts or increments an order line, decrements stock, and recalculates the order total.
- Warehouse `dbo.usp_SyncStock` filters supplier receipts by cutoff, aggregates quantities by warehouse and SKU, merges totals into `dbo.StockLevel`, and logs row counts; on failure it rolls back, logs the error, and rethrows it.

#### Dependencies
- Library views and routines use `author`, `book`, `member`, and `loan`; loan auditing uses `loan_audit`. The migration uses PostgreSQL `generate_series` to create copy records.
- Retail `order_item` references `customer_order` and `product`; stock updates and totals use `product`, `order_item`, and `customer_order`.
- Warehouse synchronization uses SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- SQL implementations span PostgreSQL DDL, views, materialized views, PL/pgSQL routines, triggers, migrations, and seed data; MySQL 8 tables and stored procedures; and SQL Server stored procedures.
- Integrity and concurrency controls include keys, checks, foreign keys, indexes, transaction handling, and row locking.
- Error handling is domain-specific: library routines reject invalid or unavailable loans, retail reports unknown SKUs or insufficient stock, and warehouse synchronization rolls back, logs failure details, and rethrows errors.
