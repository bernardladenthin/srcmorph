### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:14:06Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library circulation, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines relational schemas and operational routines for lending books, reserving shop inventory for orders, and synchronizing warehouse stock from supplier feeds.
- Includes reporting, audit, and run-outcome tracking where applicable.

#### Responsibilities
- **Library lending and inventory:** Catalog, member, loan, branch, and individual-copy records; checkout, returns, late fees, audit history, and lending reports.
- **Shop orders and stock reservation:** Product and order tables; add order items by SKU while deducting stock and recalculating order totals.
- **Warehouse stock synchronization:** Aggregate recent supplier quantities into stock balances and log successful or failed synchronization runs.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, circulation, and branch inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and overdue-fee rules.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record library checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library circulation reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, order lines, stock reservation, and total recalculation.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — warehouse quantity inputs, current balances, synchronization outcomes, and the sync procedure.

#### Data flow
- Library checkout validates members and copy availability, creates a loan, and records the event; return updates the loan and inventory, with audit tracking and late-fee calculation. Reporting units aggregate lending records.
- Shop item addition locks a product by SKU, inserts or increments an order line, deducts stock, and recalculates the order total.
- Warehouse synchronization filters supplier-feed rows by cutoff, aggregates quantities by warehouse and SKU, merges them into stock balances, and logs counts; failures roll back, log the error, and rethrow.

#### Dependencies
- Library routines and reports operate on `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, and `book_copy`; foreign keys link catalog, loans, members, and copies.
- Shop `add_order_item` uses `product`, `order_item`, and `customer_order`; order items reference products and orders.
- Warehouse `dbo.usp_SyncStock` uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- The package spans PostgreSQL library operations, MySQL 8 shop operations, and SQL Server T-SQL warehouse synchronization.
- Database constraints and indexes support valid records and lookups; locking protects shop product stock updates.
- Library checkout and return reject invalid member, availability, or loan states; warehouse changes and success logging are transactional, with rollback and error logging on failure.
