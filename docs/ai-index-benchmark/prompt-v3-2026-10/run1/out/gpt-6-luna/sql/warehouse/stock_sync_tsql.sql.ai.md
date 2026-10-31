### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:21Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock quantities from supplier feed rows received since a cutoff and records the synchronization outcome.

#### Purpose
- Aggregate recent supplier quantities by warehouse and SKU, then add them to existing stock or create new stock levels.
- Log inserted and updated row counts, or the error if synchronization fails.

#### Type
- SQL Server (T-SQL): tables and stored procedure.
- Procedure: `dbo.usp_SyncStock`

#### Schema
- `dbo.StockLevel`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`; default constraint `DF_StockLevel_OnHand`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`; default constraint `DF_StockLevel_UpdatedAt`
  - Primary key constraint `PK_StockLevel` on `(WarehouseId, Sku)`
  - Index `PK_StockLevel` on `(WarehouseId, Sku)`; primary-key index
- `dbo.SupplierFeed`
  - `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL`; primary key constraint `PK_SupplierFeed`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`
  - Index `PK_SupplierFeed` on `(FeedRowId)`; primary-key index
- `dbo.SyncLog`
  - `SyncLogId INT IDENTITY(1, 1) NOT NULL`; primary key constraint `PK_SyncLog`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`
  - Index `PK_SyncLog` on `(SyncLogId)`; primary-key index

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
- `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand` (match and update)
- `@Changes`: `MergeAction`

#### Writes
- `dbo.StockLevel`: `INSERT` unmatched warehouse/SKU aggregates; `UPDATE` matched rows’ `OnHand` and `UpdatedAt`. Row counts depend on feed data.
- `dbo.SyncLog`: inserts one success log row after the merge, or one error log row on failure.

#### Relationships
- `dbo.StockLevel.WarehouseId` + `dbo.StockLevel.Sku` ↔ `dbo.SupplierFeed.WarehouseId` + `dbo.SupplierFeed.Sku` (MERGE match; no foreign key declared).

#### Routine logic
- `dbo.usp_SyncStock(@Since DATETIME2(0))`
- Records the UTC start time; groups feed rows with `ReceivedAt >= @Since` by warehouse and SKU, summing `Quantity`.
- In a transaction, adds each grouped quantity to matched `StockLevel.OnHand` and updates `UpdatedAt`; inserts unmatched stock rows.
- Counts MERGE `INSERT` and `UPDATE` actions into a success row in `dbo.SyncLog`, then commits.
- On error, rolls back if a transaction is active, logs `ERROR_MESSAGE()` with zero row counts, and rethrows.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
