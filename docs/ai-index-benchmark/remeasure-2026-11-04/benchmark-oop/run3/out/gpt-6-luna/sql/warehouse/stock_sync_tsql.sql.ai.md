### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:31Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Synchronizes warehouse stock quantities from recent supplier feed rows and records the run outcome.

#### Purpose
- Aggregate supplier quantities since a cutoff by warehouse and SKU.
- Add quantities to existing stock or create stock records; log merge counts or errors.

#### Type
- SQL Server (T-SQL): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` — composite primary key `PK_StockLevel`.
  - `Sku NVARCHAR(40) NOT NULL` — composite primary key `PK_StockLevel`.
  - `OnHand INT NOT NULL DEFAULT (0)` — default constraint `DF_StockLevel_OnHand`.
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — default constraint `DF_StockLevel_UpdatedAt`.
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
- `@Changes`: `MergeAction` (aggregate merge outcomes).

#### Writes
- `dbo.StockLevel`: MERGE inserts unmatched warehouse/SKU pairs (`WarehouseId`, `Sku`, `OnHand`) or updates matched rows (`OnHand`, `UpdatedAt`); row counts depend on qualifying feed groups and matches.
- `dbo.SyncLog`: one row per successful execution with `StartedAt`, `RowsInserted`, `RowsUpdated`; on error, one row with `StartedAt`, zero counts, and `ErrorMessage`.

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` — MERGE match.
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` — MERGE match.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; aggregates feed quantities by `WarehouseId`, `Sku` where `ReceivedAt >= @Since`.
  - In a transaction, adds aggregated quantities to matching stock or inserts new stock rows; captures INSERT/UPDATE actions.
  - Writes action counts to `dbo.SyncLog` and commits.
  - On error, rolls back if needed, logs `ERROR_MESSAGE()`, then rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- `SYSUTCDATETIME()`
- `ERROR_MESSAGE()`
