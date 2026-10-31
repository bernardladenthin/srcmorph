### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:44:26Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, recording insert/update counts and errors.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed quantities.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL, SQL Server)

#### Schema
**dbo.StockLevel**
- `WarehouseId INT NOT NULL`  
- `Sku NVARCHAR(40) NOT NULL`  
- `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`  
- `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`  
- `CONSTRAINT PK_StockLevel PRIMARY KEY (WarehouseId, Sku)`

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
- `dbo.SupplierFeed` (WarehouseId, Sku, Quantity, ReceivedAt)  
- `dbo.StockLevel` (WarehouseId, Sku, OnHand) for merge matching

#### Writes
- `dbo.StockLevel`:  
  - `INSERT`: 1 row per distinct (WarehouseId, Sku) not present  
  - `UPDATE`: 1 row per matched (WarehouseId, Sku)  
- `dbo.SyncLog`:  
  - `INSERT`: 1 row per sync run (success or error)

#### Relationships
- `dbo.StockLevel.WarehouseId, Sku` ← `dbo.SupplierFeed.WarehouseId, Sku` (used in merge)

#### Routine logic (dbo.usp_SyncStock)
- Parameters: `@Since DATETIME2(0)`
- Start timestamp `@Started = SYSUTCDATETIME()`
- Temporary table `@Changes` captures merge actions
- Transaction:
  1. `MERGE` `dbo.StockLevel` with aggregated `dbo.SupplierFeed` rows where `ReceivedAt >= @Since`
     - `WHEN MATCHED`: `UPDATE` `OnHand += source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`
     - `WHEN NOT MATCHED BY TARGET`: `INSERT` new row with `OnHand = source.Quantity`
     - `OUTPUT $action` into `@Changes`
  2. `INSERT` into `dbo.SyncLog` with counts of `INSERT` and `UPDATE` from `@Changes`
  3. Commit
- Error handling: rollback, log error message, rethrow

#### Dependencies
- `dbo.StockLevel`  
- `dbo.SupplierFeed`  
- `dbo.SyncLog`  
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
