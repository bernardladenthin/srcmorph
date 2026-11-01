### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:15:53Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse inventory quantities from recent supplier-feed rows and records the synchronization outcome.

#### Purpose
- Aggregate supplier quantities by warehouse and SKU, then add them to existing stock or create stock-level rows.
- Log inserted and updated row counts, or log an error and rethrow it.

#### Type
- T-SQL (SQL Server): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key `PK_StockLevel (WarehouseId, Sku)`
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
- No separately declared indexes.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filter on `ReceivedAt`, group by `WarehouseId`, `Sku`, aggregate `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand`; MERGE match and update source.
- `@Changes` table variable: `MergeAction`; counts INSERT and UPDATE actions.

#### Writes
- `dbo.StockLevel`: MERGE inserts `WarehouseId`, `Sku`, `OnHand`; updates `OnHand`, `UpdatedAt`. Runtime row count depends on feed and existing stock.
- `dbo.SyncLog`: inserts success counts into `StartedAt`, `RowsInserted`, `RowsUpdated`; on failure inserts `StartedAt`, zero counts, and `ErrorMessage`. Runtime row count depends on procedure outcome.

#### Relationships
- MERGE join: `dbo.StockLevel.WarehouseId -> source.WarehouseId`; `dbo.StockLevel.Sku -> source.Sku` (source aggregated from `dbo.SupplierFeed`).
- No declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Captures UTC start time; aggregates feed rows with `ReceivedAt >= @Since` by warehouse and SKU.
- In a transaction, adds aggregate quantities to matched stock rows or inserts missing rows; captures MERGE actions.
- Logs inserted and updated counts, then commits.
- On error, rolls back if a transaction is active, logs `ERROR_MESSAGE()` with zero counts, and rethrows.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
