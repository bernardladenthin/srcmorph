### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:01:24Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> This package suite provides distinct persistence layers for managing library lending, e-commerce order fulfillment, and automated warehouse inventory synchronization.

#### Purpose
* To define and manage the data models and transactional logic for three distinct business domains: library resource lending, web shop order processing, and warehouse stock synchronization.

#### Responsibilities
* Library Management: Defining schemas for books, members, and loans, and executing checkout/return transactions.
* E-commerce Operations: Defining product and order schemas, and managing atomic order placement with stock reservation.
* Inventory Synchronization: Orchestrating nightly updates of master stock levels from external supplier feeds.

#### Key units
* Library: `author`, `book`, `member`, `loan`, `book_copy`, `fn_checkout_book`, `sp_return_book`, `overdue_loans`.
* Shop: `product`, `customer_order`, `order_item`, `add_order_item`.
* Warehouse: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `usp_SyncStock`.

#### Data flow
* Library: Seeding data flows into core tables, which are then modified by routines (`fn_checkout_book`, `sp_return_book`) and read by reporting views (`overdue_loans`).
* Shop: Input data flows into `add_order_item`, which reads `product` data, writes to `order_item`, and updates `product` stock and `customer_order` totals.
* Warehouse: Data flows from `dbo.SupplierFeed` into `usp_SyncStock`, which performs a `MERGE` operation on `dbo.StockLevel` and logs results in `dbo.SyncLog`.

#### Dependencies
* Library: Depends on PostgreSQL features (PL/pgSQL, Triggers, Views, Materialized Views) and internal relationships between `author`, `book`, `loan`, and `member`.
* Shop: Relies on MySQL schema definitions and transactional locking mechanisms.
* Warehouse: Depends on `dbo.SupplierFeed` for input and modifies `dbo.StockLevel` and `dbo.SyncLog`.

#### Cross-cutting
* Transactional Integrity: Enforced via constraints/triggers in the library module and row locking in the shop module.
* Auditing: Centralized logging of state changes via `loan_audit` (Library) and `dbo.SyncLog` (Warehouse).
* Schema Management: Utilizes migrations for library schema evolution and specific SQL dialects (PostgreSQL vs. MySQL vs. T-SQL).
