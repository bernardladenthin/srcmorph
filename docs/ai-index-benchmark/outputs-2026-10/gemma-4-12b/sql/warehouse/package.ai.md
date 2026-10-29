### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T04:43:24Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> This package manages the automated synchronization of warehouse inventory levels by merging supplier feed data into a master stock table.

#### Purpose
* Synchronizes `StockLevel` inventory counts from incoming `SupplierFeed` data.
* Maintains an audit trail of synchronization metrics and errors.

#### Responsibilities
* Data Aggregation: Summarizing `SupplierFeed` records by `WarehouseId` and `Sku`.
* Inventory Management: Executing `MERGE` operations to update or insert `StockLevel` records.
* Logging and Metrics: Tracking `RowsInserted`, `RowsUpdated`, and error messages in `SyncLog`.
* Transactional Integrity: Ensuring atomic updates with rollback mechanisms and error capturing.

#### Key units
* `dbo.usp_SyncStock`: Procedure that aggregates feed data and performs the merge into the master inventory.
* `dbo.StockLevel`: Master table storing current `OnHand` quantities per warehouse and SKU.
* `dbo.SupplierFeed`: Staging table for incoming quantity data and timestamps.
* `dbo.SyncLog`: Audit table for recording execution statistics and failure messages.

#### Data flow
`SupplierFeed` records are filtered by a `@Since` timestamp, aggregated by SKU/Warehouse, and merged into `StockLevel`. The `OUTPUT` clause captures the results of the merge to populate `SyncLog`.

#### Dependencies
* `dbo.StockLevel` (Target table)
* `dbo.SupplierFeed` (Source data)
* `dbo.SyncLog` (Audit destination)
