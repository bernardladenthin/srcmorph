### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:31Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Synchronizes warehouse stock quantities from newly received supplier-feed rows and records the outcome.

#### Purpose
- Aggregate supplier quantities received since a cutoff and apply them to warehouse SKU stock levels.
- Log inserted/updated stock counts or the synchronization error.

#### Type
- Tables and procedure; Microsoft SQL Server (T-SQL).
- Procedure: `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)` — constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint `PK_StockLevel` on `(WarehouseId, Sku)`.
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
- Primary-key indexes: `PK_StockLevel` on `dbo.StockLevel (WarehouseId, Sku)`; `PK_SupplierFeed` on `dbo.SupplierFeed (FeedRowId)`; `PK_SyncLog` on `dbo.SyncLog (SyncLogId)`.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by `WarehouseId, Sku`, sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku` for matching; `OnHand` for quantity accumulation.
- `@Changes`: `MergeAction` for inserted/updated counts.

#### Writes
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` on unmatched rows; updates `OnHand`, `UpdatedAt` on matched rows.
- `dbo.SyncLog`: inserts `StartedAt`, `RowsInserted`, `RowsUpdated` on success; inserts those columns plus `ErrorMessage` on failure.

#### Relationships
- `dbo.StockLevel.WarehouseId -> source.WarehouseId` and `dbo.StockLevel.Sku -> source.Sku` (MERGE match; `source` is grouped `dbo.SupplierFeed` data).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; begins a transaction.
  - Aggregates feed quantities received since `@Since` by warehouse and SKU.
  - MERGEs aggregates into `dbo.StockLevel`: adds quantities to matching stock, inserts new stock rows otherwise; captures actions.
  - Logs the number of inserted and updated stock rows, then commits.
  - On error, rolls back any active transaction, logs `ERROR_MESSAGE()` with zero counts, and rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
