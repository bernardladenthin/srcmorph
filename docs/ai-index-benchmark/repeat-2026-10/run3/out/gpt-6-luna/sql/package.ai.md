### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:24:32Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Supports library lending, online order inventory reservation, and warehouse stock synchronization through database schemas and transactional SQL routines.

#### Purpose
- Defines three database domains: library catalog and lending, shop products and orders, and warehouse inventory synchronization.
- Provides reporting, stock updates, audit records, and error logging appropriate to each domain.

#### Responsibilities
- **Library lending (`library/`):** Maintain catalog, members, loans, branch-based copy inventory, checkout and return operations, loan auditing, and borrowing reports.
- **Shop ordering (`shop/`):** Store products and customer orders; add order items while reserving stock and recalculating totals.
- **Warehouse synchronization (`warehouse/`):** Aggregate supplier-feed quantities into stock balances and record synchronization outcomes.

#### Key units
- `author`, `book`, `member`, `loan` — library catalog, membership, and borrowing records.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate and process lending operations and calculate overdue fees.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record loan checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report overdue items, member activity, and frequently borrowed books.
- `branch`, `book_copy`, `04_migration_v2.sql` — model library locations and individual copies; migrate legacy inventory to copy records.
- `product`, `customer_order`, `order_item` — shop catalog, orders, and order-line quantity and price snapshots.
- `add_order_item` — reserve product stock, upsert an order line, and recalculate its order total.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — merge recent supplier quantities into warehouse stock and log the result.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog` — supply inventory inputs, maintain balances, and record synchronization outcomes.

#### Data flow
- **Library:** Seed catalog, member, and loan records; checkout validates eligibility and availability before creating a loan and audit event; returns update loan and inventory state and trigger auditing. Reports derive from loan and catalog data. Migration creates individual copies from legacy counts and assigns branches.
- **Shop:** `add_order_item` locks a SKU-matched product with sufficient stock, inserts or increments its order line, decrements stock, and recalculates the order total.
- **Warehouse:** `usp_SyncStock` filters supplier feeds by `@Since`, aggregates quantities by warehouse and SKU, merges them into stock balances, and logs inserted/updated counts or an error.

#### Dependencies
- **Library:** PostgreSQL tables, views, routines, and triggers collaborate through catalog, member, loan, audit, branch, and copy records; migration uses `generate_series`.
- **Shop:** MySQL 8 with InnoDB; order lines reference `customer_order` and `product`.
- **Warehouse:** `dbo.usp_SyncStock` reads `dbo.SupplierFeed`, updates `dbo.StockLevel`, and writes `dbo.SyncLog`.

#### Cross-cutting
- Database constraints and keys enforce relationships and domain invariants; shop order lines use a composite key, and warehouse stock is keyed by warehouse and SKU.
- Operations use transactions or row locks to protect inventory updates; library and shop routines signal invalid or unavailable operations with errors.
- Warehouse synchronization rolls back active work on failure, records `ERROR_MESSAGE()` with zero counts, and rethrows the error.
