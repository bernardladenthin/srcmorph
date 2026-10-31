### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:54:58Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory table and records the operation outcome.

#### Purpose
- Merge supplier feed rows into `StockLevel`.
- Log sync statistics and errors in `SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL, SQL Server)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`  
  - `Sku NVARCHAR(40) NOT NULL`  
  - `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`  
  - `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`  
  - **PK**: `PK_StockLevel (WarehouseId, Sku)`

- **dbo.SupplierFeed**
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY`
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `Quantity INT NOT NULL`
  - `ReceivedAt DATETIME2(0) NOT NULL`

- **dbo.SyncLog**
  - `SyncLogId INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY`
  - `StartedAt DATETIME2(0) NOT NULL`
  - `RowsInserted INT NOT NULL`
  - `RowsUpdated INT NOT NULL`
  - `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed` (columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`)
- `dbo.StockLevel` (columns: `WarehouseId`, `Sku`, `OnHand`)

#### Writes
- `dbo.StockLevel`
  - `OnHand` updated or inserted per `WarehouseId`/`Sku`
- `dbo.SyncLog`
  - One row per sync: `StartedAt`, `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`

#### Relationships
- None (no foreign keys defined).

#### Routine logic
- **Parameters**: `@Since DATETIME2(0)`
- **Steps**:
  1. Capture start time `@Started`.
  2. Begin transaction.
  3. `MERGE` `dbo.StockLevel` with aggregated `SupplierFeed` rows where `ReceivedAt >= @Since`.
     - `WHEN MATCHED`: increment `OnHand`, update `UpdatedAt`.
     - `WHEN NOT MATCHED BY TARGET`: insert new `WarehouseId`, `Sku`, `OnHand`.
  4. Capture merge actions into `@Changes`.
  5. Insert a log row into `SyncLog` with counts of `INSERT` and `UPDATE` actions.
  6. Commit transaction.
  7. On error: rollback, insert error log row, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variables: `@@TRANCOUNT`
