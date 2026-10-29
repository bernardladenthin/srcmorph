### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:45Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates recent supplier inventory quantities by warehouse and SKU, updates stock levels, and records synchronization outcomes or errors.

#### Purpose
- Defines stock-level, supplier-feed, and synchronization-log tables.
- Provides a procedure to apply feed quantities received since a timestamp and log the result.

#### Type
- SQL Server (T-SQL): tables and stored procedure

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)` (`DF_StockLevel_OnHand`)
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` (`DF_StockLevel_UpdatedAt`)
  - Primary key: `(WarehouseId, Sku)` (`PK_StockLevel`)
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — primary key (`PK_SupplierFeed`)
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — primary key (`PK_SyncLog`)
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters on `ReceivedAt >= @Since`, groups by `WarehouseId, Sku`, and sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` for the `MERGE` match and update.
- `@Changes` table variable: `MergeAction` when computing insert and update counts.

#### Writes
- `dbo.StockLevel`: inserts `WarehouseId`, `Sku`, `OnHand`; updates `OnHand` and `UpdatedAt`.
- `dbo.SyncLog`: inserts `StartedAt`, `RowsInserted`, `RowsUpdated`; on failure also inserts `ErrorMessage`.

#### Relationships
- `dbo.StockLevel.WarehouseId -> aggregated dbo.SupplierFeed.WarehouseId` — `MERGE` match.
- `dbo.StockLevel.Sku -> aggregated dbo.SupplierFeed.Sku` — `MERGE` match.
- No declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time and begins a transaction.
  - Aggregates `dbo.SupplierFeed` quantities received since `@Since` by warehouse and SKU.
  - Adds quantities to matched stock rows; inserts rows for unmatched warehouse/SKU pairs.
  - Counts merge `INSERT` and `UPDATE` actions and records them in `dbo.SyncLog`.
  - On error, rolls back, logs `ERROR_MESSAGE()` with zero counts, and rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
