### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:39Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse on-hand inventory from supplier feed quantities received since a cutoff, and records the sync outcome or error.

#### Purpose
- Aggregates recent `dbo.SupplierFeed` quantities by warehouse and SKU, then adds them to `dbo.StockLevel`.
- Records inserted and updated row counts in `dbo.SyncLog`; logs errors before rethrowing them.

#### Type
- Tables and stored procedure; Microsoft SQL Server (T-SQL).

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)` — default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — default constraint `DF_StockLevel_UpdatedAt`
  - Primary key: `PK_StockLevel (WarehouseId, Sku)`
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — primary key constraint `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — primary key constraint `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
- No other indexes or foreign keys declared.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, and sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` for MERGE matching and matched-row updates.
- `@Changes`: `MergeAction` for counting MERGE inserts and updates.

#### Writes
- `dbo.StockLevel`: MERGE inserts unmatched `(WarehouseId, Sku, OnHand)` rows and updates matched `OnHand`, `UpdatedAt`.
- `@Changes`: MERGE `OUTPUT $action` records each insert or update action.
- `dbo.SyncLog`: inserts one success row with `StartedAt`, aggregate `RowsInserted` and `RowsUpdated`; on error, inserts one row with zero counts and `ERROR_MESSAGE()`.

#### Relationships
- MERGE match: `dbo.StockLevel.WarehouseId -> source.WarehouseId`; `dbo.StockLevel.Sku -> source.Sku`.
- `source.WarehouseId`, `source.Sku` are grouped from `dbo.SupplierFeed`; no declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; starts a transaction.
  - Aggregates feed quantities received since `@Since` by warehouse and SKU.
  - Adds quantities to existing stock or inserts new stock rows; updates `UpdatedAt` on matched rows.
  - Logs action counts and commits.
  - On failure, rolls back if needed, logs the error, and rethrows it.

#### Dependencies
- `dbo.usp_SyncStock` references `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`, and `SYSUTCDATETIME()`.
