### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:23:52Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronisation applying supplier feed quantities to stock levels with audit logging.

#### Purpose
- Synchronise `StockLevel.OnHand` from aggregated `SupplierFeed.Quantity` received since cutoff
- Audit each run in `SyncLog` with inserted / updated counts and error capture

#### Type
- table / procedure, T-SQL dialect (SQL Server, `GO`, `IDENTITY`, `MERGE`, `SYSUTCDATETIME`)
- objects: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `dbo.usp_SyncStock`

#### Schema
- `dbo.StockLevel`: `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `OnHand INT NOT NULL DEFAULT 0`, `UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()`, PK `PK_StockLevel (WarehouseId, Sku)`
- `dbo.SupplierFeed`: `FeedRowId BIGINT IDENTITY(1,1) NOT NULL PK`, `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `Quantity INT NOT NULL`, `ReceivedAt DATETIME2(0) NOT NULL`, PK `PK_SupplierFeed (FeedRowId)`
- `dbo.SyncLog`: `SyncLogId INT IDENTITY(1,1) NOT NULL PK`, `StartedAt DATETIME2(0) NOT NULL`, `RowsInserted INT NOT NULL`, `RowsUpdated INT NOT NULL`, `ErrorMessage NVARCHAR(4000) NULL`, PK `PK_SyncLog (SyncLogId)`
- defaults: `DF_StockLevel_OnHand`, `DF_StockLevel_UpdatedAt`

#### Reads
- `dbo.SupplierFeed.WarehouseId, Sku, Quantity, ReceivedAt` filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku` with `SUM(Quantity)`
- `dbo.StockLevel.WarehouseId, Sku, OnHand` as MERGE target for match detection
- `@Changes.MergeAction` with `INSERT` / `UPDATE` counts for log insert
- inputs: `@Since DATETIME2(0)`, `@Started DATETIME2(0)`, `SYSUTCDATETIME()`

#### Writes
- `dbo.StockLevel.WarehouseId, Sku, OnHand` via `MERGE ... WHEN NOT MATCHED BY TARGET INSERT`
- `dbo.StockLevel.OnHand, UpdatedAt` via `MERGE ... WHEN MATCHED UPDATE` with `OnHand + Quantity`, `SYSUTCDATETIME()`
- `dbo.SyncLog.StartedAt, RowsInserted, RowsUpdated` on success, plus `ErrorMessage` on catch via `ERROR_MESSAGE()`

#### Relationships
- `StockLevel.WarehouseId -> SupplierFeed.WarehouseId` via `MERGE ON`
- `StockLevel.Sku -> SupplierFeed.Sku` via `MERGE ON`
- no declared FK constraints, join-equivalent on `(WarehouseId, Sku)`

#### Routine logic
- params: `@Since DATETIME2(0)` cutoff for `SupplierFeed.ReceivedAt`
- captures `@Started = SYSUTCDATETIME()`, table var `@Changes(MergeAction NVARCHAR(10))`
- `BEGIN TRANSACTION` + `MERGE` aggregated feed into `StockLevel`, `OUTPUT $action INTO @Changes`
- `INSERT INTO dbo.SyncLog` with `SUM(CASE MergeAction)` for `RowsInserted`, `RowsUpdated`, then `COMMIT`
- `CATCH`: `ROLLBACK` if `@@TRANCOUNT > 0`, log `0,0,ERROR_MESSAGE()` to `SyncLog`, `THROW`

#### Dependencies
- tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- system: `SYSUTCDATETIME()`, `$action`, `ERROR_MESSAGE()`, `@@TRANCOUNT`
