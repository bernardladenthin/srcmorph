### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:44:58Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Synchronizes warehouse on-hand inventory from newly received supplier quantities and records the synchronization outcome.

#### Purpose
- Aggregates supplier feed quantities by warehouse and SKU, then adds them to stock or creates stock records.
- Logs successful row counts or the error when synchronization fails.

#### Type
- Tables and procedure; Microsoft SQL Server (T-SQL).

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` — primary key component (`PK_StockLevel`).
  - `Sku NVARCHAR(40) NOT NULL` — primary key component (`PK_StockLevel`).
  - `OnHand INT NOT NULL DEFAULT (0)` — default constraint `DF_StockLevel_OnHand`.
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — default constraint `DF_StockLevel_UpdatedAt`.
  - Primary key: `PK_StockLevel (WarehouseId, Sku)`.
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — primary key `PK_SupplierFeed`.
  - `WarehouseId INT NOT NULL`.
  - `Sku NVARCHAR(40) NOT NULL`.
  - `Quantity INT NOT NULL`.
  - `ReceivedAt DATETIME2(0) NOT NULL`.
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — primary key `PK_SyncLog`.
  - `StartedAt DATETIME2(0) NOT NULL`.
  - `RowsInserted INT NOT NULL`.
  - `RowsUpdated INT NOT NULL`.
  - `ErrorMessage NVARCHAR(4000) NULL`.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` (MERGE match and update).
- `@Changes`: `MergeAction` (success log aggregation).

#### Writes
- `dbo.StockLevel`: MERGE updates `OnHand`, `UpdatedAt` for matches; inserts `WarehouseId`, `Sku`, `OnHand` for nonmatches.
- `dbo.SyncLog`: success-path INSERT writes `StartedAt`, `RowsInserted`, `RowsUpdated` (one row); catch-path INSERT writes those columns plus `ErrorMessage` (one row).

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` — MERGE match.
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` — MERGE match.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`.
- Captures UTC start time; selects supplier feed rows with `ReceivedAt >= @Since`, groups by warehouse and SKU, and sums `Quantity`.
- MERGE adds summed quantities to matched stock and refreshes `UpdatedAt`; inserts new stock rows otherwise.
- Records MERGE INSERT/UPDATE counts in `dbo.SyncLog` and commits the transaction.
- On failure, rolls back an active transaction, logs `ERROR_MESSAGE()` with zero counts, then rethrows.

#### Dependencies
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
