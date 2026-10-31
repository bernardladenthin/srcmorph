### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:17:42Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, logging the results.

#### Purpose
- Updates `StockLevel` with aggregated supplier quantities.
- Records sync statistics and errors in `SyncLog`.

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
- `dbo.SupplierFeed` columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
- `dbo.StockLevel` columns: `WarehouseId`, `Sku`, `OnHand`

#### Writes
- `dbo.StockLevel`: `OnHand`, `UpdatedAt` (UPDATE), `WarehouseId`, `Sku`, `OnHand` (INSERT)
- `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` (INSERT)

#### Relationships
- None defined via foreign keys; logical join in `MERGE` on `WarehouseId` and `Sku`.

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Capture start time `@Started`.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId`/`Sku`.
  3. `MERGE` into `StockLevel`:
     - If match: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
     - If no match: insert new row with `OnHand = Quantity`.
  4. Capture merge actions into `@Changes`.
  5. Insert a `SyncLog` record with counts of INSERT and UPDATE actions.
  6. Commit transaction; on error rollback, log error, rethrow.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- Transaction control: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`
