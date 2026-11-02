### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:37Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides SQL data models and transactional workflows for library circulation, webshop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Groups database schemas, reporting objects, and stored routines for three operational domains: lending libraries, online shops, and warehouse inventory.
- Covers inventory changes, transaction processing, audit or sync logging, and domain-specific reporting.

#### Responsibilities
- **Library catalog and circulation:** Model authors, books, members, branches, physical copies, and loans; support checkout, return, late fees, audit events, and lending reports.
- **Shop orders and inventory:** Define products, customer orders, and order lines; add items while reserving stock and recalculating order totals.
- **Warehouse stock synchronization:** Aggregate recent supplier feeds into warehouse/SKU balances and log synchronization counts or errors.
- **Schema evolution and setup:** Migrate library inventory from book-level counts to branch-level copies and provide library sample data.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, circulation, and branch inventory tables.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library views for past-due loans, member borrowing summaries, and frequently loaned books.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library routines for checkout, return, and capped late-fee calculation.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and first-return audit events.
- `04_migration_v2.sql` — converts library inventory tracking to branch-level copies and adds copy and home-branch references.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, orders, order lines, and stock-reserving item-addition procedure.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since)` — warehouse feed, balance, and sync-log tables plus the transactional synchronization procedure.

#### Data flow
- Library checkout validates a member and available inventory, creates a loan, and records an audit event; return closes an open loan, restores availability, and records the first-return event. Views derive lending reports from catalog, member, and loan data.
- Shop item addition looks up and locks a product by SKU, inserts or increments its order line, decrements stock, and recalculates the order total.
- Warehouse synchronization filters supplier feeds by cutoff, aggregates quantities by warehouse and SKU, merges balances, and commits counts to the sync log; failures roll back and log the error.

#### Dependencies
- Library routines and views operate on related `author`, `book`, `member`, `loan`, `book_copy`, `branch`, and `loan_audit` data.
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed`, merges into `dbo.StockLevel`, and writes to `dbo.SyncLog`.
- The package spans PostgreSQL library objects, MySQL 8 shop objects, and SQL Server warehouse objects.

#### Cross-cutting
- Relational constraints and foreign keys connect domain records; uniqueness and validity checks protect identifiers, inventory values, statuses, and loan dates.
- Transactional routines handle invalid operations or failures explicitly: library checkout/return validation, shop SQLSTATE `45000` errors, and warehouse rollback, error logging, and rethrow.
- Inventory concurrency is addressed by locking the shop product row during stock validation and reservation.
