### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:34:45Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides database-backed workflows for library lending, retail order fulfillment, and warehouse inventory synchronization.

#### Purpose
- Defines schemas, operational routines, reporting, and audit records for three domains: library circulation, shop orders, and warehouse stock.
- Supports inventory changes through lending and returns, order-item reservations, and supplier-feed synchronization.

#### Responsibilities
- **Library lending (`library/`):** Manages catalog, members, branches, physical book copies, checkout and return workflows, late fees, loan auditing, reporting, and seed data.
- **Shop ordering (`shop/`):** Stores products, customers’ orders, and order lines; adds items while reserving stock and recalculating order totals.
- **Warehouse synchronization (`warehouse/`):** Aggregates supplier feed quantities by warehouse and SKU, updates stock balances, and records success or failure for each run.

#### Key units
- **Library:** `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`; reporting views `overdue_loans`, `member_loan_stats`, and `popular_books`; routines `fn_checkout_book`, `sp_return_book`, `fn_late_fee`; audit trigger `trg_loan_audit`.
- **Shop:** `product`, `customer_order`, `order_item`, and `add_order_item`.
- **Warehouse:** `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, and `dbo.usp_SyncStock`.

#### Data flow
- Library checkout validates member and book availability, records a loan, updates inventory, and audits the checkout; returns update the loan and inventory and audit the return. Views and `popular_books` derive reports from catalog, member, and loan data.
- Shop `add_order_item` finds an eligible product by SKU, adds or increments the order line, decrements stock, and recalculates the order total; missing or understocked products raise a SQL error.
- Warehouse `dbo.usp_SyncStock` filters supplier rows by cutoff, groups quantities by warehouse and SKU, merges aggregates into stock, and logs row counts; failures are logged and rethrown.

#### Dependencies
- Library views and routines use `author`, `book`, `member`, and `loan`; audit logic writes to `loan_audit`. Its branch-aware migration builds on existing book, loan, and member data.
- Shop `order_item` references `customer_order` and `product`; product lookup locks eligible stock.
- Warehouse synchronization matches `dbo.SupplierFeed` to `dbo.StockLevel` by `WarehouseId` and `Sku`, using SQL Server T-SQL features including `MERGE`.

#### Cross-cutting
- The package spans PostgreSQL library objects, MySQL 8 shop objects, and SQL Server T-SQL warehouse objects.
- Database constraints and foreign keys protect domain relationships; routines handle inventory mutations and error cases.
- Warehouse synchronization uses transactions and UTC timestamps; library migration is transactional, and the library audit trigger records loan state transitions.
