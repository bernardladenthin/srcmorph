### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:18Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Aggregates recent supplier inventory quantities by warehouse and SKU, updates stock levels, and records synchronization outcomes.

#### Purpose
- Synchronizes `dbo.StockLevel` from supplier feed rows received since a specified time.
- Logs inserted and updated stock counts, or an error if synchronization fails.

#### Type
- SQL Server (T-SQL): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key `PK_StockLevel` (`WarehouseId`, `Sku`)
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL`; primary key `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1, 1) NOT NULL`; primary key `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, and sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand`; matches source keys and adds aggregated quantity to existing `OnHand`.
- `@Changes` table variable: `MergeAction`, read to count `INSERT` and `UPDATE` actions.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts unmatched warehouse/SKU pairs or updates matched rows’ `OnHand` and `UpdatedAt`.
- `dbo.SyncLog`: one success row per procedure call with `StartedAt`, inserted count, and updated count; on failure, one error row with zero counts and `ERROR_MESSAGE()`.
- `@Changes`: receives each `MERGE` action via `OUTPUT $action`.

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` (MERGE match)
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` (MERGE match)

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; begins a transaction.
  - Aggregates feed quantities received since `@Since`, then merges totals into stock levels.
  - Logs merge insert/update counts and commits.
  - On error, rolls back any active transaction, logs the error, and rethrows.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- `SYSUTCDATETIME()`
- `ERROR_MESSAGE()`
