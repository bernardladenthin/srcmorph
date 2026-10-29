### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:22:26Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates supplier inventory updates by warehouse and SKU, applies them to on-hand stock, and records synchronization counts or errors.

#### Purpose
- Synchronizes supplier feed quantities received since a supplied timestamp into warehouse stock levels.
- Logs inserted and updated stock-row counts; records errors before rethrowing them.

#### Type
- SQL Server (T-SQL): tables `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`; procedure `dbo.usp_SyncStock`

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` — primary key, composite
  - `Sku NVARCHAR(40) NOT NULL` — primary key, composite
  - `OnHand INT NOT NULL DEFAULT (0)`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`
  - Primary key: `PK_StockLevel (WarehouseId, Sku)`
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL` — primary key
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
  - Primary key: `PK_SupplierFeed (FeedRowId)`
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1, 1) NOT NULL` — primary key
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
  - Primary key: `PK_SyncLog (SyncLogId)`

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters on `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` for merge matching and updates.
- Procedure-local `@Changes`: `MergeAction` for insert/update counts.

#### Writes
- `dbo.StockLevel`: inserts new `(WarehouseId, Sku, OnHand)` rows; updates `OnHand` and `UpdatedAt` for matched rows.
- `dbo.SyncLog`: inserts synchronization start time and row counts on success; inserts zero counts and `ERROR_MESSAGE()` on failure.

#### Relationships
- `dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId` — merge matching also uses `Sku`.
- `dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku` — merge matching uses both `WarehouseId` and `Sku`.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
  - Captures UTC start time; begins a transaction.
  - Aggregates feed quantities received since `@Since` by warehouse and SKU.
  - Merges aggregates into `dbo.StockLevel`: adds quantity to matched stock or inserts a new stock row.
  - Captures merge actions and logs insert/update counts to `dbo.SyncLog`; commits.
  - On error, rolls back if a transaction is active, logs `ERROR_MESSAGE()` with zero counts, and rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
