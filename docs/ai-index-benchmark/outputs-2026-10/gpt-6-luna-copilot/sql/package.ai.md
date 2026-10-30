### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:42Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Supports lending-library circulation, retail order fulfillment, and warehouse stock synchronization across catalog, inventory, and transaction records.

#### Purpose
- Define SQL data models and operations for library loans, shop orders, and warehouse inventory updates.
- Track outcomes through lending reports, order totals, audit events, and synchronization logs.

#### Responsibilities
- **Library circulation and reporting:** Manage catalog, members, branch copies, checkouts, returns, late fees, audit events, and lending reports.
- **Shop orders and inventory:** Maintain products, customer orders, and order lines; reserve stock and recalculate order totals.
- **Warehouse synchronization:** Aggregate recent supplier updates by warehouse and SKU, merge stock levels, and log results or errors.

#### Key units
- `library.author`, `book`, `member`, `loan`, `branch`, `book_copy` — library catalog, membership, branch inventory, and circulation records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — library checkout, return, and fee operations.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library lending and usage reports.
- `shop.product`, `customer_order`, `order_item`, `add_order_item` — product stock, orders, line items, and stock-aware order updates.
- `dbo.usp_SyncStock(@Since)`, `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — warehouse feed processing, stock persistence, and synchronization logging.

#### Data flow
- Library checkout and return operations update loans and copy availability; triggers record audit events, and reports derive from circulation data.
- `add_order_item` locks an eligible product, inserts or increments an order line, decrements stock, and recalculates the order total.
- `dbo.usp_SyncStock` filters supplier feed rows by `@Since`, groups quantities by warehouse and SKU, merges stock levels, and logs success or failure.

#### Dependencies
- Library routines and reports use `loan`, `member`, `book`, and related branch and copy records; migration uses PostgreSQL `generate_series`.
- Shop order lines reference `customer_order` and `product`; deleting an order cascades to its lines.
- Warehouse synchronization uses SQL Server `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- Constraints and indexes enforce data relationships and rules across the schemas.
- Shop product selection uses `SELECT ... FOR UPDATE`; library operations reject ineligible or invalid circulation requests.
- Warehouse failures roll back, log the error with zero counts, and rethrow.
