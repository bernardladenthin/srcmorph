### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:20:23Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed quantities.
- Logs each sync run in `dbo.SyncLog`.

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
- `dbo.SupplierFeed` (columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`)
- `dbo.StockLevel` (columns: `WarehouseId`, `Sku`, `OnHand`)

#### Writes
- `dbo.StockLevel`
  - `UPDATE` on matched rows: `OnHand`, `UpdatedAt`
  - `INSERT` on non‑matched rows: `WarehouseId`, `Sku`, `OnHand`
- `dbo.SyncLog`
  - `INSERT` one row per sync run: `StartedAt`, `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`

#### Relationships
- `dbo.StockLevel (WarehouseId, Sku) -> dbo.SupplierFeed (WarehouseId, Sku)` (used in MERGE join)

#### Routine logic (dbo.usp_SyncStock)
- Parameters: `@Since DATETIME2(0)`
- Begin transaction
- Aggregate `dbo.SupplierFeed` rows received since `@Since` by `WarehouseId, Sku` → `source`
- MERGE `source` into `dbo.StockLevel`:
  - `WHEN MATCHED`: `OnHand += source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`
  - `WHEN NOT MATCHED BY TARGET`: `INSERT (WarehouseId, Sku, OnHand) = (source.WarehouseId, source.Sku, source.Quantity)`
  - Capture `$action` into `@Changes`
- Insert into `dbo.SyncLog`:
  - `StartedAt = @Started`
  - `RowsInserted = count of 'INSERT' actions`
  - `RowsUpdated = count of 'UPDATE' actions`
- Commit transaction
- On error: rollback, insert error message into `dbo.SyncLog`, rethrow

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- Transaction control: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`
