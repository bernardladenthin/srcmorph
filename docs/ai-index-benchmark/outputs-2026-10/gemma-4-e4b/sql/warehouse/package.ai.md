### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:00:59Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> This package manages the nightly synchronization of inventory levels from supplier feeds into the master stock records within the warehouse database.

#### Purpose
* Executes nightly stock synchronization from supplier feed data into the StockLevel table.
* Logs the results (inserts/updates) and any errors of the synchronization process in SyncLog.

#### Responsibilities
* Stock Level Management: Merging and updating `dbo.StockLevel` based on incoming supplier data.
* Auditing and Logging: Recording the outcome of the synchronization process in `dbo.SyncLog`.

#### Key units
* `usp_SyncStock`: T-SQL procedure responsible for executing the stock synchronization logic.
* `dbo.StockLevel`: Master table holding current inventory levels per warehouse and SKU.
* `dbo.SupplierFeed`: Staging table containing raw, recent data from supplier feeds.
* `dbo.SyncLog`: Audit table tracking the execution details and success/failure of synchronization runs.

#### Data flow
Input data flows from `dbo.SupplierFeed` (filtered by time) into the `usp_SyncStock` procedure, which then uses a MERGE operation to update or insert records into `dbo.StockLevel`. Finally, the procedure writes summary metrics to `dbo.SyncLog`.

#### Dependencies
* Relies on `dbo.SupplierFeed` for source data.
* Modifies `dbo.StockLevel` as the primary target for inventory updates.
* Writes operational metadata to `dbo.SyncLog`.

#### Cross-cutting
* Transaction management is used within `usp_SyncStock` to ensure atomicity of the merge and logging operations.
* Standardized logging of success/failure metrics is implemented via `dbo.SyncLog`.
