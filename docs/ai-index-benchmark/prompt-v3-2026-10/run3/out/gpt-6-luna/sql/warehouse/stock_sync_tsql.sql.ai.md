### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:16Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock quantities from supplier feed records received since a given timestamp and logs the outcome.

#### Purpose
- Aggregates recent supplier quantities by warehouse and SKU, adds them to existing stock or creates stock-level rows.
- Logs inserted and updated row counts; records errors and rethrows them.

#### Type
- T-SQL (SQL Server): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key `PK_StockLevel` (`WarehouseId`, `Sku`)
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

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` in `MERGE` matching and update logic.

#### Writes
- `dbo.StockLevel`: `MERGE` updates `OnHand`, `UpdatedAt` for matched rows; inserts `WarehouseId`, `Sku`, `OnHand` for unmatched rows. Insert count is data-dependent.
- `dbo.SyncLog`: inserts one success log row per successful procedure execution; on failure, inserts one error log row with `ErrorMessage = ERROR_MESSAGE()`.

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` (MERGE match).
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` (MERGE match).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time in `@Started`; creates `@Changes` table variable for MERGE actions.
  - In a transaction, aggregates feed rows received since `@Since` by warehouse and SKU, then adds quantities to matching stock or inserts new stock rows.
  - Counts `INSERT` and `UPDATE` actions from `@Changes` and writes them to `dbo.SyncLog`.
  - On error, rolls back if a transaction is active, logs the error with zero row counts, and rethrows.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
