### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:46:57Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly supplier feed updates are merged into warehouse stock balances and recorded in sync audit logs.

#### Purpose
- `dbo.usp_SyncStock` applies supplier feed changes since `@Since` to `dbo.StockLevel` and records insert/update totals in `dbo.SyncLog`.
- The script defines warehouse stock, supplier feed, and sync audit tables for nightly stock synchronization.

#### Type
- T-SQL script.
- Object kinds: `dbo.StockLevel` table, `dbo.SupplierFeed` table, `dbo.SyncLog` table, `dbo.usp_SyncStock` procedure.

#### Schema
- `dbo.StockLevel`:
  - `WarehouseId INT NOT NULL` ; PK part of `PK_StockLevel` on `(WarehouseId, Sku)`.
  - `Sku NVARCHAR(40) NOT NULL` ; PK part of `PK_StockLevel` on `(WarehouseId, Sku)`.
  - `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`.
  - `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`.
  - PK: `PK_StockLevel` on `(WarehouseId, Sku)`.
  - Indexes: primary key only.
- `dbo.SupplierFeed`:
  - `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL` ; PK `PK_SupplierFeed`.
  - `WarehouseId INT NOT NULL`.
  - `Sku NVARCHAR(40) NOT NULL`.
  - `Quantity INT NOT NULL`.
  - `ReceivedAt DATETIME2(0) NOT NULL`.
  - PK: `PK_SupplierFeed` on `FeedRowId`.
  - Indexes: primary key only.
- `dbo.SyncLog`:
  - `SyncLogId INT IDENTITY(1, 1) NOT NULL` ; PK `PK_SyncLog`.
  - `StartedAt DATETIME2(0) NOT NULL`.
  - `RowsInserted INT NOT NULL`.
  - `RowsUpdated INT NOT NULL`.
  - `ErrorMessage NVARCHAR(4000) NULL`.
  - PK: `PK_SyncLog` on `SyncLogId`.
  - Indexes: primary key only.

#### Reads
- `dbo.usp_SyncStock` reads aggregated supplier feed rows from `dbo.SupplierFeed`:
  - `dbo.SupplierFeed.WarehouseId`, `dbo.SupplierFeed.Sku`, `dbo.SupplierFeed.Quantity`, `dbo.SupplierFeed.ReceivedAt`.
  - Filter: `ReceivedAt >= @Since`.
  - Grouping: `GROUP BY WarehouseId, Sku`.
  - Aggregate: `SUM(Quantity) AS Quantity`.
- `dbo.usp_SyncStock` reads current stock rows from `dbo.StockLevel`:
  - `dbo.StockLevel.WarehouseId`, `dbo.StockLevel.Sku`, `dbo.StockLevel.OnHand`.
  - Match condition: `target.WarehouseId = source.WarehouseId` and `target.Sku = source.Sku`.
- `dbo.usp_SyncStock` reads `@Changes.MergeAction` after `OUTPUT $action INTO @Changes`.
- `dbo.usp_SyncStock` reads `@Since` as procedure parameter.

#### Writes
- `dbo.usp_SyncStock` writes to `dbo.StockLevel` via `MERGE`:
  - `WHEN MATCHED` updates `dbo.StockLevel.OnHand = target.OnHand + source.Quantity`.
  - `WHEN MATCHED` updates `dbo.StockLevel.UpdatedAt = SYSUTCDATETIME()`.
  - `WHEN NOT MATCHED BY TARGET` inserts `dbo.StockLevel (WarehouseId, Sku, OnHand)` using `source.WarehouseId`, `source.Sku`, `source.Quantity`.
- `dbo.usp_SyncStock` writes to `dbo.SyncLog`:
  - `INSERT INTO dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated) SELECT @Started, SUM(CASE WHEN MergeAction = 'INSERT' THEN 1 ELSE 0 END), SUM(CASE WHEN MergeAction = 'UPDATE' THEN 1 ELSE 0 END) FROM @Changes;`
  - `INSERT INTO dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage) VALUES (@Started, 0, 0, ERROR_MESSAGE());`
- `@Changes` table variable is populated by `OUTPUT $action INTO @Changes`.

#### Relationships
- No explicit foreign key constraints declared in `dbo.StockLevel`, `dbo.SupplierFeed`, or `dbo.SyncLog`.
- Merge join condition in `dbo.usp_SyncStock`:
  - `dbo.StockLevel.WarehouseId = source.WarehouseId`
  - `dbo.StockLevel.Sku = source.Sku`
- Supplier-derived source rows join to target stock by:
  - `dbo.SupplierFeed.WarehouseId -> source.WarehouseId`
  - `dbo.SupplierFeed.Sku -> source.Sku`

#### Routine logic
- Parameter: `@Since DATETIME2(0)`.
- Essential steps:
  - `SET NOCOUNT ON;`
  - `DECLARE @Started DATETIME2(0) = SYSUTCDATETIME();`
  - `DECLARE @Changes TABLE (MergeAction NVARCHAR(10));`
  - `BEGIN TRANSACTION;`
  - `MERGE dbo.StockLevel AS target USING (SELECT WarehouseId, Sku, SUM(Quantity) AS Quantity FROM dbo.SupplierFeed WHERE ReceivedAt >= @Since GROUP BY WarehouseId, Sku) AS source ON target.WarehouseId = source.WarehouseId AND target.Sku = source.Sku WHEN MATCHED THEN UPDATE SET OnHand = target.OnHand + source.Quantity, UpdatedAt = SYSUTCDATETIME() WHEN NOT MATCHED BY TARGET THEN INSERT (WarehouseId, Sku, OnHand) VALUES (source.WarehouseId, source.Sku, source.Quantity) OUTPUT $action INTO @Changes;`
  - `INSERT INTO dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated) SELECT @Started, SUM(CASE WHEN MergeAction = 'INSERT' THEN 1 ELSE 0 END), SUM(CASE WHEN MergeAction = 'UPDATE' THEN 1 ELSE 0 END) FROM @Changes;`
  - `COMMIT TRANSACTION;`
- Error handling:
  - `BEGIN CATCH` checks `@@TRANCOUNT > 0` and rolls back.
  - Inserts `dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage)` with `(@Started, 0, 0, ERROR_MESSAGE())`.
  - Re-throws with `THROW;`.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- `@Changes`
- `SYSUTCDATETIME()`
- `ERROR_MESSAGE()`
