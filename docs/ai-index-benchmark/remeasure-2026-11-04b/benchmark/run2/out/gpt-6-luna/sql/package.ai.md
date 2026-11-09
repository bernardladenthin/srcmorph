### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:00:31Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides SQL-backed workflows for library lending, shop order fulfillment, and warehouse inventory synchronization.

#### Purpose
- Defines relational data models and operational routines for catalog and lending, product orders, and supplier-fed stock.
- Supports transactional updates, reporting, and outcome auditing or logging across these domains.

#### Responsibilities
- **Library catalog and lending:** Store authors, books, members, branches, copies, and loans; handle checkout, returns, late fees, auditing, and lending reports.
- **Shop orders and inventory:** Maintain products, customer orders, and order lines; add requested items while reserving stock and recalculating order totals.
- **Warehouse synchronization:** Aggregate recent supplier quantities by warehouse and SKU, update stock levels, and log synchronization results or errors.

#### Key units
- `author`, `book`, `member`, `loan` — core library catalog and lending tables; `branch` and `book_copy` extend lending to branch-based, copy-level inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and overdue-fee routines.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library views and materialized ranking for lending reports.
- `product`, `customer_order`, `order_item` — shop catalog and inventory, order header, and order-line tables.
- `add_order_item(p_order_id, p_sku, p_qty)` — validates stock, updates order lines and product stock, and recalculates the order total.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — aggregates supplier feed quantities, merges stock changes, and logs synchronization outcomes.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse input, inventory, and synchronization-log tables.

#### Data flow
- Library seed data populates catalog, members, and loans; checkout and return routines update loan and availability state, while triggers audit transitions and reporting objects derive lending summaries.
- Shop `add_order_item` locks the matching product, validates SKU and stock, inserts or increments an order line, decrements stock, then recalculates the order total.
- Warehouse `usp_SyncStock` filters supplier rows by `@Since`, aggregates quantity by warehouse and SKU, merges changes into stock, counts insert/update actions, and logs success; on failure it rolls back, logs the error, and rethrows.

#### Dependencies
- Library routines and reports operate on PostgreSQL catalog and lending tables; migrated `book_copy` references `book` and `branch`, while loans and members gain copy and home-branch references.
- Shop `order_item` references `customer_order` with cascade deletion and `product`; `add_order_item` coordinates updates across those tables.
- Warehouse `dbo.usp_SyncStock` depends on SQL Server `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`; its `@Changes` table variable captures merge actions.

#### Cross-cutting
- Relational constraints, keys, and indexes enforce identity and relationships across catalog, order, and inventory records.
- Operational routines validate eligibility and availability, using SQL exceptions for library and shop failures.
- Updates use transactional safeguards: the shop locks the product row with `FOR UPDATE`; warehouse synchronization runs in a transaction and rolls back on error.
