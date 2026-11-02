### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:36Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock quantities from newly received supplier-feed rows and records each synchronization outcome.

#### Purpose
- Aggregates supplier quantities received since a cutoff by warehouse and SKU, then adds them to existing stock or creates new stock levels.
- Logs inserted and updated stock counts, or the error message if synchronization fails.

#### Type
- Tables and procedure; Microsoft SQL Server (T-SQL).
- Procedure: `dbo.usp_SyncStock(@Since DATETIME2(0))`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key `PK_StockLevel` (`WarehouseId`, `Sku`)
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL`; primary key constraint `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1, 1) NOT NULL`; primary key constraint `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, and sums `Quantity`.
- `dbo.StockLevel` target rows: `WarehouseId`, `Sku`, `OnHand`.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts unmatched warehouse/SKU pairs with `OnHand = source.Quantity`; updates matched rows by adding `source.Quantity` to `OnHand` and setting `UpdatedAt`.
- `dbo.SyncLog`: inserts one success log row with `StartedAt`, inserted count, and updated count; on failure, inserts one row with zero counts and `ERROR_MESSAGE()`.
- `@Changes` table variable: captures each `MERGE` action (`INSERT` or `UPDATE`).

#### Relationships
- `dbo.StockLevel.WarehouseId` + `dbo.StockLevel.Sku` are matched against `dbo.SupplierFeed.WarehouseId` + `dbo.SupplierFeed.Sku` in the `MERGE`; no declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`:
  - Captures UTC start time in `@Started`; begins a transaction.
  - Aggregates feed quantities received since `@Since` by warehouse and SKU.
  - Merges aggregates into `dbo.StockLevel`; captures actions in `@Changes`.
  - Logs counts of inserted and updated stock rows, then commits.
  - On error, rolls back if a transaction is active, logs the error, and rethrows it.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
