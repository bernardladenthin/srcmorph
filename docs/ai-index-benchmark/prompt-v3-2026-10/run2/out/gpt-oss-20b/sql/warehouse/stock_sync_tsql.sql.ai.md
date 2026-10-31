### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:20:12Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed data.
- Records sync statistics and errors in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL, SQL Server)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`  
  - `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`  
  - **PK** `PK_StockLevel (WarehouseId, Sku)`

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

#### Writes
- `dbo.StockLevel`  
  - `OnHand` updated or inserted per aggregated `Quantity`  
- `dbo.SyncLog`  
  - One row per sync: `StartedAt`, `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`

#### Relationships
- None (no foreign keys defined)

#### Routine logic
- **dbo.usp_SyncStock(@Since DATETIME2(0))**
  - Start transaction.
  - Aggregate `SupplierFeed` rows where `ReceivedAt >= @Since` by `WarehouseId, Sku`.
  - `MERGE` into `StockLevel`:
    - `WHEN MATCHED`: increment `OnHand`, update `UpdatedAt`.
    - `WHEN NOT MATCHED BY TARGET`: insert new row with `OnHand = Quantity`.
  - Capture `$action` into `@Changes`.
  - Insert sync summary into `SyncLog` with counts of `INSERT` and `UPDATE`.
  - Commit; on error, rollback, log error, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variable: `@@TRANCOUNT`
