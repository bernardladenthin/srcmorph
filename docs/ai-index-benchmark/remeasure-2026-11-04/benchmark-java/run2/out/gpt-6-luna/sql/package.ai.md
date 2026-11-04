### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:32:17Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library lending, web-shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines schemas and operational routines for three business domains: borrowing and library inventory, product orders and stock reservation, and supplier-driven warehouse replenishment.
- Provides reporting, auditing, and synchronization outcome records alongside transactional data updates.

#### Responsibilities
- **Library lending (`library/`):** Manage catalogue, members, branch-aware physical copies, loans, checkout and return, late fees, audit events, and borrowing reports; include migration and sample-data scripts.
- **Shop orders (`shop/`):** Manage products, customer orders, and order lines; validate stock, reserve inventory when adding items, and recalculate order totals.
- **Warehouse inventory (`warehouse/`):** Aggregate recent supplier-feed quantities into warehouse/SKU stock levels and log synchronization results or errors.

#### Key units
- `author`, `book`, `member`, `branch`, `book_copy`, `loan`, `loan_audit` — library catalogue, membership, physical inventory, lending, and audit records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and overdue-fee operations.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member-activity, and popular-title reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop inventory and order-line model with stock reservation and total recalculation.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock` — warehouse input feed, current stock, synchronization log, and sync procedure.

#### Data flow
- Library checkout validates member and inventory state, creates a loan, and triggers audit recording; return updates the loan and inventory and records the return. Views and the materialized report derive lending activity from catalogue, member, and loan data.
- Shop `add_order_item` finds a product by SKU, checks available stock, inserts or increments an order line, decrements stock, and recalculates the order total.
- Warehouse `dbo.usp_SyncStock` filters supplier rows by `@Since`, aggregates quantities by warehouse and SKU, merges totals into stock levels, and logs counts; on failure it rolls back, logs the error, and rethrows.

#### Dependencies
- Library relationships connect books to authors, loans to members and books, and migrated copies and members to branches; reporting and routines use these records.
- Shop order lines reference `customer_order` and `product`; order deletion cascades to its lines.
- Warehouse synchronization reads `dbo.SupplierFeed`, merges into `dbo.StockLevel` by warehouse and SKU, and writes `dbo.SyncLog`.
- The package contains PostgreSQL library scripts, MySQL 8 shop objects, and SQL Server warehouse objects.

#### Cross-cutting
- Relational constraints, foreign keys, indexes, and domain checks enforce valid identifiers, relationships, and statuses.
- Operational errors are handled explicitly: shop item addition signals unknown SKU or insufficient stock; warehouse sync rolls back and records an error before rethrowing.
- Auditing and operational records capture library loan events and warehouse synchronization outcomes.
