### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:32Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock quantities from supplier feed rows received since a supplied timestamp and records the synchronization outcome.

#### Purpose
- Aggregates recent `dbo.SupplierFeed` quantities by warehouse and SKU, then adds them to or inserts them into `dbo.StockLevel`.
- Records inserted and updated row counts, or an error message, in `dbo.SyncLog`.

#### Type
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock`
- Dialect: T-SQL (SQL Server)

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT` NOT NULL
  - `Sku NVARCHAR(40)` NOT NULL
  - `OnHand INT` NOT NULL DEFAULT `(0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0)` NOT NULL DEFAULT `(SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint `PK_StockLevel` on `(WarehouseId, Sku)`; primary-key index
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1, 1)` NOT NULL; primary key constraint `PK_SupplierFeed`, primary-key index
  - `WarehouseId INT` NOT NULL
  - `Sku NVARCHAR(40)` NOT NULL
  - `Quantity INT` NOT NULL
  - `ReceivedAt DATETIME2(0)` NOT NULL
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1, 1)` NOT NULL; primary key constraint `PK_SyncLog`, primary-key index
  - `StartedAt DATETIME2(0)` NOT NULL
  - `RowsInserted INT` NOT NULL
  - `RowsUpdated INT` NOT NULL
  - `ErrorMessage NVARCHAR(4000)` NULL

#### Reads
- `dbo.usp_SyncStock`
  - `dbo.SupplierFeed`: reads `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters on `ReceivedAt >= @Since` and groups by `WarehouseId`, `Sku`.
  - `dbo.StockLevel`: reads `WarehouseId`, `Sku`, `OnHand` in the `MERGE`.
  - `@Changes`: reads `MergeAction` to calculate insert and update counts.

#### Writes
- `dbo.usp_SyncStock`
  - `dbo.StockLevel`: `MERGE` inserts unmatched `(WarehouseId, Sku, OnHand)` rows and updates matched `OnHand`, `UpdatedAt`; inserted-row count depends on source data.
  - `dbo.SyncLog`: inserts one aggregate outcome row on success; inserts one error row on failure.
  - `@Changes`: captures one `$action` value per `MERGE` change.

#### Relationships
- `dbo.StockLevel.WarehouseId` and `dbo.StockLevel.Sku` match `dbo.SupplierFeed.WarehouseId` and `dbo.SupplierFeed.Sku` in the `MERGE`.
- No declared foreign keys.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures start time in `@Started`; initializes table variable `@Changes`.
  - Within a transaction, aggregates feed quantities received since `@Since` by warehouse and SKU.
  - Adds aggregate quantities to matching stock levels; inserts stock levels for unmatched warehouse/SKU pairs.
  - Logs counts from `@Changes` and commits.
  - On error, rolls back if a transaction is active, logs `ERROR_MESSAGE()` with zero counts, then rethrows.
