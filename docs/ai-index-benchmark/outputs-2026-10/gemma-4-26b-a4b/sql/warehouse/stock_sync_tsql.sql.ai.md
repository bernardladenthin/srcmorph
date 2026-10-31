### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:56:06Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse inventory levels by aggregating supplier feed data and logging the results of the merge operation.

#### Purpose
* Maintains current stock levels per warehouse and SKU.
* Processes incremental supplier feed updates via a merge procedure.
* Tracks synchronization history and errors.

#### Type
T-SQL (SQL Server)

#### Schema
#### dbo.StockLevel
* `WarehouseId` INT NOT NULL (PK)
* `Sku` NVARCHAR(40) NOT NULL (PK)
* `OnHand` INT NOT NULL DEFAULT (0)
* `UpdatedAt` DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())

#### dbo.SupplierFeed
* `FeedRowId` BIGINT IDENTITY(1, 1) NOT NULL (PK)
* `WarehouseId` INT NOT NULL
* `Sku` NVARCHAR(40) NOT NULL
* `Quantity` INT NOT NULL
* `ReceivedAt` DATETIME2(0) NOT NULL

#### dbo.SyncLog
* `SyncLogId` INT IDENTITY(1, 1) NOT NULL (PK)
* `StartedAt` DATETIME2(0) NOT NULL
* `RowsInserted` INT NOT NULL
* `RowsUpdated` INT NOT NULL
* `ErrorMessage` NVARCHAR(4000) NULL

#### Reads
#### dbo.usp_SyncStock
* `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
* `@Since` (Parameter)

#### Writes
#### dbo.usp_SyncStock
* `dbo.StockLevel`: `OnHand`, `UpdatedAt` (via MERGE)
* `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`

#### Relationships
* `dbo.StockLevel.WarehouseId` -> `dbo.SupplierFeed.WarehouseId`
* `dbo.StockLevel.Sku` -> `dbo.SupplierFeed.Sku`

#### Routine logic
#### dbo.usp_SyncStock
* Parameters: `@Since DATETIME2(0)`
* Aggregates `Quantity` from `dbo.SupplierFeed` grouped by `WarehouseId` and `Sku` where `ReceivedAt >= @Since`.
* Performs a `MERGE` into `dbo.StockLevel`:
    * If matched: Increments `OnHand` by the aggregated quantity and updates `UpdatedAt`.
    * If not matched: Inserts a new record with the aggregated quantity.
* Captures the number of 'INSERT' and 'UPDATE' actions using the `OUTPUT` clause.
* Inserts a summary record into `dbo.SyncLog`.
* Implements transaction management with rollback and error logging in the `CATCH` block.

#### Dependencies
* `dbo.StockLevel`
* `dbo.SupplierFeed`
* `dbo.SyncLog`
