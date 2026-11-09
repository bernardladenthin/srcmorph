### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:10:49Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Synchronises nightly supplier stock feed into the warehouse inventory, recording changes and errors.

#### Purpose
- Updates `StockLevel` with aggregated supplier quantities.
- Logs each sync run in `SyncLog`.

#### Type
- **Table**: `dbo.StockLevel` (PostgreSQL dialect implied by T‑SQL syntax).
- **Table**: `dbo.SupplierFeed`.
- **Table**: `dbo.SyncLog`.
- **Procedure**: `dbo.usp_SyncStock` (T‑SQL).

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
- `dbo.SupplierFeed` (columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`).
- `dbo.StockLevel` (columns: `WarehouseId`, `Sku`, `OnHand`).

#### Writes
- `dbo.StockLevel`: `INSERT` (new rows) and `UPDATE` (existing rows) via `MERGE`.
- `dbo.SyncLog`: `INSERT` (one row per sync run).

#### Relationships
- None defined via foreign keys.

#### Routine logic
- **Parameters**: `@Since DATETIME2(0)`.
- **Steps**:
  1. Capture start time `@Started`.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId` and `Sku`.
  3. `MERGE` into `StockLevel`:
     - `UPDATE` existing rows: `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
     - `INSERT` new rows: `OnHand = Quantity`.
  4. Record merge actions in table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of inserts and updates.
  6. Commit transaction; on error rollback and log error.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
