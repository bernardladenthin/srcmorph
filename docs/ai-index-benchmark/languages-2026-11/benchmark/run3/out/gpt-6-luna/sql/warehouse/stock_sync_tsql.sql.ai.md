### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:59:51Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock quantities from recently received supplier feed rows and records the synchronization outcome.

#### Purpose
- Aggregate supplier quantities received since a cutoff by warehouse and SKU, then add them to current stock or create stock-level rows.
- Log inserted and updated row counts, or log an error and rethrow it.

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
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`; primary key constraint `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`; primary key constraint `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filters by `ReceivedAt >= @Since`; groups by warehouse and SKU; sums quantity).
- `dbo.StockLevel`: `WarehouseId`, `Sku` for MERGE matching; `OnHand` for matched-row updates.

#### Writes
- `dbo.StockLevel`: MERGE updates matched rows’ `OnHand` and `UpdatedAt`; inserts unmatched rows’ `WarehouseId`, `Sku`, `OnHand`.
- `dbo.SyncLog`: success path inserts `StartedAt`, `RowsInserted`, `RowsUpdated` from merge-action counts; error path inserts `StartedAt`, zero counts, and `ERROR_MESSAGE()`.

#### Relationships
- MERGE match: `dbo.StockLevel.WarehouseId -> source.WarehouseId` and `dbo.StockLevel.Sku -> source.Sku` (source is aggregated `dbo.SupplierFeed` data).
- No declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Captures UTC start time, starts a transaction, and aggregates feed quantities received since `@Since`.
- Adds aggregated quantities to matching stock rows or inserts stock rows for unmatched warehouse/SKU pairs; captures MERGE actions.
- Inserts counts of inserted and updated rows into `dbo.SyncLog`, then commits.
- On error, rolls back any active transaction, logs the error with zero counts, and rethrows.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
