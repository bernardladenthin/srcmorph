### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:26:27Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, recording changes and errors.

#### Purpose
- Updates `StockLevel` with aggregated supplier feed quantities.
- Logs each sync run in `SyncLog`.

#### Type
- **Table**: `dbo.StockLevel` (SQL Server, T‑SQL)
- **Table**: `dbo.SupplierFeed` (SQL Server, T‑SQL)
- **Table**: `dbo.SyncLog` (SQL Server, T‑SQL)
- **Procedure**: `dbo.usp_SyncStock` (SQL Server, T‑SQL)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `OnHand INT NOT NULL DEFAULT (0)`  
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`  
  - **PK**: `PK_StockLevel (WarehouseId, Sku)`

- **dbo.SupplierFeed**
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL`  
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `Quantity INT NOT NULL`  
  - `ReceivedAt DATETIME2(0) NOT NULL`  
  - **PK**: `PK_SupplierFeed (FeedRowId)`

- **dbo.SyncLog**
  - `SyncLogId INT IDENTITY(1,1) NOT NULL`  
  - `StartedAt DATETIME2(0) NOT NULL`  
  - `RowsInserted INT NOT NULL`  
  - `RowsUpdated INT NOT NULL`  
  - `ErrorMessage NVARCHAR(4000) NULL`  
  - **PK**: `PK_SyncLog (SyncLogId)`

#### Reads
- `dbo.SupplierFeed` (columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`)
- `dbo.StockLevel` (columns: `WarehouseId`, `Sku`, `OnHand`)

#### Writes
- `dbo.StockLevel`: `INSERT` (new rows) and `UPDATE` (existing rows) via `MERGE`.
- `dbo.SyncLog`: `INSERT` (one row per sync run), with counts of inserted/updated rows or error message.

#### Relationships
- `dbo.StockLevel (WarehouseId, Sku)` ↔ `dbo.SupplierFeed (WarehouseId, Sku)` (used in `MERGE` join).

#### Routine logic (`dbo.usp_SyncStock`)
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Start transaction.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId` and `Sku`.
  3. `MERGE` into `StockLevel`:
     - `WHEN MATCHED`: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
     - `WHEN NOT MATCHED BY TARGET`: insert new row with `OnHand = Quantity`.
  4. Capture merge actions (`INSERT`/`UPDATE`) into table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of inserted and updated rows.
  6. Commit transaction.
  7. On error: rollback, insert error log row, rethrow.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`  
- System objects: `IDENTITY`, `MERGE`, `OUTPUT`.
