### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:41Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronisation that merges supplier feed quantities into per-warehouse SKU stock levels and logs the outcome.

#### Purpose
- Models warehouse stock levels per SKU and the raw supplier feed rows that drive nightly reconciliation.
- Provides `dbo.usp_SyncStock` to merge feed quantities into `StockLevel` and record a `SyncLog` entry per run.

#### Type
- T-SQL (SQL Server): 3 tables + 1 stored procedure.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` — PK (composite)
  - `Sku NVARCHAR(40) NOT NULL` — PK (composite)
  - `OnHand INT NOT NULL DEFAULT (0)` — `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — `DF_StockLevel_UpdatedAt`
  - `PK_StockLevel (WarehouseId, Sku)`
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt)` filtered by `ReceivedAt >= @Since`, aggregated with `SUM(Quantity) GROUP BY WarehouseId, Sku`.
- `dbo.usp_SyncStock` reads `dbo.StockLevel (WarehouseId, Sku, OnHand)` as the MERGE target.

#### Writes
- `dbo.usp_SyncStock` MERGEs into `dbo.StockLevel`:
  - UPDATE: `OnHand = target.OnHand + source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`
  - INSERT: `(WarehouseId, Sku, OnHand)`
- `dbo.usp_SyncStock` INSERTs into `dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated)` on success; on error INSERTs `(StartedAt, RowsInserted=0, RowsUpdated=0, ErrorMessage)`.

#### Relationships
- `dbo.StockLevel.WarehouseId` + `dbo.StockLevel.Sku` ↔ `dbo.SupplierFeed.WarehouseId` + `dbo.SupplierFeed.Sku` (logical join key used in MERGE `ON` clause; no explicit FK constraint defined).
- `dbo.SyncLog` is a standalone audit log with no FK to `StockLevel` or `SupplierFeed`.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures `@Started = SYSUTCDATETIME()`; declares table variable `@Changes (MergeAction NVARCHAR(10))`.
  - Opens transaction; executes `MERGE dbo.StockLevel` using aggregated `SupplierFeed` rows since `@Since`.
  - `WHEN MATCHED` → increment `OnHand`, refresh `UpdatedAt`.
  - `WHEN NOT MATCHED BY TARGET` → insert new `StockLevel` row with `OnHand = source.Quantity`.
  - `OUTPUT $action INTO @Changes` to capture per-row action.
  - Aggregates `@Changes` into `RowsInserted` / `RowsUpdated` and INSERTs a `SyncLog` row.
  - `COMMIT` on success; on `CATCH` → `ROLLBACK`, INSERT error `SyncLog` row, `THROW`.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`
