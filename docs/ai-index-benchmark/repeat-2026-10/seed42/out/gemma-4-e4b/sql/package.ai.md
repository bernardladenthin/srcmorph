### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:54:16Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> This package suite provides distinct, specialized persistence layers for library management, e-commerce order processing, and automated inventory synchronization.

#### Purpose
* To manage the core data models and transactional logic for lending libraries (`library/`).
* To handle atomic order creation and inventory reservation for e-commerce (`shop/`).
* To automate the nightly synchronization of external supplier stock data into master inventory records (`warehouse/`).

#### Responsibilities
* Library Data Modeling and Operations: Defining schemas for books, members, and loans, and executing checkout/return routines (`library/`).
* E-commerce Transaction Management: Defining product/order schemas and ensuring atomic stock deduction during order placement (`shop/`).
* Inventory Synchronization: Merging external supplier stock feeds into the master `StockLevel` table and logging the process (`warehouse/`).

#### Key units
* Library Core: `author`, `book`, `member`, `loan`, `loan_audit`, `fn_checkout_book`, `sp_return_book`, `overdue_loans` (View).
* E-commerce: `product`, `customer_order`, `order_item`, `add_order_item` (Stored Procedure).
* Warehouse Sync: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `usp_SyncStock` (T-SQL Procedure).

#### Data flow
Library data flows from seeding into core tables, modified by transactional routines (`fn_checkout_book`, `sp_return_book`) which are audited in `loan_audit`. E-commerce data flows from input parameters through `add_order_item` to atomically update `product` stock and `customer_order` totals. Warehouse data flows from the source `dbo.SupplierFeed` into `dbo.StockLevel` via `usp_SyncStock`, with results logged in `dbo.SyncLog`.

#### Dependencies
* `library/` depends on PostgreSQL features (functions, materialized views) and relies on schema definitions from `01_schema.sql`.
* `shop/` depends on MySQL schema definitions and relies on the integrity of `product`, `customer_order`, and `order_item` tables.
* `warehouse/` depends on the structure of `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
* Transactional Integrity: Enforced via stored procedures (`add_order_item`) and database routines (`fn_checkout_book`, `sp_return_book`) to ensure atomic state changes across related entities.
* Schema Management: Utilizes sequential SQL scripts for defining, migrating, and reporting on data structures across the different domains.
* Auditing: Implemented via event-driven triggers (`trg_loan_audit_fn`) in the library module and explicit logging procedures (`usp_SyncStock` writing to `dbo.SyncLog`) in the warehouse module.
