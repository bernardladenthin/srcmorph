### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:25Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Aggregates recent supplier quantities by warehouse and SKU, updates current stock levels, and records synchronization outcomes or errors.

#### Purpose
- Synchronizes supplier-feed quantities received since a cutoff into warehouse stock balances.
- Logs inserted and updated stock counts, or the error from a failed synchronization.

#### Type
- Tables and procedure; SQL Server (T-SQL).
- Procedure: `dbo.usp_SyncStock`

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL` — composite primary key
  - `Sku NVARCHAR(40) NOT NULL` — composite primary key
  - `OnHand INT NOT NULL DEFAULT (0)` — constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())` — constraint `DF_StockLevel_UpdatedAt`
  - Constraint `PK_StockLevel`: primary key (`WarehouseId`, `Sku`)
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — primary key
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
  - Constraint `PK_SupplierFeed`: primary key (`FeedRowId`)
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — primary key
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
  - Constraint `PK_SyncLog`: primary key (`SyncLogId`)

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`; filters by `ReceivedAt >= @Since`, groups by `WarehouseId`, `Sku`, sums `Quantity`.
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` for MERGE matching and matched-row updates.
- `@Changes`: `MergeAction` for inserted/updated counts.

#### Writes
- `dbo.StockLevel` via MERGE:
  - Matched rows: updates `OnHand`, `UpdatedAt`.
  - Unmatched rows: inserts `WarehouseId`, `Sku`, `OnHand`.
- `dbo.SyncLog` on success: inserts `StartedAt`, `RowsInserted`, `RowsUpdated` from merge-action counts.
- `dbo.SyncLog` on failure: inserts one VALUES tuple with `StartedAt`, zero counts, and `ErrorMessage`; rethrows the error.

#### Relationships
- `dbo.StockLevel.WarehouseId -> source.WarehouseId` and `dbo.StockLevel.Sku -> source.Sku` — MERGE matching against aggregated `dbo.SupplierFeed`.

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`:
  - Captures the UTC start time and initializes merge-action tracking.
  - In a transaction, aggregates feed quantities since `@Since` and merges them into `dbo.StockLevel`.
  - Records counts of inserted and updated stock rows in `dbo.SyncLog`, then commits.
  - On error, rolls back any active transaction, logs the error, and rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
