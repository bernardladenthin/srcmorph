### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:44:16Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly synchronization of warehouse stock levels from a supplier feed, merging received quantities into on-hand inventory per warehouse and SKU while logging run metrics and errors.

#### Purpose
- Persist current on-hand stock per warehouse/SKU (`dbo.StockLevel`), raw supplier delivery rows (`dbo.SupplierFeed`), and sync run audit records (`dbo.SyncLog`).
- `dbo.usp_SyncStock` incrementally merges supplier feed deltas since `@Since` into `StockLevel` and writes inserted/updated counts or error details to `SyncLog`.

#### Type
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` — T-SQL (SQL Server)
- Stored procedure: `dbo.usp_SyncStock` — T-SQL (SQL Server)

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` — part of composite PK
  - `Sku NVARCHAR(40) NOT NULL` — part of composite PK
  - `OnHand INT NOT NULL DEFAULT (0)` — constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — constraint `DF_StockLevel_UpdatedAt`
  - PK: `PK_StockLevel (WarehouseId, Sku)`
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
- `dbo.SupplierFeed.WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` — filtered `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku`, aggregated `SUM(Quantity)`
- `dbo.StockLevel.WarehouseId`, `Sku`, `OnHand` — MERGE target columns
- `@Changes.MergeAction` — table variable populated via MERGE `OUTPUT $action`

#### Writes
- `dbo.StockLevel` — MERGE target: `INSERT (WarehouseId, Sku, OnHand)`; `UPDATE OnHand`, `UpdatedAt`
- `dbo.SyncLog` — `INSERT (StartedAt, RowsInserted, RowsUpdated)` on success; `INSERT (StartedAt, RowsInserted, RowsUpdated, ErrorMessage)` on failure
- `@Changes` — receives `$action` from MERGE OUTPUT clause

#### Relationships
- `dbo.SupplierFeed.WarehouseId, Sku -> dbo.StockLevel.WarehouseId, Sku` — MERGE join key (no declared FK constraint)
- `dbo.StockLevel (WarehouseId, Sku)` — composite primary key `PK_StockLevel`

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures `@Started = SYSUTCDATETIME()`; declares `@Changes TABLE (MergeAction NVARCHAR(10))`
  - `BEGIN TRANSACTION`
  - `MERGE dbo.StockLevel` using aggregated `dbo.SupplierFeed` (`ReceivedAt >= @Since`) `ON WarehouseId + Sku`
    - `WHEN MATCHED THEN UPDATE` sets `OnHand = target.OnHand + source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`
    - `WHEN NOT MATCHED BY TARGET THEN INSERT (WarehouseId, Sku, OnHand)`
    - `OUTPUT $action INTO @Changes`
  - Inserts success row into `dbo.SyncLog` with `SUM(CASE ...)` counts of INSERT/UPDATE from `@Changes`
  - `COMMIT TRANSACTION`
  - `CATCH` block: `ROLLBACK` if `@@TRANCOUNT > 0`; inserts error row into `dbo.SyncLog` with `ERROR_MESSAGE()`; rethrows via `THROW`

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Table variable: `@Changes`
- System functions/globals: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`, MERGE `$action` output
