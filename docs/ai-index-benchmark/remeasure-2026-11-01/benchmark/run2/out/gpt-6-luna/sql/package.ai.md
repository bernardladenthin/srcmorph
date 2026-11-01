### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:31Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Defines database workflows for library lending, webshop ordering, and warehouse stock synchronization.

#### Purpose
- Groups SQL schemas, routines, reports, and migrations for managing borrowing, product orders, and warehouse inventory.
- Covers operational updates that maintain availability or stock and, where applicable, record audit or synchronization outcomes.

#### Responsibilities
- **Library lending (`library/`):** Models catalog, members, loans, branches, and individual book copies; handles checkout, returns, late fees, audit history, and borrowing reports.
- **Webshop purchasing (`shop/`):** Stores products, customer orders, and order lines; adds items while reserving stock and recalculating order totals.
- **Warehouse synchronization (`warehouse/`):** Aggregates supplier receipts since a timestamp, merges quantities into warehouse stock, and logs each run’s outcome.

#### Key units
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate and process library loans, restore availability, and calculate capped late fees.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — provide library loan and popularity reports.
- `add_order_item` — updates an order line, reserves product stock, and refreshes the order total.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — merges eligible supplier-feed totals into stock and logs success or failure.
- `author`, `book`, `member`, `loan`, `branch`, `book_copy`; `product`, `customer_order`, `order_item`; `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — domain tables for the three workflows.

#### Data flow
- **Library:** Checkout validates member and book availability, creates a loan, updates availability, and triggers audit recording; returns update the loan and availability, with reports derived from loan and catalog data.
- **Shop:** `add_order_item` locks a product selected by SKU, validates stock, inserts or increments an order line, decrements stock, and recalculates the order total.
- **Warehouse:** `dbo.usp_SyncStock` filters supplier receipts by `@Since`, groups quantities by warehouse and SKU, merges them into `dbo.StockLevel`, and records counts or error details in `dbo.SyncLog`.

#### Dependencies
- **Database systems:** PostgreSQL routines and migration in `library/`; MySQL 8 tables and procedure in `shop/`; SQL Server objects under `dbo` in `warehouse/`.
- Library operations and reports use `author`, `book`, `member`, and `loan`; branch and copy inventory links through `branch`, `book_copy`, `loan.copy_id`, and `member.home_branch_id`.
- `add_order_item` reads and updates `product`, `order_item`, and `customer_order`; order lines reference order headers and products.
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed`, updates `dbo.StockLevel`, and writes `dbo.SyncLog`.

#### Cross-cutting
- Workflows enforce domain constraints and handle invalid or unavailable inputs; shop product selection and warehouse stock updates use transactional locking or transaction control.
- Monetary values in the shop are stored in cents; library checkout and returns are audited, while warehouse synchronization records run outcomes.
