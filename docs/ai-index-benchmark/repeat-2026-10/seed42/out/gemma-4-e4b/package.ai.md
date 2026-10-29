### ai
- H: 1.0
- C: C91EB333
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:54:25Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package suite provides specialized persistence layers for library management, e-commerce order processing, and automated inventory synchronization.

#### Purpose
* To manage the core data models and transactional logic for lending libraries (`library/`).
* To handle atomic order creation and inventory reservation for e-commerce (`shop/`) and synchronize external stock data (`warehouse/`).

#### Responsibilities
* **Library Data Modeling and Operations:** Defining schemas for books, members, and loans, and executing checkout/return routines (`library/`).
* **E-commerce Transaction Management:** Defining product/order schemas and ensuring atomic stock deduction during order placement (`shop/`).
* **Inventory Synchronization:** Merging external supplier stock feeds into the master `StockLevel` table and logging the process (`warehouse/`).

#### Key units
* `author`, `book`, `member`, `loan`, `loan_audit`: Core entities and audit logs for the library system.
* `fn_checkout_book`, `sp_return_book`: Transactional functions managing library loan state changes.
* `product`, `customer_order`, `order_item`: Schemas defining e-commerce entities.
* `add_order_item`: Stored procedure ensuring atomic order item creation and stock deduction.
* `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`: Tables managing master inventory and synchronization logs.
* `usp_SyncStock`: T-SQL procedure handling the nightly synchronization of supplier stock.

#### Data flow
Library data flows through transactional routines like `fn_checkout_book` and `sp_return_book`, which are recorded in `loan_audit`. E-commerce data moves through `add_order_item` to atomically update `product` and `customer_order` records. Warehouse data is ingested from `dbo.SupplierFeed` into `dbo.StockLevel` via `usp_SyncStock`, with results logged in `dbo.SyncLog`.

#### Dependencies
* **Library:** Depends on PostgreSQL features and schema definitions from `01_schema.sql`.
* **Shop:** Depends on MySQL schema definitions for `product`, `customer_order`, and `order_item`.
* **Warehouse:** Depends on the structure of `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
* **Transactional Integrity:** Enforced via stored procedures and database routines to guarantee atomic state changes across related entities.
* **Auditing:** Implemented via triggers (`trg_loan_audit_fn`) in the library module and explicit logging procedures in the warehouse module.
* **Schema Management:** Utilizes sequential SQL scripts for defining and migrating data structures.
