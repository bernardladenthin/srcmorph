### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:00:40Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse stock levels by aggregating supplier feed data and logging operation results.

#### Purpose
* Update `StockLevel` records based on incoming `SupplierFeed` data.
* Maintain an audit trail of synchronization performance and errors.

#### Responsibilities
* **Stock Synchronization**: Aggregates quantities from supplier feeds and merges them into the master stock table.
* **Operational Logging**: Tracks the number of rows inserted, updated, and any execution errors per sync run.

#### Key units
* `dbo.usp_SyncStock`: Procedure that filters feed records by timestamp, aggregates quantities, and performs the `MERGE` into stock levels.
* `dbo.StockLevel`: Table storing current on-hand quantities per warehouse and SKU.
* `dbo.SupplierFeed`: Table acting as the landing zone for incoming stock data.
* `dbo.SyncLog`: Table recording the outcome and metrics of each synchronization process.

#### Data flow
`dbo.SupplierFeed` (filtered by `@Since`) $\rightarrow$ Aggregation by `WarehouseId`/`Sku` $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ Metrics/Errors written to `dbo.SyncLog`.

#### Dependencies
* Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.

#### Cross-cutting
* **Transaction Management**: Uses `TRY...CATCH` blocks and explicit transactions to ensure atomicity and error logging.
* **Concurrency/Updates**: Employs the `MERGE` statement to handle simultaneous inserts and updates of stock levels.
