### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:54:11Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Defines database schemas and transactional workflows for library lending, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Model operational data and implement SQL routines for catalog and lending, product orders and inventory, and supplier-driven warehouse stock updates.
- Provide supporting reports, audit records, and synchronization outcomes across these domains.

#### Responsibilities
- **Library lending:** Maintain authors, books, members, branches, copies, and loans; support checkout, return, late-fee calculation, audit, and borrowing reports.
- **Shop orders:** Store products, customers’ orders, and priced order lines; add order items while reserving stock and recalculating totals.
- **Warehouse synchronization:** Aggregate recent supplier-feed quantities by warehouse and SKU, merge them into stock levels, and log run results or errors.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, branch inventory, and borrowing records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and capped late-fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and completed-return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member activity, and popular-title reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, order records, line items, and stock-reserving item placement.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — supplier quantities, warehouse stock, synchronization logs, and the stock-sync procedure.

#### Data flow
- Library catalog and lending records feed checkout and return routines, trigger-based audit entries, and reports derived from loan history.
- Shop item placement looks up and locks a SKU, inserts or increments its order line, deducts stock, and recalculates the order total.
- Warehouse synchronization filters supplier feeds by cutoff time, aggregates quantities by warehouse and SKU, merges stock changes, and logs success or failure.

#### Dependencies
- Library routines and reports depend on `author`, `book`, `member`, and `loan`; migrations add `branch` and `book_copy` relationships.
- Shop `add_order_item` reads and updates `product`, `order_item`, and `customer_order`; order lines reference orders and products.
- `dbo.usp_SyncStock` uses SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Relational constraints, foreign keys, and indexes support integrity and lookups; shop line items and warehouse stock use composite keys.
- Transactions protect library migration and seed operations, and warehouse stock merging and success logging.
- Error handling is domain-specific: library routines raise exceptions for invalid operations, shop item placement signals insufficient stock or unknown SKU, and warehouse sync rolls back, logs errors, and rethrows.
- Monetary amounts are stored in cents; warehouse synchronization uses UTC run timestamps.
