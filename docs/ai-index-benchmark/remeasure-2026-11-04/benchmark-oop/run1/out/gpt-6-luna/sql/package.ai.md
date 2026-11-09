### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:23:57Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides database structures and transactional operations for library lending, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines domain-specific schemas, routines, and reports for library catalog and loans, shop products and orders, and warehouse inventory.
- Handles key workflows including checkout and returns, stock-reserving order updates, and supplier-feed synchronization.

#### Responsibilities
- **Library lending:** Manage authors, books, members, branches, book copies, loans, auditing, late fees, and borrowing reports.
- **Shop purchasing:** Store product inventory, customer orders, and order lines; add items while reserving stock and recalculating order totals.
- **Warehouse synchronization:** Aggregate timestamp-filtered supplier receipts into stock levels and log synchronization outcomes.

#### Key units
- `author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, branch inventory, and borrowing records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate and process lending operations and calculate capped late charges.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report overdue borrowing, member activity, and frequently loaned titles.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog, order data, and stock-aware order-line updates.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since)` — warehouse receipt input, on-hand inventory, synchronization logging, and the sync procedure.

#### Data flow
- **Library:** Catalog, member, and loan records feed reports; checkout creates a loan and reduces available inventory, while return closes an open loan and restores a copy. Triggers audit loan state transitions.
- **Shop:** `add_order_item` locks and validates a product by SKU, inserts or increments its order line, decrements stock, and recalculates `customer_order.total_cents`.
- **Warehouse:** `usp_SyncStock` filters `SupplierFeed` by receipt time, aggregates quantities by warehouse and SKU, merges totals into `StockLevel`, and logs counts or failure details.

#### Dependencies
- Library routines and reports operate on the library catalog, member, loan, branch, and copy tables; migration converts legacy book copy counts into branch-specific copy records.
- Shop `order_item` references `customer_order` and `product`; `add_order_item` reads and updates all three.
- Warehouse `usp_SyncStock` reads `dbo.SupplierFeed`, writes `dbo.StockLevel`, and records results in `dbo.SyncLog`.
- Uses distinct SQL platforms: PostgreSQL for library operations, MySQL 8 for shop orders, and SQL Server for warehouse synchronization.

#### Cross-cutting
- Referential integrity and constraints protect library relationships, copy and audit statuses, shop order lines, and unique product SKUs.
- Inventory changes are coupled to business operations: lending updates copy availability, shop orders reserve product stock, and warehouse sync applies aggregated receipts.
- Error handling includes shop validation errors, warehouse transactional rollback and error logging, and library loan-event auditing.
