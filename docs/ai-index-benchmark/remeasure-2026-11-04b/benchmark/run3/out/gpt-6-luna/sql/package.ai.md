### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:41Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Implements database workflows for library circulation, shop order fulfillment, and warehouse stock synchronization across three SQL systems.

#### Purpose
- Defines relational schemas and operational SQL for lending-library inventory, customer orders, and warehouse stock.
- Provides domain-specific routines, reporting, auditing, migrations, and synchronization logging.

#### Responsibilities
- **Library catalog and circulation (PostgreSQL):** Manage authors, books, members, loans, branches, individually tracked copies, checkout and returns, late fees, audit events, and borrowing reports.
- **Shop catalog and order placement (MySQL):** Maintain products and stock, track customer orders and line items, and add items while reserving inventory and recalculating totals.
- **Warehouse inventory synchronization (SQL Server):** Aggregate recent supplier-feed quantities into stock balances and log synchronization results or errors.

#### Key units
- `author`, `book`, `member`, `loan` — library catalog, membership, and borrowing records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate checkout, process returns, and calculate overdue charges.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report overdue items, member activity, and frequently borrowed books.
- `branch`, `book_copy` — represent library locations and barcoded copies.
- `product`, `customer_order`, `order_item` — shop catalog, order, and order-line records.
- `add_order_item` — add or increment an order line, deduct stock, and recalculate the order total.
- `dbo.usp_SyncStock(@Since)` — merge aggregated supplier quantities into warehouse stock and log the outcome.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — provide incoming quantities, current balances, and synchronization records.

#### Data flow
- **Library:** Checkout validates member eligibility and availability, creates a loan, and triggers an audit; return updates the loan and inventory and triggers a return audit. Reports aggregate loan, member, book, and author data. A migration converts book-level counts into branch-based copy records.
- **Shop:** `add_order_item` finds and locks an eligible product by SKU, inserts or increments its order line, reduces stock, and recomputes the order total; unavailable stock or an unknown SKU raises an error.
- **Warehouse:** `dbo.usp_SyncStock` groups eligible `dbo.SupplierFeed` quantities by warehouse and SKU, inserts or increments matching `dbo.StockLevel` rows, then logs affected-row counts; failures roll back when needed, log the error, and rethrow.

#### Dependencies
- **Library:** PostgreSQL tables reference one another through book, member, branch, and copy relationships; views and routines use catalog and circulation data. Uses built-ins including `CURRENT_DATE`, `now()`, and `generate_series`.
- **Shop:** MySQL `order_item` references `customer_order` and `product`; `add_order_item` reads products and order lines and updates order lines, stock, and order totals.
- **Warehouse:** SQL Server `dbo.usp_SyncStock` uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`; feed-to-stock matching uses warehouse and SKU values without declared foreign keys.

#### Cross-cutting
- Each domain enforces its own data integrity: library constraints cover dates, statuses, uniqueness, and inventory bounds; shop lines use a composite key and amounts are stored in cents; warehouse synchronization uses transactional error handling.
- Inventory changes are coupled to their operations: checkout and return update library availability, order placement reserves shop stock, and supplier synchronization adjusts warehouse balances.
- SQL dialects and mechanisms differ by domain: PostgreSQL functions, procedures, views, and triggers; a MySQL locking procedure; and a SQL Server transactional merge with action capture.
