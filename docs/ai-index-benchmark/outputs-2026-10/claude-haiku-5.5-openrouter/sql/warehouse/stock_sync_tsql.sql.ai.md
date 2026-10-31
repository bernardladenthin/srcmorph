### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:36:04Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly T-SQL job that merges supplier feed stock receipts into warehouse on-hand levels and logs each sync run with its outcome.

#### Purpose
- Apply supplier feed quantities received since a cutoff to `dbo.StockLevel.OnHand` per warehouse and SKU.
- Record per-run insert/update counts and any error message in `dbo.SyncLog`.

#### Type
- Multiple objects: 3 tables (`dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`) and 1 stored procedure (`dbo.usp_SyncStock`).
- Dialect: SQL Server (T-SQL).

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` (PK part 1)
  - `Sku NVARCHAR(40) NOT NULL` (PK part 2)
  - `OnHand INT NOT NULL DEFAULT (0)` via `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` via `DF_StockLevel_UpdatedAt`
  - PK: `PK_StockLevel (WarehouseId, Sku)`
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` (PK `PK_SupplierFeed`)
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` (PK `PK_SyncLog`)
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku`, summed `Quantity`).
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` (merge match and target read).
- `@Changes` table variable: `MergeAction` (read for aggregation into SyncLog).

#### Writes
- `dbo.StockLevel`: MERGE target
  - MATCHED: `OnHand = OnHand + source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`
  - NOT MATCHED: insert `WarehouseId`, `Sku`, `OnHand`
- `@Changes`: receives `$action` via `OUTPUT ... INTO`.
- `dbo.SyncLog`: one row per run
  - Success: `StartedAt`, `RowsInserted`, `RowsUpdated`
  - Failure (CATCH): `StartedAt`, `RowsInserted = 0`, `RowsUpdated = 0`, `ErrorMessage = ERROR_MESSAGE()`

#### Relationships
- `dbo.SupplierFeed.(WarehouseId, Sku) -> dbo.StockLevel.(WarehouseId, Sku)` (logical join in MERGE; no declared FK)
- `dbo.SyncLog`: no declared relationship to other tables.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Sets `NOCOUNT ON`, captures `@Started = SYSUTCDATETIME()`, declares table variable `@Changes (MergeAction NVARCHAR(10))`.
- TRY block:
  - Begins transaction.
  - MERGE aggregated `SupplierFeed` rows (`SUM(Quantity)` per `WarehouseId, Sku` where `ReceivedAt >= @Since`) into `StockLevel`.
  - MATCHED: adds quantity to `OnHand`, stamps `UpdatedAt`.
  - NOT MATCHED BY TARGET: inserts new row with quantity as `OnHand`.
  - Captures `$action` into `@Changes`.
  - Inserts one `SyncLog` row with counts of `'INSERT'` and `'UPDATE'` actions.
  - Commits.
- CATCH block:
  - Rolls back if `@@TRANCOUNT > 0`.
  - Inserts `SyncLog` row with zero counts and `ERROR_MESSAGE()`.
  - Re-raises via `THROW`.
- Effect: stock on-hand increased by aggregated feed quantities received since `@Since`; run audit row written either way.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Constraints: `PK_StockLevel`, `PK_SupplierFeed`, `PK_SyncLog`, `DF_StockLevel_OnHand`, `DF_StockLevel_UpdatedAt`
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`, `THROW`, `$action`
- Procedure: `dbo.usp_SyncStock`
