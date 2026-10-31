### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:33:05Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates supplier quantities received since a cutoff into warehouse SKU stock levels and records synchronization outcomes.

#### Purpose
- Defines warehouse stock, supplier-feed, and synchronization-log tables.
- `dbo.usp_SyncStock` applies feed quantities to stock and logs inserted/updated counts or errors.

#### Type
- SQL Server (T-SQL): 3 tables, 1 procedure

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)` — constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — constraint `DF_StockLevel_UpdatedAt`
  - Primary key `PK_StockLevel` (`WarehouseId`, `Sku`)
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL` — primary key constraint `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1, 1) NOT NULL` — primary key constraint `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters on `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, and sums `Quantity`.
- `dbo.StockLevel` as MERGE target: reads `WarehouseId`, `Sku`, `OnHand` for matching and updates.
- `@Changes` table variable: reads `MergeAction` to count inserted and updated rows.

#### Writes
- `dbo.StockLevel`: MERGE inserts `WarehouseId`, `Sku`, `OnHand` for unmatched rows; updates `OnHand` and `UpdatedAt` for matched rows.
- `dbo.SyncLog`: inserts one outcome row on success with `StartedAt`, `RowsInserted`, `RowsUpdated`; on failure, inserts one row with zero counts and `ErrorMessage`.
- Row counts depend on the feed and MERGE results.

#### Relationships
- `dbo.StockLevel.WarehouseId` + `dbo.StockLevel.Sku` match `dbo.SupplierFeed.WarehouseId` + `dbo.SupplierFeed.Sku` in the MERGE; no foreign key is declared.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Captures `@Started` using `SYSUTCDATETIME()`; aggregates supplier quantities received since `@Since` by warehouse and SKU.
- In a transaction, adds aggregated quantities to existing `dbo.StockLevel` rows or inserts new rows; captures MERGE actions in `@Changes`.
- Logs inserted and updated counts, then commits.
- On error, rolls back any active transaction, logs `ERROR_MESSAGE()` with zero counts, and rethrows.
