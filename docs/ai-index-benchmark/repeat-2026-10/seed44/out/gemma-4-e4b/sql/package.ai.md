### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:31Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> This package suite provides distinct, specialized persistence and operational logic for managing library lending, e-commerce inventory, and warehouse stock synchronization.

#### Purpose
* To define and manage the data persistence layers for three distinct domains: library lending, product sales, and warehouse inventory.
* To implement core business logic for transactions (lending, ordering, stock updates) within each domain.

#### Responsibilities
* Library Lending Management: Defining core entities, managing loan transactions, and generating library reports.
* E-commerce Order Processing: Defining product catalogs, tracking customer orders, and managing stock reservation during order placement.
* Warehouse Inventory Synchronization: Ingesting and merging stock levels from external supplier feeds into master warehouse records.

#### Key units
* Library Lending: `author`, `book`, `member`, `loan`, `fn_checkout_book`, `overdue_loans`.
* E-commerce: `product`, `customer_order`, `order_item`, `add_order_item`.
* Warehouse: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.usp_SyncStock`, `dbo.SyncLog`.

#### Data flow
* Library: Seeding data flows into core tables, which are then modified by routines like `fn_checkout_book`, with results audited in `loan_audit`.
* E-commerce: Input data flows into `add_order_item`, which validates stock in `product` before updating `order_item` and `customer_order`.
* Warehouse: Data flows from `dbo.SupplierFeed` into `dbo.usp_SyncStock`, which applies changes to `dbo.StockLevel` and logs the outcome in `dbo.SyncLog`.

#### Dependencies
* Library: Depends on PostgreSQL features and relies on `01_schema.sql` as its foundation.
* E-commerce: Depends on the defined structure of `product`, `customer_order`, and `order_item` tables.
* Warehouse: Depends on the existence of `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog` tables.

#### Cross-cutting
* Transactional Integrity: Enforced via PL/pgSQL routines (Library) and MySQL procedures (Shop), and explicit transaction blocks (Warehouse).
* Auditing/Logging: Implemented via `loan_audit` (Library) and `dbo.SyncLog` (Warehouse).
* Schema Management: Includes schema evolution support via migration scripts (`04_migration_v2.sql` in Library).
