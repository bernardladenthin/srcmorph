### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:02:42Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, recording changes and errors.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier quantities.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL, SQL Server)

#### Schema
**dbo.StockLevel**
- `WarehouseId INT NOT NULL`  
- `Sku NVARCHAR(40) NOT NULL`  
- `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`  
- `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`  
- Primary key: `PK_StockLevel (WarehouseId, Sku)`

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
- `dbo.SupplierFeed` columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `@Since`).
- `dbo.StockLevel` columns: `WarehouseId`, `Sku`, `OnHand` (for update).

#### Writes
- `dbo.StockLevel`: up to one row per `(WarehouseId, Sku)` per sync; `OnHand` updated or new row inserted.
- `dbo.SyncLog`: one row per sync run; `RowsInserted` and `RowsUpdated` derived from merge output; `ErrorMessage` on failure.

#### Relationships
- None defined; `dbo.StockLevel` and `dbo.SupplierFeed` share `WarehouseId` and `Sku` but no FK constraints.

#### Routine logic
- `usp_SyncStock(@Since DATETIME2(0))`
  - Starts transaction.
  - Aggregates `SupplierFeed` rows received since `@Since` by `(WarehouseId, Sku)`.
  - MERGE into `StockLevel`:
    - `WHEN MATCHED`: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
    - `WHEN NOT MATCHED BY TARGET`: insert new row with `OnHand = Quantity`.
  - Captures merge actions into `@Changes`.
  - Inserts a log row into `SyncLog` with counts of INSERT and UPDATE actions.
  - Commits transaction; on error rolls back, logs error, rethrows.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variable: `@@TRANCOUNT`
