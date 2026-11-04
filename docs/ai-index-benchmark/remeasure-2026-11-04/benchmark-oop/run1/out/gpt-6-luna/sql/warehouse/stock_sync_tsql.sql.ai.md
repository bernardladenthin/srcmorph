### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:22:03Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Aggregates supplier stock receipts by warehouse and SKU, updates on-hand inventory, and records synchronization outcomes.

#### Purpose
- `dbo.usp_SyncStock` applies supplier-feed quantities received since a supplied timestamp to warehouse stock levels.
- Logs inserted and updated stock counts, or the error message if synchronization fails.

#### Type
- SQL Server (T-SQL): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT` NOT NULL; `Sku NVARCHAR(40)` NOT NULL; composite primary key `PK_StockLevel` (`WarehouseId`, `Sku`).
  - `OnHand INT` NOT NULL DEFAULT `(0)`; default constraint `DF_StockLevel_OnHand`.
  - `UpdatedAt DATETIME2(0)` NOT NULL DEFAULT `(SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`.
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT` IDENTITY(1,1) NOT NULL; primary key constraint `PK_SupplierFeed`.
  - `WarehouseId INT` NOT NULL; `Sku NVARCHAR(40)` NOT NULL; `Quantity INT` NOT NULL; `ReceivedAt DATETIME2(0)` NOT NULL.
- `dbo.SyncLog`
  - `SyncLogId INT` IDENTITY(1,1) NOT NULL; primary key constraint `PK_SyncLog`.
  - `StartedAt DATETIME2(0)` NOT NULL; `RowsInserted INT` NOT NULL; `RowsUpdated INT` NOT NULL; `ErrorMessage NVARCHAR(4000)` NULL.
- No separately declared indexes.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters on `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, and sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku` for matching; `OnHand` for updates.
- `@Changes`: `MergeAction` for calculating logged insert and update counts.

#### Writes
- `dbo.StockLevel`: `MERGE` updates `OnHand`, `UpdatedAt` for matches; inserts `WarehouseId`, `Sku`, `OnHand` for non-matches.
- `dbo.SyncLog`: success insert writes `StartedAt`, `RowsInserted`, `RowsUpdated`; error insert writes those columns plus `ErrorMessage`.

#### Relationships
- `dbo.StockLevel.WarehouseId -> source.WarehouseId` (MERGE match).
- `dbo.StockLevel.Sku -> source.Sku` (MERGE match).
- Source values are aggregated from `dbo.SupplierFeed` by `WarehouseId`, `Sku`; no declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`.
- Captures UTC start time; begins a transaction and aggregates feed quantities received since `@Since`.
- Merges totals into `dbo.StockLevel`, capturing `$action` values in `@Changes`.
- Inserts a `dbo.SyncLog` row with counts of inserted and updated stock rows, then commits.
- On error, rolls back any active transaction, logs `ERROR_MESSAGE()` with zero counts, and rethrows.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- `SYSUTCDATETIME()`
- `ERROR_MESSAGE()`
