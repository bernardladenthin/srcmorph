### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:34Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates supplier inventory updates by warehouse and SKU, applies them to stock levels, and records synchronization outcomes.

#### Purpose
- Synchronize supplier feed quantities received since a specified time into warehouse stock levels.
- Log inserted and updated row counts, or the error message on failure.

#### Type
- Tables and procedure; SQL Server (T-SQL).

#### Schema
- `dbo.StockLevel`: `WarehouseId INT NOT NULL` (PK), `Sku NVARCHAR(40) NOT NULL` (PK), `OnHand INT NOT NULL DEFAULT (0)`, `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; composite PK `PK_StockLevel (WarehouseId, Sku)`.
- `dbo.SupplierFeed`: `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` (PK), `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `Quantity INT NOT NULL`, `ReceivedAt DATETIME2(0) NOT NULL`; PK `PK_SupplierFeed`.
- `dbo.SyncLog`: `SyncLogId INT IDENTITY(1,1) NOT NULL` (PK), `StartedAt DATETIME2(0) NOT NULL`, `RowsInserted INT NOT NULL`, `RowsUpdated INT NOT NULL`, `ErrorMessage NVARCHAR(4000) NULL`; PK `PK_SyncLog`.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters on `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`.

#### Writes
- `dbo.StockLevel`: inserts `WarehouseId`, `Sku`, `OnHand`; updates `OnHand`, `UpdatedAt`.
- `dbo.SyncLog`: inserts start time and inserted/updated counts; on failure, inserts zero counts and `ErrorMessage`.

#### Relationships
- Merge match: `dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId`.
- Merge match: `dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku`.
- No foreign key constraints defined.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`: captures UTC start time and aggregates feed quantities received since `@Since` by warehouse and SKU.
- Merges aggregates into `dbo.StockLevel`: adds quantity to matched stock; inserts unmatched stock rows.
- Logs merge action counts in `dbo.SyncLog`; on error, rolls back, logs `ERROR_MESSAGE()`, and rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- `SYSUTCDATETIME()`
- `ERROR_MESSAGE()`
