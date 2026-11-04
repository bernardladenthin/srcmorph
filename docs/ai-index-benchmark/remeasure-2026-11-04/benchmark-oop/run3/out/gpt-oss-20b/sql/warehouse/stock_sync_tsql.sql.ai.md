### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:01Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Synchronises supplier stock feed into the warehouse stock level table and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed data.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- **Table**: `dbo.StockLevel` (SQL Server T‑SQL)
- **Table**: `dbo.SupplierFeed` (SQL Server T‑SQL)
- **Table**: `dbo.SyncLog` (SQL Server T‑SQL)
- **Procedure**: `dbo.usp_SyncStock` (SQL Server T‑SQL)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `OnHand INT NOT NULL DEFAULT 0`  
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()`  
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
- `dbo.SupplierFeed` – `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `@Since`).
- `dbo.StockLevel` – `WarehouseId`, `Sku`, `OnHand` (for merge match).

#### Writes
- `dbo.StockLevel` – `OnHand`, `UpdatedAt` (UPDATE) or new rows (INSERT).
- `dbo.SyncLog` – one row per sync run (`StartedAt`, `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`).

#### Relationships
- None (no foreign keys defined).

#### Routine logic
- **Parameters**: `@Since DATETIME2(0)`
- **Steps**:
  1. Capture start time `@Started`.
  2. Aggregate `SupplierFeed` rows since `@Since` by `WarehouseId, Sku` → `Quantity`.
  3. `MERGE` into `StockLevel`:
     - `WHEN MATCHED`: add `Quantity` to `OnHand`, update `UpdatedAt`.
     - `WHEN NOT MATCHED`: insert new row with `OnHand = Quantity`.
  4. Capture merge actions in table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of INSERT and UPDATE actions.
  6. Commit transaction; on error rollback, log error, rethrow.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Functions: `SYSUTCDATETIME()`.
- System objects: `@@TRANCOUNT`, `ERROR_MESSAGE()`.
