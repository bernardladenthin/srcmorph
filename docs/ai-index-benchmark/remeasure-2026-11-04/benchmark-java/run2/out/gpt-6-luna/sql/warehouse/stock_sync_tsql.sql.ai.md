### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:21Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Aggregates recent supplier quantities by warehouse and SKU, applies them to on-hand inventory, and records synchronization results or errors.

#### Purpose
- Synchronize `dbo.StockLevel` from supplier-feed rows received since a supplied timestamp.
- Log inserted and updated inventory-row counts, or the error if synchronization fails.

#### Type
- T-SQL (SQL Server): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; constraint `DF_StockLevel_UpdatedAt`
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
- No explicit indexes created; primary-key constraints are declared on all three tables.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since` and aggregates `Quantity` by warehouse and SKU.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` for matching and update calculations.
- `@Changes`: `MergeAction` for log counts.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts `WarehouseId`, `Sku`, `OnHand` for unmatched rows; updates `OnHand`, `UpdatedAt` for matched rows. Runtime-dependent row counts.
- `dbo.SyncLog`: success `INSERT` writes `StartedAt`, `RowsInserted`, `RowsUpdated` (one row); error `INSERT` writes those columns plus `ErrorMessage` (one row).
- `@Changes`: `MERGE OUTPUT` writes each merge action.

#### Relationships
- Merge match: `dbo.StockLevel.WarehouseId -> source.WarehouseId`; `dbo.StockLevel.Sku -> source.Sku` (source aggregated from `dbo.SupplierFeed`).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Captures UTC start time; begins a transaction.
- Aggregates feed quantities received since `@Since`; merges totals into stock, adding quantities to existing `OnHand` or inserting new stock rows.
- Logs merge insert/update counts and commits.
- On failure, rolls back if needed, logs `ERROR_MESSAGE()` with zero counts, then rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
