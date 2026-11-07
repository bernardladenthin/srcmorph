### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:31Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Aggregates recent supplier quantities by warehouse and SKU, updates on-hand inventory, and records each synchronization outcome.

#### Purpose
- Maintains warehouse stock levels from supplier-feed rows received since a supplied timestamp.
- Logs inserted and updated stock counts, or the error from a failed synchronization.

#### Type
- Tables and procedure; Microsoft SQL Server (T-SQL).
- Procedure: `dbo.usp_SyncStock`.

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
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by warehouse and SKU, sums `Quantity`.
- `dbo.StockLevel`: matches on `WarehouseId`, `Sku`; reads `OnHand` when updating matched rows.
- `@Changes`: reads `MergeAction` to calculate insert and update counts.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts `(WarehouseId, Sku, OnHand)` for unmatched source keys; updates `OnHand` and `UpdatedAt` for matched keys.
- `dbo.SyncLog`: success-path `INSERT ... SELECT` writes one aggregate log row; catch-path `INSERT ... VALUES` writes one error log row. Both paths cannot execute for the same invocation.
- `@Changes`: receives each `MERGE` action through `OUTPUT $action`.

#### Relationships
- `dbo.StockLevel.WarehouseId -> source.WarehouseId` and `dbo.StockLevel.Sku -> source.Sku` (`MERGE` matching pairs; `source` aggregates `dbo.SupplierFeed`).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; begins a transaction.
  - Aggregates supplier quantities received since `@Since` by warehouse and SKU.
  - Merges aggregates into `dbo.StockLevel`, recording actions in `@Changes`.
  - On success, logs insert/update counts and commits.
  - On error, rolls back if a transaction is active, logs `ERROR_MESSAGE()` with zero counts, then rethrows.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- `SYSUTCDATETIME()`
- `ERROR_MESSAGE()`
