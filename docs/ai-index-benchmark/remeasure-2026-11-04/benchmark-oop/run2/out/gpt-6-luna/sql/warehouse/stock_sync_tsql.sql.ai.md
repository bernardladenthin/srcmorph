### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:34Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Aggregates recent supplier inventory receipts by warehouse and SKU, updates on-hand stock, and records the synchronization outcome.

#### Purpose
- `dbo.usp_SyncStock` applies supplier feed quantities received since a cutoff to warehouse stock levels.
- Logs inserted and updated stock-row counts, or records an error before rethrowing it.

#### Type
- SQL Server (T-SQL): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` — composite primary key.
  - `Sku NVARCHAR(40) NOT NULL` — composite primary key.
  - `OnHand INT NOT NULL DEFAULT (0)` — constraint `DF_StockLevel_OnHand`.
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — constraint `DF_StockLevel_UpdatedAt`.
  - Primary key constraint `PK_StockLevel` (`WarehouseId`, `Sku`).
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — primary key constraint `PK_SupplierFeed`.
  - `WarehouseId INT NOT NULL`.
  - `Sku NVARCHAR(40) NOT NULL`.
  - `Quantity INT NOT NULL`.
  - `ReceivedAt DATETIME2(0) NOT NULL`.
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — primary key constraint `PK_SyncLog`.
  - `StartedAt DATETIME2(0) NOT NULL`.
  - `RowsInserted INT NOT NULL`.
  - `RowsUpdated INT NOT NULL`.
  - `ErrorMessage NVARCHAR(4000) NULL`.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` during `MERGE` matching and updates.
- `@Changes`: `MergeAction` when calculating outcome counts.

#### Writes
- `dbo.StockLevel`: `MERGE` inserts `WarehouseId`, `Sku`, `OnHand` for unmatched keys; updates `OnHand`, `UpdatedAt` for matched keys. Row counts depend on feed data.
- `dbo.SyncLog`: inserts one aggregate outcome row on success (`StartedAt`, `RowsInserted`, `RowsUpdated`); on error, inserts one row (`StartedAt`, zero counts, `ErrorMessage`).

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` — `MERGE` match.
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` — `MERGE` match.
- No foreign key constraints declared.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`.
- Captures UTC start time; groups feed rows with `ReceivedAt >= @Since` by warehouse and SKU, summing `Quantity`.
- In a transaction, adds grouped quantities to matching `StockLevel` rows or inserts new rows; captures merge actions.
- Writes inserted and updated counts to `dbo.SyncLog`, then commits.
- On error, rolls back an active transaction, logs `ERROR_MESSAGE()` with zero counts, and rethrows.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
