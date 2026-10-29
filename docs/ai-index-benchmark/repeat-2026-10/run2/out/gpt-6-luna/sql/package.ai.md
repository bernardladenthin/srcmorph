### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:52:24Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> Defines relational database operations for library circulation, shop ordering and stock reservation, and warehouse inventory synchronization.

#### Purpose
- Provides SQL schemas, procedures, reporting, and inventory workflows across library, retail, and warehouse domains.
- Enforces data integrity and records operational outcomes, including circulation audits and synchronization logs.

#### Responsibilities
- **Library catalog and circulation:** Manage authors, books, members, loans, checkout and returns, late fees, audit events, and circulation reports; migrate inventory to branch-assigned, individually tracked copies.
- **Shop orders and stock:** Store products, customer orders, and order lines; validate SKU and availability, reserve stock, and recalculate order totals.
- **Warehouse stock synchronization:** Aggregate recent supplier feeds into per-warehouse SKU stock levels and log sync counts or errors.

#### Key units
- `author`, `book`, `member`, `loan` — library catalog, membership, and circulation records; `branch` and `book_copy` — branch-aware, individually identified inventory.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — validate and process library loans, returns, and capped overdue charges.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return transitions.
- `overdue_loans`, `member_loan_stats`, `popular_books` — library overdue, member activity, and popular-book reports.
- `product`, `customer_order`, `order_item`, `add_order_item` — shop catalog and order lines, with a procedure to update stock and order totals.
- `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, `dbo.usp_SyncStock(@Since)` — warehouse feed aggregation, stock updates, and synchronization outcome logging.

#### Data flow
- Library seed data populates catalog, membership, and loan tables; checkout validates availability, creates a loan, and triggers an audit record, while return updates the loan, restores availability, and records the return. Reports aggregate circulation data; migration transforms aggregate book counts into branch-assigned copy records.
- Shop `add_order_item` locks a matching product with sufficient stock, inserts or increments its order line, decrements stock, and recalculates the order total; invalid SKU or stock raises an error.
- Warehouse `usp_SyncStock` filters supplier feed rows by `@Since`, aggregates quantities by warehouse and SKU, merges totals into `StockLevel`, and logs counts; failures roll back stock changes, log the error, and rethrow.

#### Dependencies
- Library routines and reports use `loan`, `member`, `book`, and `author`; audit triggers write to `loan_audit`. Migration links copies to books and branches, loans to copies, and members to home branches.
- Shop `add_order_item` uses `product`, `order_item`, and `customer_order`; order lines reference orders and products.
- Warehouse `usp_SyncStock` reads `dbo.SupplierFeed` and merges into `dbo.StockLevel`, then writes `dbo.SyncLog`.

#### Cross-cutting
- Relational constraints, keys, checks, and indexes enforce integrity and support lookups across the SQL schemas.
- Invalid circulation, order, or synchronization conditions are handled through exceptions or errors; library transitions and warehouse sync outcomes are recorded.
- Concurrency and transaction handling include row locking for shop product selection and transactional stock updates with rollback in warehouse synchronization.
