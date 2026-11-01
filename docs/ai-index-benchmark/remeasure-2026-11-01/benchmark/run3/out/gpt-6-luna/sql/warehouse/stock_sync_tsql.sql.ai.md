### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:01:36Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock quantities from recent supplier feed rows and records the synchronization outcome.

#### Purpose
- Aggregate supplier quantities by warehouse and SKU, then add them to existing stock or create stock-level rows.
- Log inserted and updated row counts, or log an error and rethrow it.

#### Type
- T-SQL (SQL Server); tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint `PK_StockLevel` on `(WarehouseId, Sku)`
  - Primary-key index: `PK_StockLevel`, table `dbo.StockLevel`, columns `(WarehouseId, Sku)`; method not specified
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`; primary key constraint `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
  - Primary-key index: `PK_SupplierFeed`, table `dbo.SupplierFeed`, column `(FeedRowId)`; method not specified
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`; primary key constraint `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
  - Primary-key index: `PK_SyncLog`, table `dbo.SyncLog`, column `(SyncLogId)`; method not specified

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filter by `ReceivedAt >= @Since`, aggregate `SUM(Quantity)` grouped by `WarehouseId, Sku`.
- `dbo.StockLevel`: `WarehouseId`, `Sku` for matching; `OnHand` for matched-row updates.
- `@Changes`: `MergeAction` for synchronization counts.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts unmatched `(WarehouseId, Sku, OnHand)` rows or updates matched `OnHand` and `UpdatedAt`; row counts depend on feed and existing stock.
- `dbo.SyncLog`: one row per successful execution with `StartedAt`, `RowsInserted`, `RowsUpdated`; on error, one row with zero counts and `ErrorMessage`.
- `@Changes`: receives one `$action` value per affected `MERGE` row.

#### Relationships
- `dbo.StockLevel.WarehouseId -> source.WarehouseId`; `dbo.StockLevel.Sku -> source.Sku` (MERGE match; source aggregates `dbo.SupplierFeed` by those columns).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`.
- Captures UTC start time; aggregates feed rows received since `@Since` by warehouse and SKU.
- In a transaction, adds quantities to matched stock or inserts new stock rows; captures `INSERT`/`UPDATE` actions.
- Logs action counts and commits; on failure, rolls back if needed, logs `ERROR_MESSAGE()`, then rethrows.

#### Dependencies
- Tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
