### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:14:41Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, recording changes and errors.

#### Purpose
- Updates `StockLevel` with aggregated supplier feed quantities.
- Logs each sync run in `SyncLog`.

#### Type
- Table: `dbo.StockLevel` (SQL Server, T‑SQL)
- Table: `dbo.SupplierFeed` (SQL Server, T‑SQL)
- Table: `dbo.SyncLog` (SQL Server, T‑SQL)
- Procedure: `dbo.usp_SyncStock` (SQL Server, T‑SQL)

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
- `dbo.SupplierFeed` (WHERE `ReceivedAt >= @Since`, GROUP BY `WarehouseId, Sku`)
- `dbo.StockLevel` (for matching rows in MERGE)

#### Writes
- `dbo.StockLevel` (UPDATE or INSERT via MERGE)
- `dbo.SyncLog` (INSERT of sync result or error)

#### Relationships
- `dbo.StockLevel.WarehouseId, Sku` ↔ `dbo.SupplierFeed.WarehouseId, Sku` (used in MERGE join)

#### Routine logic
- **Parameters**: `@Since DATETIME2(0)`
- **Steps**:
  1. Capture start time `@Started`.
  2. Aggregate `SupplierFeed` rows since `@Since` by `WarehouseId, Sku` → `Quantity`.
  3. MERGE into `StockLevel`:
     - UPDATE `OnHand` = `OnHand` + `Quantity`, set `UpdatedAt`.
     - INSERT new rows with `OnHand = Quantity`.
  4. Capture merge actions in `@Changes`.
  5. Insert a log row into `SyncLog` with counts of INSERT and UPDATE actions.
  6. Commit transaction; on error rollback, log error, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- Transaction control (`BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`)
