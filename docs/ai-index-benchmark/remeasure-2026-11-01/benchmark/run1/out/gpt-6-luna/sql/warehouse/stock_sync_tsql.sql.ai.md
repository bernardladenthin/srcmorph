### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:22:22Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates newly received supplier quantities by warehouse and SKU, updates or creates stock levels, and records synchronization outcomes.

#### Purpose
- Synchronizes `dbo.StockLevel` from supplier-feed rows received at or after `@Since`.
- Logs inserted and updated stock counts, or the error message if synchronization fails.

#### Type
- Tables and stored procedure; Microsoft SQL Server (T-SQL).

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint `PK_StockLevel` on `(WarehouseId, Sku)`
  - Index `PK_StockLevel` on `dbo.StockLevel (WarehouseId, Sku)`; primary-key index
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`; primary key constraint `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
  - Index `PK_SupplierFeed` on `dbo.SupplierFeed (FeedRowId)`; primary-key index
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`; primary key constraint `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
  - Index `PK_SyncLog` on `dbo.SyncLog (SyncLogId)`; primary-key index

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by `WarehouseId, Sku`, sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku` for matching; `OnHand` for quantity accumulation.
- `@Changes`: `MergeAction` when calculating inserted and updated counts.

#### Writes
- `dbo.StockLevel` via `MERGE`:
  - Matched rows: update `OnHand`, `UpdatedAt`.
  - Unmatched rows: insert `WarehouseId`, `Sku`, `OnHand`.
- `dbo.SyncLog`:
  - Success path: insert one aggregate row per procedure execution, setting `StartedAt`, `RowsInserted`, `RowsUpdated`.
  - Error path: `INSERT ... VALUES` inserts exactly 1 row with `StartedAt`, zero counts, and `ErrorMessage`; then rethrows the error.

#### Relationships
- `dbo.StockLevel.WarehouseId` + `dbo.StockLevel.Sku` match `dbo.SupplierFeed.WarehouseId` + `dbo.SupplierFeed.Sku` in the `MERGE`; no foreign-key constraints declared.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; begins a transaction.
  - Aggregates feed quantities since `@Since` by warehouse and SKU.
  - Adds quantities to matching stock levels or inserts new levels; captures merge actions in `@Changes`.
  - Logs counts of merge `INSERT` and `UPDATE` actions, then commits.
  - On error, rolls back any active transaction, logs the error, and rethrows.

#### Dependencies
- Tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- Built-in functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
