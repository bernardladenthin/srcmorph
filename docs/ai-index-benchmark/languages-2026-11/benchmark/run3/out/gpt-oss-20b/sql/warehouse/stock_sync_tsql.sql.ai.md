### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:05:31Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, recording changes and errors.

#### Purpose
- Merge supplier feed into current stock levels.
- Log number of rows inserted/updated and any errors.

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
- `dbo.SupplierFeed` columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
- `dbo.StockLevel` columns: `WarehouseId`, `Sku`, `OnHand`

#### Writes
- `dbo.StockLevel`: `OnHand`, `UpdatedAt` (UPDATE), `WarehouseId`, `Sku`, `OnHand` (INSERT)  
- `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` (INSERT)

#### Relationships
- None defined (no foreign keys).

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Start transaction.
- Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId` and `Sku`.
- MERGE into `StockLevel`:
  - If matched: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
  - If not matched: insert new row with `OnHand = Quantity`.
  - Capture `$action` into `@Changes`.
- Insert into `SyncLog`:
  - `StartedAt` = current UTC.
  - `RowsInserted` = count of `INSERT` actions.
  - `RowsUpdated` = count of `UPDATE` actions.
- Commit transaction.
- On error: rollback, insert error message into `SyncLog`, rethrow.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variable: `@@TRANCOUNT`
