### sql
- H: 1.0
- C: 69E20A85
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:46:41Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Provides SQL data models and transactional workflows for library lending, shop order fulfillment, and warehouse stock synchronization.

#### Purpose
- Defines three domain-specific database packages for catalog and lending operations, customer orders, and supplier-driven warehouse inventory.
- Combines operational routines, reporting, and synchronization logging across these database examples.

#### Responsibilities
- **Library lending and catalog:** Manage authors, books, members, loans, branch inventory, checkout, returns, late fees, audit events, and loan reports.
- **Shop orders and stock reservation:** Model products, customer orders, and order items; reserve stock and recalculate order totals when adding items.
- **Warehouse synchronization:** Aggregate supplier feed quantities into stock levels and log synchronization results or failures.

#### Key units
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate and process library loans, returns, and overdue fees.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report lending status and activity.
- `branch`, `book_copy`, `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — model branch-level copies and record loan events.
- `product`, `customer_order`, `order_item`, `add_order_item` — represent shop inventory and orders; add order lines while reserving stock.
- `dbo.usp_SyncStock(@Since)` — aggregate eligible supplier quantities into warehouse stock and record the outcome.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — store incoming quantities, current inventory, and sync results.

#### Data flow
- Library checkout and return routines update loans and inventory; audit triggers record checkout and first-return events, while reports derive status and activity from lending records.
- Shop order placement validates and locks product stock, inserts or increases an order line, decrements inventory, and recalculates the order total.
- Warehouse synchronization filters and aggregates supplier feed rows, merges quantities into stock, then logs counts; failures roll back and are logged before being rethrown.

#### Dependencies
- Library routines and reports use `author`, `book`, `member`, and `loan`; migration adds `branch` and `book_copy` relationships.
- Shop `order_item` references `customer_order` and `product`; `add_order_item` operates on product stock and order totals.
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and updates `dbo.StockLevel`, recording results in `dbo.SyncLog`.

#### Cross-cutting
- SQL constraints and foreign keys enforce domain relationships and valid records; monetary amounts are stored in cents in the shop schema.
- Transactional routines handle invalid operations with exceptions or SQL signals; warehouse synchronization rolls back and logs errors.
- Inventory updates are guarded by row locking in shop order placement and by a transaction around warehouse synchronization.
