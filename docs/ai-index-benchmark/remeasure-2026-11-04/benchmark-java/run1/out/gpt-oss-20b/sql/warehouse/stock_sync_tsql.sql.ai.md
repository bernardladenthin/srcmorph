### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:17:26Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed quantities.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL, SQL Server)

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
- `dbo.SupplierFeed` (columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`)
- `dbo.StockLevel` (columns: `WarehouseId`, `Sku`, `OnHand`)

#### Writes
- `dbo.StockLevel`: `MERGE` updates or inserts aggregated rows.
- `dbo.SyncLog`: one row per sync run; `RowsInserted` and `RowsUpdated` derived from merge output.

#### Relationships
- None defined via foreign keys; logical join in `MERGE`:
  - `dbo.StockLevel.WarehouseId, Sku -> dbo.SupplierFeed.WarehouseId, Sku`

#### Routine logic
- **dbo.usp_SyncStock(@Since DATETIME2(0))**
  - Start transaction.
  - Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId, Sku`.
  - `MERGE` into `StockLevel`:
    - `WHEN MATCHED`: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
    - `WHEN NOT MATCHED BY TARGET`: insert new row with `OnHand = Quantity`.
  - Capture merge actions (`INSERT`/`UPDATE`) into table variable `@Changes`.
  - Insert a log row into `SyncLog` with counts of inserts and updates.
  - Commit; on error rollback and log error message.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- Transaction control: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`
