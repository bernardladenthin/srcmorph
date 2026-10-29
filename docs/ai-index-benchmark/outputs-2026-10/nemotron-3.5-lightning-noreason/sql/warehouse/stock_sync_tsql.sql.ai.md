### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:53:51Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, merging new and updated quantities and logging the result.

#### Purpose
- Merges supplier feed changes into StockLevel since a given timestamp
- Logs insert/update counts and errors into SyncLog

#### Type
Procedure (T-SQL)

#### Schema
- **StockLevel**: `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `OnHand INT NOT NULL DEFAULT (0)`, `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; PK: `PK_StockLevel` on `(WarehouseId, Sku)`
- **SupplierFeed**: `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`, `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `Quantity INT NOT NULL`, `ReceivedAt DATETIME2(0) NOT NULL`; PK: `PK_SupplierFeed` on `FeedRowId`
- **SyncLog**: `SyncLogId INT IDENTITY(1,1) NOT NULL`, `StartedAt DATETIME2(0) NOT NULL`, `RowsInserted INT NOT NULL`, `RowsUpdated INT NOT NULL`, `ErrorMessage NVARCHAR(4000) NULL`; PK: `PK_SyncLog` on `SyncLogId`

#### Reads
- `dbo.SupplierFeed` (WHERE `ReceivedAt >= @Since`, GROUP BY `WarehouseId`, `Sku`)
- `dbo.StockLevel` (ON `WarehouseId`, `Sku` merge join)

#### Writes
- `dbo.StockLevel` (WHEN MATCHED UPDATE `OnHand`, `UpdatedAt`; WHEN NOT MATCHED BY TARGET INSERT `WarehouseId`, `Sku`, `OnHand`)
- `dbo.SyncLog` (INSERT `StartedAt`, `RowsInserted`, `RowsUpdated`; INSERT `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` on error)

#### Relationships
- `StockLevel.WarehouseId` → `SupplierFeed.WarehouseId`
- `StockLevel.Sku` → `SupplierFeed.Sku`

#### Routine logic
- Declares `@Started` and table variable `@Changes` for merge actions
- Executes MERGE on `StockLevel` using aggregated `SupplierFeed` where `ReceivedAt >= @Since`
- Captures `$action` into `@Changes`
- Inserts aggregated insert/update counts into `SyncLog`
- On error: rolls back transaction, inserts error row into `SyncLog`, re-throws

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System function `SYSUTCDATETIME()`
