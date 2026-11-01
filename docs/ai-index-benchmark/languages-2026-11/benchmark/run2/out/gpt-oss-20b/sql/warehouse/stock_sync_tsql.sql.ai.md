### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:42:14Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, logging the number of rows inserted or updated.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed data.
- Records sync statistics and errors in `dbo.SyncLog`.

#### Type
- **Table**: `dbo.StockLevel` (SQL Server, T‑SQL)
- **Table**: `dbo.SupplierFeed` (SQL Server, T‑SQL)
- **Table**: `dbo.SyncLog` (SQL Server, T‑SQL)
- **Procedure**: `dbo.usp_SyncStock` (SQL Server, T‑SQL)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`  
  - `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`  
  - **PK**: `PK_StockLevel (WarehouseId, Sku)`

- **dbo.SupplierFeed**
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`

- **dbo.SyncLog**
  - `SyncLogId INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed` (WHERE `ReceivedAt >= @Since`, GROUP BY `WarehouseId, Sku`)
- `dbo.StockLevel` (for matching rows in MERGE)

#### Writes
- `dbo.StockLevel`
  - `OnHand` updated or inserted per aggregated `Quantity`
- `dbo.SyncLog`
  - One row per sync: `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`

#### Relationships
- None (no foreign keys defined)

#### Routine logic (`dbo.usp_SyncStock`)
- Parameters: `@Since DATETIME2(0)`
- Begin transaction
- MERGE `dbo.StockLevel` with aggregated `dbo.SupplierFeed` since `@Since`
  - `WHEN MATCHED`: `OnHand += source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`
  - `WHEN NOT MATCHED BY TARGET`: `INSERT (WarehouseId, Sku, OnHand) = (source.WarehouseId, source.Sku, source.Quantity)`
  - Capture `$action` into `@Changes`
- Insert into `dbo.SyncLog` counts of `INSERT` and `UPDATE` actions
- Commit transaction
- On error: rollback, insert error into `dbo.SyncLog`, rethrow

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
