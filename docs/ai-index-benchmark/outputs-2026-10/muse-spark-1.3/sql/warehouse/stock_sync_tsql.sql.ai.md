### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:16:54Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronization merging supplier feed quantities into on-hand inventory levels with sync audit logging.
#### Purpose
- Synchronize `StockLevel` on-hand inventory from `SupplierFeed` quantities received since cutoff
- Audit each sync run outcome in `SyncLog` with inserted/updated counts or error
#### Type
- table, procedure, T-SQL dialect (SQL Server, `GO`, `IDENTITY`, `MERGE`, `SYSUTCDATETIME`, `TRY/CATCH`)
#### Schema
- `dbo.StockLevel`: `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `OnHand INT NOT NULL DEFAULT (0)`, `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`, `PK_StockLevel PRIMARY KEY (WarehouseId, Sku)`
- `dbo.SupplierFeed`: `FeedRowId BIGINT IDENTITY(1,1) NOT NULL PK`, `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `Quantity INT NOT NULL`, `ReceivedAt DATETIME2(0) NOT NULL`, `PK_SupplierFeed PRIMARY KEY (FeedRowId)`
- `dbo.SyncLog`: `SyncLogId INT IDENTITY(1,1) NOT NULL PK`, `StartedAt DATETIME2(0) NOT NULL`, `RowsInserted INT NOT NULL`, `RowsUpdated INT NOT NULL`, `ErrorMessage NVARCHAR(4000) NULL`, `PK_SyncLog PRIMARY KEY (SyncLogId)`
#### Reads
- `dbo.SupplierFeed.WarehouseId, Sku, Quantity, ReceivedAt` filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku` with `SUM(Quantity)`
- `dbo.StockLevel.WarehouseId, Sku, OnHand` as `MERGE` target for match detection
- `@Changes.MergeAction` with `$action` output for `INSERT`/`UPDATE` counts
#### Writes
- `dbo.StockLevel.WarehouseId, Sku, OnHand` via `MERGE INSERT` for not-matched SKUs
- `dbo.StockLevel.OnHand, UpdatedAt` via `MERGE UPDATE` (`OnHand + Quantity`, `SYSUTCDATETIME()`)
- `dbo.SyncLog.StartedAt, RowsInserted, RowsUpdated` on success, plus `ErrorMessage` on catch with `0, 0` counts
#### Relationships
- `SupplierFeed.WarehouseId -> StockLevel.WarehouseId` via `MERGE ON`
- `SupplierFeed.Sku -> StockLevel.Sku` via `MERGE ON`
#### Routine logic
- `dbo.usp_SyncStock @Since DATETIME2(0)`: merges feed since cutoff into stock, logs result
- Captures `@Started = SYSUTCDATETIME()`, collects `$action` into `@Changes TABLE (MergeAction NVARCHAR(10))`
- Transactional `MERGE`: `UPDATE SET OnHand + Quantity`, `INSERT (WarehouseId, Sku, OnHand)`; then `INSERT SyncLog` with `SUM(CASE MergeAction)` counts; `COMMIT`
- `CATCH`: `ROLLBACK` if `@@TRANCOUNT > 0`, `INSERT SyncLog` with `ERROR_MESSAGE()`, `THROW`
#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- `SYSUTCDATETIME`, `ERROR_MESSAGE`, `$action`
