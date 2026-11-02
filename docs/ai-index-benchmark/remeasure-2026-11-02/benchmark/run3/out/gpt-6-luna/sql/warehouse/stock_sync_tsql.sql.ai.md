### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:59:00Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Aggregates recent supplier inventory quantities by warehouse and SKU, updates stock balances, and records synchronization outcomes or errors.

#### Purpose
- Maintain warehouse stock levels from supplier feed rows received on or after a cutoff.
- Log inserted and updated stock counts, or the error from a failed synchronization.

#### Type
- SQL Server (T-SQL): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint `PK_StockLevel` on (`WarehouseId`, `Sku`)
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
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand`
- `@Changes`: `MergeAction`

#### Writes
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand`, `UpdatedAt` via `MERGE`; inserts unmatched warehouse/SKU pairs and adds aggregated feed quantity to matched balances.
- `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated` via aggregate `INSERT ... SELECT` after merge; inserts one log row per successful execution.
- `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` in the catch path; inserts one error log row per caught failure.

#### Relationships
- `dbo.StockLevel.WarehouseId -> source.WarehouseId` (MERGE match)
- `dbo.StockLevel.Sku -> source.Sku` (MERGE match)
- `source.WarehouseId`, `source.Sku` are grouped from `dbo.SupplierFeed.WarehouseId`, `dbo.SupplierFeed.Sku`

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Captures UTC start time; filters supplier feed by `ReceivedAt >= @Since` and aggregates `Quantity` by warehouse and SKU.
- In a transaction, merges aggregates into `dbo.StockLevel`; captures merge actions in `@Changes`.
- Logs counts of inserted and updated stock rows, then commits.
- On error, rolls back any active transaction, logs `ERROR_MESSAGE()`, and rethrows.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
