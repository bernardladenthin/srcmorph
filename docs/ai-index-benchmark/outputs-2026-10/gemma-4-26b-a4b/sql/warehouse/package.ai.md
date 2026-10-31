### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:10:50Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse inventory levels by aggregating supplier feed data and logging the results of the merge operation.

#### Purpose
* Maintains accurate, real-time stock levels per warehouse and SKU.
* Automates the processing of incremental supplier updates through a transactional merge pipeline.

#### Responsibilities
* **Inventory Management**: Tracking current `OnHand` quantities and update timestamps for specific SKUs and warehouses.
* **Data Ingestion**: Aggregating raw quantities from supplier feeds based on temporal thresholds.
* **Audit & Logging**: Recording synchronization history, including row counts and error states.

#### Key units
* `dbo.StockLevel`: Stores the current state of inventory per `WarehouseId` and `Sku`.
* `dbo.SupplierFeed`: Contains the raw incoming quantity data with `ReceivedAt` timestamps.
* `dbo.SyncLog`: Maintains a history of synchronization attempts, including `RowsInserted`, `RowsUpdated`, and `ErrorMessage`.
* `dbo.usp_SyncStock`: The central stored procedure that aggregates feeds, executes the `MERGE` logic, and manages transactions.

#### Data flow
1. **Input**: The `usp_SyncStock` procedure receives a `@Since` timestamp parameter.
2. **Aggregation**: Data is read from `dbo.SupplierFeed` where `ReceivedAt` is greater than or equal to the parameter, grouped by `WarehouseId` and `Sku`.
3. **Merge**: The aggregated quantities are merged into `dbo.StockLevel`, either incrementing existing `OnHand` values or inserting new records.
4. **Output**: The procedure captures the count of affected rows via an `OUTPUT` clause and writes a summary record to `dbo.SyncLog`.

#### Dependencies
* **Tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
