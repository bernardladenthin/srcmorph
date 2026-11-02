### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:47:37Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed quantities.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel` (SQL Server, T‑SQL)
- Table: `dbo.SupplierFeed` (SQL Server, T‑SQL)
- Table: `dbo.SyncLog` (SQL Server, T‑SQL)
- Procedure: `dbo.usp_SyncStock` (SQL Server, T‑SQL)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `OnHand INT NOT NULL DEFAULT 0`  
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()`  
  - PK: `PK_StockLevel (WarehouseId, Sku)`

- **dbo.SupplierFeed**
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`  
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `Quantity INT NOT NULL`  
  - `ReceivedAt DATETIME2(0) NOT NULL`  
  - PK: `PK_SupplierFeed (FeedRowId)`

- **dbo.SyncLog**
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`  
  - `StartedAt DATETIME2(0) NOT NULL`  
  - `RowsInserted INT NOT NULL`  
  - `RowsUpdated INT NOT NULL`  
  - `ErrorMessage NVARCHAR(4000) NULL`  
  - PK: `PK_SyncLog (SyncLogId)`

#### Reads
- `dbo.SupplierFeed` columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
- `dbo.StockLevel` columns: `WarehouseId`, `Sku`, `OnHand`

#### Writes
- `dbo.StockLevel`: `OnHand`, `UpdatedAt` (UPDATE), `WarehouseId`, `Sku`, `OnHand` (INSERT)
- `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` (INSERT)

#### Relationships
- None defined via foreign keys.

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Begin transaction.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId, Sku`.
  3. MERGE into `StockLevel`:
     - UPDATE `OnHand` += aggregated `Quantity`, set `UpdatedAt`.
     - INSERT new rows with `OnHand` = aggregated `Quantity`.
  4. Capture merge actions in table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of INSERT and UPDATE actions.
  6. Commit transaction.
  7. On error, rollback, log error message, rethrow.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variable: `@@TRANCOUNT`
