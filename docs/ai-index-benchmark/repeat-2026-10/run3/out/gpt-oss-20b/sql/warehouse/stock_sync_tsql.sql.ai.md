### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:41Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory table and records the operation outcome.

#### Purpose
- Update `StockLevel` with aggregated supplier feed quantities.
- Log sync statistics and errors in `SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL)

#### Schema
**dbo.StockLevel**
- `WarehouseId INT NOT NULL`  
- `Sku NVARCHAR(40) NOT NULL`  
- `OnHand INT NOT NULL DEFAULT (0)`  
- `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`  
- PK: `(WarehouseId, Sku)`

**dbo.SupplierFeed**
- `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`  
- `WarehouseId INT NOT NULL`  
- `Sku NVARCHAR(40) NOT NULL`  
- `Quantity INT NOT NULL`  
- `ReceivedAt DATETIME2(0) NOT NULL`  
- PK: `FeedRowId`

**dbo.SyncLog**
- `SyncLogId INT IDENTITY(1,1) NOT NULL`  
- `StartedAt DATETIME2(0) NOT NULL`  
- `RowsInserted INT NOT NULL`  
- `RowsUpdated INT NOT NULL`  
- `ErrorMessage NVARCHAR(4000) NULL`  
- PK: `SyncLogId`

#### Reads
- `dbo.SupplierFeed` (WarehouseId, Sku, Quantity, ReceivedAt)  
- `dbo.StockLevel` (WarehouseId, Sku, OnHand)  

#### Writes
- `dbo.StockLevel` (INSERT or UPDATE OnHand, UpdatedAt)  
- `dbo.SyncLog` (INSERT rows with sync stats or error)

#### Relationships
- `dbo.StockLevel.WarehouseId, Sku` ↔ `dbo.SupplierFeed.WarehouseId, Sku` (used in MERGE)

#### Routine logic
- **Parameters**: `@Since DATETIME2(0)` – start of feed window.  
- **Steps**:
  1. Begin transaction.  
  2. Aggregate `SupplierFeed` rows by `WarehouseId, Sku` where `ReceivedAt >= @Since`.  
  3. MERGE into `StockLevel`:  
     - If matched: `OnHand += Quantity`, update `UpdatedAt`.  
     - If not matched: insert new row with `OnHand = Quantity`.  
  4. Capture `$action` into `@Changes`.  
  5. Insert sync statistics into `SyncLog` (rows inserted/updated).  
  6. Commit transaction.  
  7. On error: rollback, log error in `SyncLog`, rethrow.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`  
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`  
- Transaction control: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`
