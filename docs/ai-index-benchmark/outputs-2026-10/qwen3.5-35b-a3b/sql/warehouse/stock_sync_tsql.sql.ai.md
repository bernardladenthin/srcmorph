### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:36:41Z
- G: 
- A: 0.0.0
- X: file
---
> Warehouse inventory levels are synchronized from supplier feed data with transaction logging and error handling.

#### Purpose
*   Maintains `dbo.StockLevel` inventory state based on `dbo.SupplierFeed` incoming data.
*   Records synchronization metrics and failures in `dbo.SyncLog`.

#### Type
*   Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
*   Procedure: `dbo.usp_SyncStock`.
*   Dialect: T-SQL (SQL Server).

#### Schema
*   `dbo.StockLevel`: `WarehouseId` INT NOT NULL, `Sku` NVARCHAR(40) NOT NULL, `OnHand` INT NOT NULL DEFAULT 0, `UpdatedAt` DATETIME2(0) DEFAULT SYSUTCDATETIME(). PK `PK_StockLevel` (`WarehouseId`, `Sku`).
*   `dbo.SupplierFeed`: `FeedRowId` BIGINT IDENTITY NOT NULL PK `PK_SupplierFeed`, `WarehouseId` INT NOT NULL, `Sku` NVARCHAR(40) NOT NULL, `Quantity` INT NOT NULL, `ReceivedAt` DATETIME2(0) NOT NULL.
*   `dbo.SyncLog`: `SyncLogId` INT IDENTITY NOT NULL PK `PK_SyncLog`, `StartedAt` DATETIME2(0) NOT NULL, `RowsInserted` INT NOT NULL, `RowsUpdated` INT NOT NULL, `ErrorMessage` NVARCHAR(4000) NULL.

#### Reads
*   `dbo.SupplierFeed` (columns `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`).
*   `dbo.StockLevel` (columns `WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`).

#### Writes
*   `dbo.StockLevel` (UPDATE `OnHand`, `UpdatedAt`; INSERT `WarehouseId`, `Sku`, `OnHand`).
*   `dbo.SyncLog` (INSERT `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`).

#### Relationships
*   `dbo.StockLevel.WarehouseId` matches `dbo.SupplierFeed.WarehouseId`.
*   `dbo.StockLevel.Sku` matches `dbo.SupplierFeed.Sku`.

#### Routine logic
*   Procedure `dbo.usp_SyncStock` accepts parameter `@Since` DATETIME2(0).
*   Aggregates `dbo.SupplierFeed` by `WarehouseId`, `Sku` where `ReceivedAt >= @Since`.
*   MERGE `dbo.StockLevel` target updates `OnHand` or inserts new rows based on feed.
*   Outputs merge actions to temp table `@Changes` to count inserts/updates.
*   Inserts summary into `dbo.SyncLog` on success.
*   Catches errors, rolls back transaction, inserts error details into `dbo.SyncLog`.

#### Dependencies
*   Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
*   Functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
