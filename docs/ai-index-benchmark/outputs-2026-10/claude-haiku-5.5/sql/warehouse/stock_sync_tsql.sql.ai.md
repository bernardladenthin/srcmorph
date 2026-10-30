### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:32:07Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly T-SQL job that merges supplier feed quantities into warehouse stock levels and logs each sync run.

#### Purpose
- Synchronises `dbo.SupplierFeed` quantities into `dbo.StockLevel` on-hand counts per `WarehouseId` and `Sku`.
- Records per-run insert/update counts and errors in `dbo.SyncLog`.

#### Type
- Three tables and one stored procedure `dbo.usp_SyncStock`.
- Dialect: SQL Server (T-SQL).

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`, PK part 1 (`PK_StockLevel`, composite PK with `Sku`)
  - `Sku NVARCHAR(40) NOT NULL`, PK part 2
  - `OnHand INT NOT NULL`, DEFAULT `0` (`DF_StockLevel_OnHand`)
  - `UpdatedAt DATETIME2(0) NOT NULL`, DEFAULT `SYSUTCDATETIME()` (`DF_StockLevel_UpdatedAt`)
- **dbo.SupplierFeed**
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`, PK (`PK_SupplierFeed`)
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- **dbo.SyncLog**
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`, PK (`PK_SyncLog`)
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
- No secondary indexes declared.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku`, summed `Quantity`).
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` (merge match and update source).
- `@Changes` table variable: `MergeAction` (aggregated into SyncLog counts).

#### Writes
- `dbo.StockLevel`: MERGE target; updates `OnHand` (increment) and `UpdatedAt` on match; inserts `WarehouseId, Sku, OnHand` on no match.
- `dbo.SyncLog`: INSERT of `StartedAt, RowsInserted, RowsUpdated` on success; `StartedAt, RowsInserted, RowsUpdated, ErrorMessage` on failure.
- `@Changes`: receives `$action` via `OUTPUT ... INTO`.

#### Relationships
- `dbo.SupplierFeed.(WarehouseId, Sku) -> dbo.StockLevel.(WarehouseId, Sku)` (logical join in MERGE; no declared FK).
- `dbo.SyncLog`: no relationship to other tables.

#### Routine logic
- **dbo.usp_SyncStock**, parameter `@Since DATETIME2(0)`.
- Sets `@Started` to `SYSUTCDATETIME()`; declares table variable `@Changes (MergeAction)`.
- Begins transaction inside `TRY`.
- MERGE aggregated `SupplierFeed` rows (`ReceivedAt >= @Since`, `SUM(Quantity)` per `WarehouseId, Sku`) into `StockLevel`.
  - Matched: `OnHand = OnHand + Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
  - Not matched: insert new row with `OnHand = Quantity`.
  - `OUTPUT $action INTO @Changes`.
- Inserts `SyncLog` row with counts of `INSERT` and `UPDATE` actions.
- Commits transaction.
- `CATCH`: rolls back if `@@TRANCOUNT > 0`, inserts `SyncLog` row with zero counts and `ERROR_MESSAGE()`, then `THROW`.
- Effect: `StockLevel` on-hand counts increased by feed quantities since `@Since`; one `SyncLog` row per run.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Procedure: `dbo.usp_SyncStock`.
- Constraints: `PK_StockLevel`, `DF_StockLevel_OnHand`, `DF_StockLevel_UpdatedAt`, `PK_SupplierFeed`, `PK_SyncLog`.
