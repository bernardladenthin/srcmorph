### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:58:49Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronisation that merges supplier feed quantities into per-warehouse SKU stock levels and logs the outcome.

#### Purpose
- Models warehouse stock levels per SKU and the raw supplier feed rows that drive nightly stock updates.
- Provides a stored procedure to merge feed quantities into stock levels and record sync statistics or errors.

#### Type
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock`
- Dialect: T-SQL (SQL Server)

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)` — constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — constraint `DF_StockLevel_UpdatedAt`
  - PK `PK_StockLevel` on (`WarehouseId`, `Sku`)
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — PK `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — PK `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.usp_SyncStock` reads:
  - `dbo.SupplierFeed.WarehouseId`, `dbo.SupplierFeed.Sku`, `dbo.SupplierFeed.Quantity`, `dbo.SupplierFeed.ReceivedAt` (filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId`, `Sku`)
  - `dbo.StockLevel.WarehouseId`, `dbo.StockLevel.Sku`, `dbo.StockLevel.OnHand` (MERGE target)

#### Writes
- `dbo.usp_SyncStock` writes:
  - `dbo.StockLevel.OnHand`, `dbo.StockLevel.UpdatedAt` (UPDATE on match)
  - `dbo.StockLevel.WarehouseId`, `dbo.StockLevel.Sku`, `dbo.StockLevel.OnHand` (INSERT when not matched)
  - `dbo.SyncLog.StartedAt`, `dbo.SyncLog.RowsInserted`, `dbo.SyncLog.RowsUpdated` (success path)
  - `dbo.SyncLog.StartedAt`, `dbo.SyncLog.RowsInserted`, `dbo.SyncLog.RowsUpdated`, `dbo.SyncLog.ErrorMessage` (error path)

#### Relationships
- `dbo.StockLevel.WarehouseId` + `dbo.StockLevel.Sku` ↔ `dbo.SupplierFeed.WarehouseId` + `dbo.SupplierFeed.Sku` (MERGE join key)
- No explicit FK constraints defined; logical relationship between `StockLevel` and `SupplierFeed` on (`WarehouseId`, `Sku`)

#### Routine logic
- `dbo.usp_SyncStock`
  - Parameter: `@Since DATETIME2(0)` — lower bound for `SupplierFeed.ReceivedAt`
  - Steps:
    - Capture `@Started = SYSUTCDATETIME()`
    - Open transaction
    - MERGE `dbo.StockLevel` using aggregated `dbo.SupplierFeed` rows (`SUM(Quantity)` grouped by `WarehouseId`, `Sku`) where `ReceivedAt >= @Since`
    - MATCHED → `OnHand = OnHand + Quantity`, `UpdatedAt = SYSUTCDATETIME()`
    - NOT MATCHED BY TARGET → INSERT new `StockLevel` row with `OnHand = Quantity`
    - Capture `$action` into table variable `@Changes`
    - INSERT summary counts into `dbo.SyncLog`
    - COMMIT
  - Error handling:
    - ROLLBACK if transaction open
    - INSERT error row into `dbo.SyncLog` with `ERROR_MESSAGE()`
    - `THROW` to propagate

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- Built-in: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`
