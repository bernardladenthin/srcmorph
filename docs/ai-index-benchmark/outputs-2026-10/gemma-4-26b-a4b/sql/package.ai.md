### sql
- H: 1.0
- C: 48611908
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:11:26Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A multi-domain relational database system managing library circulation, retail e-commerce transactions, and warehouse inventory synchronization.

#### Purpose
* Provides specialized schemas for library inventory/membership, web shop order fulfillment, and automated warehouse stock synchronization.

#### Responsibilities
* **Library Management**: Tracks authors, books, physical copies, and members across multiple branches, including automated loan lifecycles and late fee calculations.
* **E-commerce Operations**: Manages product catalogs, real-time stock levels, and the atomic lifecycle of customer orders and line items.
* **Warehouse Synchronization**: Automates the ingestion of supplier feeds to update warehouse stock levels via transactional merge pipelines.
* **Audit & Reporting**: Maintains historical logs for loan actions, synchronization attempts, and provides views for overdue tracking and product demand.

#### Key units
* `author`, `book`, `book_copy`: Hierarchical library entities from biographical data to specific physical items.
* `branch`, `member`, `loan`: Library operational units managing locations, users, and transaction history.
* `fn_checkout_book`, `sp_return_book`: Library routines for managing book availability and loan status.
* `product`: Retail catalog entity containing `sku`, `price_cents`, and `stock`.
* `customer_order`, `order_item`: E-commerce entities for tracking order metadata and individual line items.
* `add_order_item`: Procedure for atomic order updates, stock decrementing, and total recalculation.
* `dbo.StockLevel`: Warehouse entity tracking `OnHand` quantities per `WarehouseId` and `Sku`.
* `dbo.SupplierFeed`: Raw incoming inventory data used for warehouse updates.
* `dbo.usp_SyncStock`: Central warehouse procedure for aggregating feeds and executing `MERGE` logic.
* `dbo.SyncLog`: Audit trail for warehouse synchronization results and errors.

#### Data flow
* **Library Pipeline**: `fn_checkout_book` validates `member` and `book_copy` status to create `loan` records, which are subsequently captured by `loan_audit` triggers.
* **Retail Pipeline**: Adding an `order_item` triggers a recalculation of `customer_order.total_cents` and a decrement of `product.stock`.
* **Warehouse Pipeline**: `usp_SyncStock` aggregates `SupplierFeed` data based on a timestamp, merges quantities into `StockLevel`, and logs the outcome in `SyncLog`.

#### Dependencies
* **Internal**: Relies on strict relational hierarchies (e.g., `author` $\rightarrow$ `book` $\rightarrow$ `book_copy`; `customer_order` $\rightarrow$ `order_item` $\rightarrow$ `product`).
* **External**: Utilizes PostgreSQL-specific features (`PL/pgSQL`, materialized views, `BIGSERIAL`) and T-SQL `MERGE` operations.

#### Cross-cutting
* **Automated Auditing**: Implemented via triggers in the library (`loan_audit`) and explicit logging procedures in the warehouse (`SyncLog`).
* **Inventory Integrity**: Enforced through status constraints (`AVAILABLE`, `ON_LOAN`), stock validation during order creation, and transactional merge logic.
* **Reporting Views**: Uses materialized and standard views (`overdue_loans`, `popular_books`) to aggregate transactional data for business intelligence.
