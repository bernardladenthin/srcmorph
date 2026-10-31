### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:52:04Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library circulation, shop order and inventory management, and supplier-driven warehouse stock synchronization.

#### Purpose
- Define SQL schemas and operations for three business domains: lending libraries, web-shop ordering, and warehouse inventory.
- Support transactional workflows for borrowing and returns, stock reservation during order entry, and supplier-feed synchronization.

#### Responsibilities
- **Library circulation (`library/`):** Manage catalog, members, branches, copy inventory, loans, returns, late fees, audit events, sample data, and lending reports.
- **Shop ordering (`shop/`):** Store products, customers’ orders, and order lines; add items while reserving stock and recalculating order totals.
- **Warehouse synchronization (`warehouse/`):** Aggregate recent supplier quantities into warehouse stock and log synchronization outcomes.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, branch, and circulation records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and overdue-fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and first-return audit events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library borrowing and activity reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, order storage, and item addition with stock reservation.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — warehouse feed, stock, run logging, and synchronization procedure.

#### Data flow
- Library seed data populates catalog, members, and loan history; migration expands book-level counts into branch-specific copies. Checkout validates member and inventory, creates a loan, and audits it; return updates loan and copy state and audits the first return. Reports aggregate circulation records.
- Shop `add_order_item` locks and validates a product by SKU, inserts or increments an order line at the selected price, decrements stock, and recalculates the order total.
- Warehouse `dbo.usp_SyncStock` filters supplier feeds by cutoff, aggregates quantities by warehouse and SKU, merges them into stock, then logs row counts; failures roll back active changes, log the error, and rethrow.

#### Dependencies
- Library relationships connect `book` to `author`, `loan` to `book` and `member`, `book_copy` to `book` and `branch`, and members to home branches; reporting joins loan, member, book, and author data.
- Shop order lines reference `customer_order` and `product`; order deletion cascades to its lines.
- Warehouse synchronization uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`, with UTC timestamps from `SYSUTCDATETIME()`.

#### Cross-cutting
- SQL implementations span PostgreSQL DDL, PL/pgSQL routines, triggers, and views; MySQL tables and procedures; and SQL Server stored procedures.
- Workflows use transactions for warehouse synchronization and row locking for shop stock reservation; library constraints enforce key uniqueness and valid circulation state.
