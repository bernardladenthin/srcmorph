### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:25Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> This package manages the automated synchronization of inventory stock levels from external supplier feeds into the master warehouse stock records.

#### Purpose
* Executes nightly stock synchronization from supplier feed into StockLevel.
* Logs the results (inserts/updates) of the stock synchronization process in SyncLog.

#### Responsibilities
* Stock level data ingestion and merging from `dbo.SupplierFeed` into `dbo.StockLevel`.
* Transaction management and error logging for the synchronization process via `dbo.usp_SyncStock`.

#### Key units
* `dbo.usp_SyncStock`: Procedure handling the transactional stock synchronization logic, accepting `@Since` parameter.
* `dbo.StockLevel`: Master table tracking `OnHand` stock per `WarehouseId` and `Sku`.
* `dbo.SupplierFeed`: Source table containing incoming stock data from suppliers.
* `dbo.SyncLog`: Audit table recording the start time, row counts, and errors of synchronization runs.

#### Data flow
Input data flows from `dbo.SupplierFeed` into the `dbo.usp_SyncStock` procedure, which aggregates and applies changes to `dbo.StockLevel` via a `MERGE` operation, finally logging the outcome (inserts/updates) into `dbo.SyncLog`.

#### Dependencies
* Depends on `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog` tables.
* Utilizes `SYSUTCDATETIME()` for timestamping.

#### Cross-cutting
* All operations are encapsulated within a transaction block within `dbo.usp_SyncStock`.
* Error handling includes catching exceptions, rolling back the transaction, and logging the error message into `dbo.SyncLog`.
