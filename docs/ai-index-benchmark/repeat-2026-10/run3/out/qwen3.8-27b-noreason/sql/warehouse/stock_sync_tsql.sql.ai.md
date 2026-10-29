### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:48:40Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronisation that merges supplier feed quantities into on-hand stock levels and logs the sync outcome.

#### Purpose
- Models per-warehouse, per-SKU on-hand stock (`StockLevel`) and the raw supplier quantity feed (`SupplierFeed`).
- Provides `usp_SyncStock` to merge feed rows received since a cutoff into stock levels and record a `SyncLog` entry.

#### Type
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` (T-SQL / SQL Server)
- Procedure: `dbo.usp_SyncStock` (T-SQL / SQL Server)

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
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` columns `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId`, `Sku`).
- `dbo.usp_SyncStock` reads `dbo.StockLevel` columns `WarehouseId`, `Sku`, `OnHand` as the MERGE target.

#### Writes
- `dbo.usp_SyncStock` MERGEs into `dbo.StockLevel`:
  - UPDATE sets `OnHand`, `UpdatedAt`
  - INSERT sets `WarehouseId`, `Sku`, `OnHand`
- `dbo.usp_SyncStock` INSERTs into `dbo.SyncLog` columns `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.

#### Relationships
- `dbo.StockLevel.WarehouseId` + `dbo.StockLevel.Sku` ↔ `dbo.SupplierFeed.WarehouseId` + `dbo.SupplierFeed.Sku` (MERGE join key; no FK constraint declared)
- `dbo.SyncLog` is a standalone audit log (no FK to `StockLevel` or `SupplierFeed`)

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Parameter: `@Since` — lower bound on `SupplierFeed.ReceivedAt`
  - Opens a transaction; declares table variable `@Changes` to capture MERGE `$action`
  - MERGE `dbo.StockLevel` using aggregated `SupplierFeed` rows (`SUM(Quantity)` per `WarehouseId`, `Sku` where `ReceivedAt >= @Since`)
    - MATCHED → `OnHand = OnHand + Quantity`, `UpdatedAt = SYSUTCDATETIME()`
    - NOT MATCHED BY TARGET → INSERT new `StockLevel` row
  - INSERTs a `SyncLog` row with `StartedAt`, counts of INSERT vs UPDATE actions
  - On error: rollback, INSERT `SyncLog` with `ErrorMessage = ERROR_MESSAGE()`, then `THROW`

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- Built-in: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`
