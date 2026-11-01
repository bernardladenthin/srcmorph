### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:43:54Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates supplier stock receipts by warehouse and SKU, updates current on-hand inventory, and records each synchronization outcome.

#### Purpose
- Synchronizes supplier feed quantities received since a timestamp into warehouse stock levels.
- Logs inserted and updated stock-row counts, or the error message on failure.

#### Type
- SQL Server (T-SQL); tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key `PK_StockLevel` on (`WarehouseId`, `Sku`)
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`; primary key `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`; primary key `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
- Primary-key indexes: `PK_StockLevel` on `dbo.StockLevel(WarehouseId, Sku)`; `PK_SupplierFeed` on `dbo.SupplierFeed(FeedRowId)`; `PK_SyncLog` on `dbo.SyncLog(SyncLogId)`.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku` for matching; `OnHand` for matched-row updates.
- `@Changes`: `MergeAction` for inserted/updated counts.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts unmatched (`WarehouseId`, `Sku`, `OnHand`) or updates matched (`OnHand`, `UpdatedAt`).
- `dbo.SyncLog`: success path inserts one aggregate row with `StartedAt`, `RowsInserted`, `RowsUpdated`; catch path inserts one row with `StartedAt`, zero counts, and `ErrorMessage`. One log row per procedure execution.

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` — `MERGE` match key.
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` — `MERGE` match key.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`.
- Captures UTC start time; begins a transaction.
- Aggregates feed quantities since `@Since` by warehouse and SKU; merges totals into stock, adding quantities to existing `OnHand` or inserting new stock rows.
- Captures merge actions and logs inserted/updated counts; commits on success.
- On error, rolls back any active transaction, logs `ERROR_MESSAGE()` with zero counts, and rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
