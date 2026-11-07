### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:51:08Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library lending, retail order fulfillment, and warehouse stock synchronization.

#### Purpose
- Provides SQL schemas, reports, routines, migrations, and seed data for three operational domains: library circulation, shop orders, and warehouse inventory.
- Keeps inventory and transaction records synchronized through checkout/return, order-item, and supplier-feed workflows.

#### Responsibilities
- **Library catalog and circulation:** Store authors, books, members, loans, branch locations, and individual copies; provide overdue and member activity reports.
- **Library loan operations and migration:** Check out and return books, calculate late fees, audit loan events, migrate from book-level counts to branch-level copies, and seed sample records.
- **Shop catalog and order fulfillment:** Track products, SKUs, stock, customer orders, and order items; reserve stock and recalculate order totals.
- **Warehouse stock synchronization:** Aggregate recent supplier feeds by warehouse and SKU, merge quantities into stock levels, and log synchronization results.

#### Key units
- `library.author`, `library.book`, `library.member`, `library.loan` — library catalog, membership, and borrowing records.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library reports for overdue loans, member borrowing activity, and frequently borrowed books.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and capped overdue-fee workflows.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record library checkout and return events.
- `branch`, `book_copy`, `04_migration_v2.sql` — represent branch inventory and migrate existing book counts to individual copies.
- `shop.product`, `shop.customer_order`, `shop.order_item` — shop catalog, customer orders, and per-order product quantities.
- `add_order_item` — MySQL procedure that validates and locks a product by SKU, updates order lines and stock, and recalculates the order total.
- `dbo.usp_SyncStock(@Since)` — aggregates supplier feed quantities, merges them into stock levels, and logs the outcome.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse feed input, current stock, and synchronization result records.

#### Data flow
- Library seed data populates catalog, membership, and loan records; checkout and return routines update lending and inventory records, while a trigger audits loan events.
- Library views derive overdue and member statistics from loans and members; `popular_books` materializes recent borrowing counts.
- `add_order_item` locks an in-stock product by SKU, inserts or increments its order line, decrements stock, and recalculates `customer_order.total_cents`.
- `dbo.usp_SyncStock` filters `dbo.SupplierFeed` by `ReceivedAt >= @Since`, groups quantities by warehouse and SKU, merges totals into `dbo.StockLevel`, and records insert/update counts in `dbo.SyncLog`.

#### Dependencies
- Library reports and routines use `loan`, `member`, `book`, and `author`; loan auditing writes to `loan_audit`.
- The library inventory migration uses existing `book`, `loan`, and `member` data and PostgreSQL `generate_series`.
- `shop.add_order_item` depends on `product`, `customer_order`, and `order_item`; order-item foreign keys connect orders and products, with order deletion cascading to items.
- `dbo.usp_SyncStock` depends on SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`, plus `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- Relational constraints and indexes enforce identifiers, relationships, inventory bounds, and report access.
- Shop monetary amounts use integer cents; product lookup uses `FOR UPDATE`, and invalid or insufficient stock signals SQLSTATE `45000`.
- Warehouse synchronization uses a transaction; failures roll back active work, log zero counts and the error, then rethrow.
- Library summaries describe differing inventory schemas: migration removes `book.copies_free` and `book.copies_total`, while checkout and return routines are also summarized as updating `book.copies_free`.
