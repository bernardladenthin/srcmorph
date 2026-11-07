### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:51:46Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
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
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`
  - `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`
  - PK: `PK_StockLevel (WarehouseId, Sku)`

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
- `dbo.SupplierFeed` columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
- `dbo.StockLevel` columns: `WarehouseId`, `Sku`, `OnHand`

#### Writes
- `dbo.StockLevel`
  - `UPDATE` rows: number equal to distinct `(WarehouseId, Sku)` in source that match target.
  - `INSERT` rows: number equal to distinct `(WarehouseId, Sku)` in source that do not match target.
- `dbo.SyncLog`
  - One row per sync: `StartedAt`, `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`.

#### Relationships
- None defined via foreign keys.

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Begin transaction.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `(WarehouseId, Sku)` → `Quantity`.
  3. `MERGE` into `StockLevel`:
     - `WHEN MATCHED`: `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
     - `WHEN NOT MATCHED BY TARGET`: `INSERT (WarehouseId, Sku, OnHand)`.
  4. Capture `$action` into `@Changes`.
  5. Insert into `SyncLog` the counts of `INSERT` and `UPDATE` actions.
  6. Commit; on error rollback, log error, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variable: `@@TRANCOUNT`
