### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:47Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Aggregates recent supplier-feed quantities by warehouse and SKU, updates warehouse stock levels, and records synchronization counts or errors.

#### Purpose
- Synchronizes `dbo.StockLevel` from supplier quantities received since a supplied timestamp.
- Logs inserted and updated stock-row counts, or the error when synchronization fails.

#### Type
- SQL Server (T-SQL); tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure: `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint `PK_StockLevel` on `(WarehouseId, Sku)`
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
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters on `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, and sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku` for matching; `OnHand` for matched-row updates.
- `@Changes`: `MergeAction` for counting merge inserts and updates.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts unmatched `(WarehouseId, Sku)` rows with summed `Quantity` as `OnHand`; updates matched `OnHand` by adding summed `Quantity` and refreshes `UpdatedAt`.
- `dbo.SyncLog`: inserts one outcome row on success with `StartedAt`, `RowsInserted`, and `RowsUpdated`; inserts one error row on failure with zero counts and `ErrorMessage`.
- `@Changes`: receives each `MERGE` action via `OUTPUT`.

#### Relationships
- `dbo.StockLevel.WarehouseId -> source.WarehouseId` and `dbo.StockLevel.Sku -> source.Sku` (MERGE match; source aggregates `dbo.SupplierFeed` by warehouse and SKU).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time and initializes a merge-action table variable.
  - Within a transaction, merges grouped feed quantities received since `@Since` into stock levels.
  - Logs counts of inserted and updated rows, then commits.
  - On error, rolls back any active transaction, logs `ERROR_MESSAGE()`, and rethrows.
