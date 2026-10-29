### ai
- H: 1.0
- C: F336108D
- D: 2026-10-28T23:55:11Z
- T: 2026-10-29T00:01:12Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> This package suite provides specialized persistence layers for managing library lending, e-commerce order fulfillment, and automated warehouse inventory synchronization.

#### Purpose
* To provide structured, transactional data management for three distinct business domains: library lending, e-commerce sales, and supply chain inventory.
* To define and execute the necessary database logic (schemas, procedures, routines) to support core business workflows in each domain.

#### Responsibilities
* Library Lending Management: Defining core entities and managing lending workflows (checkout, return, reporting).
* E-commerce Order Processing: Managing product inventory and ensuring atomic stock reservation during order creation.
* Warehouse Inventory Synchronization: Executing nightly batch jobs to merge raw supplier feed data into master stock records.

#### Key units
* Library: `fn_checkout_book`, `sp_return_book`, `overdue_loans` (View), `loan_audit` (Table).
* Shop: `add_order_item` (Procedure), `product` (Entity), `customer_order` (Entity).
* Warehouse: `usp_SyncStock` (Procedure), `dbo.StockLevel` (Table), `dbo.SupplierFeed` (Staging Table).

#### Data flow
* Library: Seeding $\rightarrow$ Core Schema $\rightarrow$ Transactional Routines $\rightarrow$ Audit Logging $\rightarrow$ Reporting Views.
* Shop: Input (SKU, Quantity) $\rightarrow$ `add_order_item` $\rightarrow$ Stock Check/Update $\rightarrow$ Order/Item Persistence.
* Warehouse: `dbo.SupplierFeed` (Input) $\rightarrow$ `usp_SyncStock` (Merge) $\rightarrow$ `dbo.StockLevel` (Output) $\rightarrow$ `dbo.SyncLog` (Audit).

#### Dependencies
* Library: Depends on PostgreSQL features and its own defined schema (`01_schema.sql`).
* Shop: Depends on MySQL schema definitions for its entities.
* Warehouse: Depends on `dbo.SupplierFeed` for source data and targets `dbo.StockLevel` for updates.

#### Cross-cutting
* Transactional Integrity: Enforced via database constraints, triggers (Library), and explicit locking within stored procedures (Shop, Warehouse).
* Schema Management: Utilizes sequential SQL scripts for definition, migration, and seeding in the Library domain.
* Auditing/Logging: Implemented via `loan_audit` (Library) and `dbo.SyncLog` (Warehouse) to track state changes and process outcomes.
