### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:32:18Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, logging the result.

#### Purpose
- Merge supplier feed rows into current stock levels.
- Record the number of rows inserted/updated and any errors.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL)

#### Schema
**dbo.StockLevel**
- `WarehouseId INT NOT NULL`  
- `Sku NVARCHAR(40) NOT NULL`  
- `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`  
- `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`  
- PK `PK_StockLevel (WarehouseId, Sku)`

**dbo.SupplierFeed**
- `FeedRowId BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY`
- `WarehouseId INT NOT NULL`
- `Sku NVARCHAR(40) NOT NULL`
- `Quantity INT NOT NULL`
- `ReceivedAt DATETIME2(0) NOT NULL`

**dbo.SyncLog**
- `SyncLogId INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY`
- `StartedAt DATETIME2(0) NOT NULL`
- `RowsInserted INT NOT NULL`
- `RowsUpdated INT NOT NULL`
- `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed` (WarehouseId, Sku, Quantity, ReceivedAt) – filtered by `ReceivedAt >= @Since`
- `dbo.StockLevel` (WarehouseId, Sku, OnHand) – for merge matching

#### Writes
- `dbo.StockLevel` – `UPDATE` or `INSERT` per merge
- `dbo.SyncLog` – one row per sync, with counts of inserted/updated rows or error message

#### Relationships
- None (no foreign keys defined)

#### Routine logic
- `usp_SyncStock(@Since DATETIME2(0))`
  - Start transaction.
  - Aggregate `SupplierFeed` rows since `@Since` by `WarehouseId, Sku`.
  - Merge into `StockLevel`:
    - `UPDATE` existing rows: `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
    - `INSERT` new rows: `OnHand = Quantity`.
  - Capture merge actions in table variable `@Changes`.
  - Insert a log row into `SyncLog` with counts of `INSERT` and `UPDATE` actions.
  - Commit transaction; on error, rollback, log error, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variable: `@@TRANCOUNT`
