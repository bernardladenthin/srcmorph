### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:38:16Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock quantities from supplier feed rows received since a cutoff and records the synchronization outcome.

#### Purpose
- Aggregate recent supplier quantities by warehouse and SKU, then add them to existing stock or create stock rows.
- Log inserted/updated counts on success or an error message on failure.

#### Type
- T-SQL (SQL Server); tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint/index `PK_StockLevel` on `(WarehouseId, Sku)`
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`; primary key constraint/index `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`; primary key constraint/index `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` in the `MERGE` match and update.
- `@Changes`: `MergeAction` when calculating logged counts.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts unmatched `(WarehouseId, Sku)` rows with aggregated `Quantity` as `OnHand`; updates matched rows by adding aggregated `Quantity` to `OnHand` and refreshing `UpdatedAt`.
- `dbo.SyncLog`: one row per successful procedure invocation, with `StartedAt`, inserted count, and updated count.
- `dbo.SyncLog`: on failure, one row with `StartedAt`, zero counts, and `ERROR_MESSAGE()`; transaction is rolled back before this insert.
- `@Changes`: `MERGE OUTPUT $action` records each merge action for count calculation.

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` — `MERGE` match.
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` — `MERGE` match.
- No declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`.
- Captures UTC start time in `@Started`; disables row-count messages.
- Within a transaction, aggregates feed rows since `@Since` and merges them into `dbo.StockLevel`.
- Counts merge `INSERT` and `UPDATE` actions via `@Changes`, inserts a success row into `dbo.SyncLog`, and commits.
- On error, rolls back any active transaction, inserts a failure row into `dbo.SyncLog`, then rethrows the error.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
