### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:46:13Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse inventory levels by aggregating supplier feed data into a master stock table with execution logging.

#### Purpose
* Maintain current inventory levels in `dbo.StockLevel` based on incremental updates from `dbo.SupplierFeed`.
* Track synchronization performance and errors in `dbo.SyncLog`.

#### Responsibilities
* Stock Synchronization: Aggregating supplier feed quantities and merging them into master stock records.
* Execution Logging: Recording row counts and error messages for each synchronization run.

#### Key units
* `dbo.StockLevel`: Master table storing `OnHand` quantities per `WarehouseId` and `Sku`.
* `dbo.SupplierFeed`: Source table containing incremental stock updates.
* `dbo.SyncLog`: Audit table tracking sync start times, row changes, and error messages.
* `dbo.usp_SyncStock`: Stored procedure that orchestrates the aggregation, merge, and logging process.

#### Data flow
* `dbo.SupplierFeed` (filtered by `@Since`) $\rightarrow$ Aggregated Quantities $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ Change counts $\rightarrow$ `dbo.SyncLog`.

#### Dependencies
* Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.

#### Cross-cutting
* Transactional integrity: Operations wrapped in transactions with rollback and `ERROR_MESSAGE()` logging on failure.
* Incremental processing: Use of `@Since` parameter to filter source data.
