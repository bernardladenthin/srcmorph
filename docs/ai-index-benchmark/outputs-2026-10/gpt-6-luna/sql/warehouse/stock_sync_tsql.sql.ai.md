### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:21:43Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates recent supplier-feed quantities by warehouse and SKU, updates inventory levels, and records synchronization outcomes.

#### Purpose
- Synchronizes `dbo.StockLevel` from supplier quantities received since a cutoff.
- Logs inserted and updated inventory counts, or an error message, in `dbo.SyncLog`.

#### Type
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure: `dbo.usp_SyncStock`
- Dialect: SQL Server (T-SQL)

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT` NOT NULL; primary-key column
  - `Sku NVARCHAR(40)` NOT NULL; primary-key column
  - `OnHand INT` NOT NULL DEFAULT `(0)`
  - `UpdatedAt DATETIME2(0)` NOT NULL DEFAULT `(SYSUTCDATETIME())`
  - Primary key: `PK_StockLevel (WarehouseId, Sku)`
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT` IDENTITY(1,1) NOT NULL; primary key `PK_SupplierFeed`
  - `WarehouseId INT` NOT NULL
  - `Sku NVARCHAR(40)` NOT NULL
  - `Quantity INT` NOT NULL
  - `ReceivedAt DATETIME2(0)` NOT NULL
- `dbo.SyncLog`
  - `SyncLogId INT` IDENTITY(1,1) NOT NULL; primary key `PK_SyncLog`
  - `StartedAt DATETIME2(0)` NOT NULL
  - `RowsInserted INT` NOT NULL
  - `RowsUpdated INT` NOT NULL
  - `ErrorMessage NVARCHAR(4000)` NULL

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` for merge matching and update arithmetic.
- `@Changes` table variable: `MergeAction` for inserted/updated counts.

#### Writes
- `dbo.StockLevel`: `OnHand`, `UpdatedAt` on matched rows; inserts `WarehouseId`, `Sku`, `OnHand` for unmatched rows.
- `dbo.SyncLog`: inserts `StartedAt`, `RowsInserted`, `RowsUpdated`; error path also inserts `ErrorMessage`.

#### Relationships
- Merge match: `dbo.StockLevel.WarehouseId -> source.WarehouseId`; `dbo.StockLevel.Sku -> source.Sku`
- Source values are aggregated from `dbo.SupplierFeed` by `WarehouseId`, `Sku`; no declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Captures UTC start time; begins transaction and aggregates feed quantities received since `@Since`.
- Merges aggregates into `dbo.StockLevel`: adds quantity and refreshes `UpdatedAt` for matches; inserts new warehouse/SKU levels otherwise.
- Counts merge actions and inserts a success row into `dbo.SyncLog`; on error, rolls back, logs `ERROR_MESSAGE()`, then rethrows.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
