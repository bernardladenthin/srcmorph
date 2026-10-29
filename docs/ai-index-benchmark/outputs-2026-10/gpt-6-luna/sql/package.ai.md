### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T21:23:31Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides database-backed workflows for library circulation, shop order fulfillment, and warehouse inventory synchronization.

#### Purpose
- Define SQL schemas and operational routines for lending-library catalog and loans, shop products and orders, and warehouse stock.
- Support inventory changes, derived reporting or totals, and operational auditing or synchronization logs.

#### Responsibilities
- **Library catalog and circulation:** Model authors, books, members, branches, physical copies, and loans; handle checkout, returns, late fees, audit events, and circulation reports.
- **Shop ordering:** Track products, customer orders, and order lines; validate and reserve stock while keeping order totals synchronized.
- **Warehouse synchronization:** Aggregate recent supplier-feed quantities into stock levels and record synchronization outcomes.

#### Key units
- `author`, `book`, `member`, `branch`, `book_copy`, `loan` — library catalog, membership, branch inventory, and circulation records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and capped late-fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member activity, and borrowing-frequency reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog and order data, with a procedure to add or increment a line, deduct stock, and recalculate totals.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — warehouse quantity input, inventory state, synchronization logging, and sync procedure.

#### Data flow
- **Library:** Seed data populates catalog, member, and loan records; checkout validates eligibility and availability, updates inventory, and creates an audited loan; return updates the loan and restores inventory. Views and materialized reporting derive overdue, member, and popularity data from loan history.
- **Shop:** `add_order_item` locks and checks a product by SKU, inserts or increments its order line, deducts requested stock, and recalculates `customer_order.total_cents`; invalid SKU or insufficient stock raises an error.
- **Warehouse:** Recent `SupplierFeed` rows are grouped by warehouse and SKU, merged into `StockLevel`, and summarized in `SyncLog`; failure rolls back inventory changes, logs the error, and rethrows.

#### Dependencies
- **Library:** Foreign keys connect books to authors, loans to books and members, and post-migration copies and members to branches; loans reference copies. Reporting uses loan, member, book, and author data.
- **Shop:** `order_item` references `customer_order` with `ON DELETE CASCADE` and references `product`; `add_order_item` reads and writes all three.
- **Warehouse:** `dbo.usp_SyncStock` uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`, plus SQL Server built-ins `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- Relational constraints enforce identifiers and valid relationships; library checks additionally constrain copy status and loan dates.
- Inventory mutations are coupled to corresponding operational records: library loans and audit events, shop order lines and totals, or warehouse synchronization logs.
- Error handling is workflow-specific: library routines reject blocked or invalid circulation operations, the shop procedure signals SQLSTATE `45000` for SKU or stock failures, and warehouse synchronization rolls back, logs, and rethrows.
- Transaction and concurrency controls include transactional library seed data, locked shop product rows, and transactional warehouse stock updates with success logging.
