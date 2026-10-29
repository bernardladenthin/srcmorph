### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T04:44:34Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> This package provides a comprehensive suite of relational database schemas and business logic for managing multi-branch library systems, retail e-commerce inventory, and warehouse stock synchronization.

#### Purpose
* Provides structured data models and transactional logic for diverse inventory and asset management domains (Library, Shop, Warehouse).
* Ensures data integrity through automated triggers, stored procedures, and atomic merge operations.

#### Responsibilities
* **Library Management**: Handles multi-branch book inventory, member accounts, loan lifecycles, and automated late fee calculations.
* **Retail Order Processing**: Manages product catalogs, customer orders, and atomic stock deduction during order fulfillment.
* **Warehouse Synchronization**: Automates the merging of supplier feed data into master stock levels with comprehensive audit logging.
* **Reporting & Analytics**: Provides views for operational insights such as overdue loans, member statistics, and popular book trends.

#### Key units
* `author`, `book`, `member`, `loan`: Core entities for library assets and user records.
* `branch`, `book_copy`: Entities for multi-location inventory tracking.
* `product`, `customer_order`, `order_item`: Core schema for retail catalog and transactional orders.
* `StockLevel`, `SupplierFeed`, `SyncLog`: Tables for warehouse inventory, staging data, and sync metrics.
* `fn_checkout_book`, `sp_return_book`, `fn_late_fee`: Procedures and functions for library loan logic.
* `add_order_item`: Procedure for validating stock and updating order totals.
* `usp_SyncStock`: Procedure for aggregating supplier feeds and merging into master stock.

#### Data flow
* **Library Pipeline**: `fn_checkout_book` validates members and availability $\rightarrow$ creates `loan` $\rightarrow$ triggers `loan_audit`.
* **Shop Pipeline**: `add_order_item` validates `product` stock $\rightarrow$ inserts `order_item` $\rightarrow$ decrements `product` stock $\rightarrow$ recalculates `customer_order` totals.
* **Warehouse Pipeline**: `SupplierFeed` (filtered by timestamp) $\rightarrow$ Aggregation by SKU/Warehouse $\rightarrow$ `MERGE` into `StockLevel` $\rightarrow$ `SyncLog` population.

#### Dependencies
* **Internal**: Cross-references between `loan`/`book`/`member` and `book_copy`/`branch`.
* **External**: PostgreSQL (Library/Shop modules) and SQL Server (Warehouse module).

#### Cross-cutting
* **Audit Trails**: Automated logging of loan state changes (`loan_audit`) and warehouse sync metrics (`SyncLog`).
* **Transactional Integrity**: Use of stored procedures to ensure atomic updates for stock levels and order totals.
* **Validation**: Strict constraints on inventory counts (e.g., `copies_free` vs `copies_total`) and stock availability.
