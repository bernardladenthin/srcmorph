### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:08:06Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly stock synchronisation from a supplier feed into a warehouse stock level table with change logging.

#### Purpose
- Synchronises supplier feed data into `dbo.StockLevel` for warehouse inventory tracking.
- Logs sync outcomes (inserts, updates, errors) into `dbo.SyncLog`.

#### Type
Procedure and tables (T-SQL).

#### Schema
- `dbo.StockLevel`: `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`, `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`, PK `PK_StockLevel PRIMARY KEY (WarehouseId, Sku)`.
- `dbo.SupplierFeed`: `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY`, `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `Quantity INT NOT NULL`, `ReceivedAt DATETIME2(0) NOT NULL`.
- `dbo.SyncLog`: `SyncLogId INT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY`, `StartedAt DATETIME2(0) NOT NULL`, `RowsInserted INT NOT NULL`, `RowsUpdated INT NOT NULL`, `ErrorMessage NVARCHAR(4000) NULL`.

#### Reads
- `dbo.SupplierFeed` columns `WarehouseId`, `Sku`, `Quantity` filtered by `ReceivedAt >= @Since` and grouped by `WarehouseId`, `Sku`.
- Parameter `@Since DATETIME2(0)`.

#### Writes
- `dbo.StockLevel` columns `OnHand`, `UpdatedAt` via MERGE `WHEN MATCHED` UPDATE and `WHEN NOT MATCHED BY TARGET` INSERT.
- `dbo.SyncLog` columns `StartedAt`, `RowsInserted`, `RowsUpdated` (and `ErrorMessage` on failure).

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` (MERGE join condition).
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` (MERGE join condition).

#### Routine logic
- Parameter `@Since DATETIME2(0)`.
- Declares `@Started DATETIME2(0)` and table variable `@Changes` for merge actions.
- Begins transaction; MERGE `dbo.StockLevel` using aggregated `dbo.SupplierFeed` where `ReceivedAt >= @Since` grouped by `WarehouseId`, `Sku`; `WHEN MATCHED` UPDATE `OnHand = target.OnHand + source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`; `WHEN NOT MATCHED BY TARGET` INSERT `WarehouseId`, `Sku`, `OnHand` values from source; OUTPUT `$action INTO @Changes`.
- INSERT INTO `dbo.SyncLog` with `StartedAt`, `RowsInserted` = SUM(CASE WHEN `MergeAction` = 'INSERT' THEN 1 ELSE 0 END), `RowsUpdated` = SUM(CASE WHEN `MergeAction` = 'UPDATE' THEN 1 ELSE 0 END) FROM `@Changes`.
- COMMIT TRANSACTION; on success. CATCH: ROLLBACK TRANSACTION if open, INSERT INTO `dbo.SyncLog` with `@Started`, 0, 0, `ERROR_MESSAGE()`, then THROW.

#### Dependencies
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, function `SYSUTCDATETIME()`, parameter `@Since`.
