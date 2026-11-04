### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:06:57Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Synchronizes warehouse on-hand quantities from recent supplier feed records and records the synchronization outcome.

#### Purpose
- Aggregate supplier quantities received since a cutoff by warehouse and SKU, then add them to current stock.
- Log inserted and updated stock-row counts, or the error if synchronization fails.

#### Type
- SQL Server (T-SQL): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key `PK_StockLevel (WarehouseId, Sku)`
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
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` for merge matching and stock updates.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts unmatched warehouse/SKU groups and updates matched rows; updates `OnHand` by adding grouped quantity and refreshes `UpdatedAt`.
- `dbo.SyncLog`: one row per successful execution with inserted/updated counts; on failure, one row with zero counts and `ERROR_MESSAGE()`.
- `@Changes`: receives one `$action` value per affected `StockLevel` row.

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` and `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` (merge match; not declared foreign keys).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; aggregates eligible supplier-feed rows by warehouse and SKU.
  - In a transaction, merges aggregates into stock and records merge actions.
  - Inserts counts of `INSERT` and `UPDATE` actions into `dbo.SyncLog`, then commits.
  - On error, rolls back if needed, logs the error, and rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
