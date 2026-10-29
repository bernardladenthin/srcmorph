### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:36:14Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock levels by aggregating and merging data from a supplier feed into a master stock table with execution logging.

#### Purpose
* Maintain current inventory levels in `dbo.StockLevel` based on incremental updates from `dbo.SupplierFeed`.
* Track synchronization performance and errors in `dbo.SyncLog`.

#### Type
T-SQL (SQL Server) tables and stored procedure.

#### Schema
**dbo.StockLevel**
* `WarehouseId` INT NOT NULL (PK)
* `Sku` NVARCHAR(40) NOT NULL (PK)
* `OnHand` INT NOT NULL DEFAULT (0)
* `UpdatedAt` DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())

**dbo.SupplierFeed**
* `FeedRowId` BIGINT IDENTITY(1, 1) NOT NULL (PK)
* `WarehouseId` INT NOT NULL
* `Sku` NVARCHAR(40) NOT NULL
* `Quantity` INT NOT NULL
* `ReceivedAt` DATETIME2(0) NOT NULL

**dbo.SyncLog**
* `SyncLogId` INT IDENTITY(1, 1) NOT NULL (PK)
* `StartedAt` DATETIME2(0) NOT NULL
* `RowsInserted` INT NOT NULL
* `RowsUpdated` INT NOT NULL
* `ErrorMessage` NVARCHAR(4000) NULL

#### Reads
* `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`

#### Writes
* `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`
* `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`

#### Relationships
* `dbo.SupplierFeed.WarehouseId` -> `dbo.StockLevel.WarehouseId`
* `dbo.SupplierFeed.Sku` -> `dbo.StockLevel.Sku`

#### Routine logic
**dbo.usp_SyncStock**
* Parameters: `@Since` (DATETIME2(0))
* Logic:
    * Aggregates `Quantity` from `dbo.SupplierFeed` where `ReceivedAt` is greater than or equal to `@Since`, grouped by `WarehouseId` and `Sku`.
    * Performs a `MERGE` into `dbo.StockLevel`:
        * If `WarehouseId` and `Sku` match: Increments `OnHand` and updates `UpdatedAt`.
        * If no match: Inserts new `WarehouseId`, `Sku`, and `OnHand` values.
    * Captures `MERGE` actions into a table variable `@Changes`.
    * Calculates total `INSERT` and `UPDATE` counts from `@Changes` and writes them to `dbo.SyncLog`.
    * Wraps operation in a transaction; on failure, rolls back and logs the `ERROR_MESSAGE()` to `dbo.SyncLog`.

#### Dependencies
* `dbo.StockLevel`
* `dbo.SupplierFeed`
* `dbo.SyncLog`
