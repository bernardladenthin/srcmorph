### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:51:46Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed quantities since a given timestamp.
- Logs each sync run in `dbo.SyncLog`.

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
- PK: `PK_StockLevel (WarehouseId, Sku)`

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
- `dbo.StockLevel` (WarehouseId, Sku, OnHand) via MERGE

#### Writes
- `dbo.StockLevel` (OnHand, UpdatedAt) via UPDATE or INSERT  
- `dbo.SyncLog` (StartedAt, RowsInserted, RowsUpdated, ErrorMessage)

#### Relationships
- `dbo.StockLevel.WarehouseId, Sku` ↔ `dbo.SupplierFeed.WarehouseId, Sku` (used in MERGE join)

#### Routine logic
**dbo.usp_SyncStock(@Since DATETIME2(0))**
- Begin transaction.
- Aggregate `SupplierFeed` rows where `ReceivedAt >= @Since` by `WarehouseId, Sku`.
- MERGE into `StockLevel`:
  - If matched: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
  - If not matched: insert new row with `OnHand = Quantity`.
- Capture `$action` (INSERT/UPDATE) into table variable `@Changes`.
- Insert a log row into `SyncLog` with counts of inserts and updates.
- Commit transaction; on error, rollback, log error, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System objects: `@@TRANCOUNT`
