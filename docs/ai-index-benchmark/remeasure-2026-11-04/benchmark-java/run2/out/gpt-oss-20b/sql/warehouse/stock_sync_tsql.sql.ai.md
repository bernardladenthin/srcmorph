### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:33:55Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory table and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed data.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`
  - `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`
  - Primary key: `PK_StockLevel (WarehouseId, Sku)`

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
  - `UPDATE` on matched rows: `OnHand`, `UpdatedAt`
  - `INSERT` on non‑matched rows: `WarehouseId`, `Sku`, `OnHand`
- `dbo.SyncLog`
  - `INSERT` one row per sync run with `StartedAt`, `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`

#### Relationships
- None (no foreign keys defined).

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Capture start time `@Started`.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId` and `Sku`.
  3. `MERGE` aggregated data into `StockLevel`:
     - `UPDATE` existing rows: add quantity to `OnHand`, set `UpdatedAt`.
     - `INSERT` new rows with `OnHand = quantity`.
  4. Capture merge actions in table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of inserts and updates.
  6. Commit transaction; on error rollback and log error.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variables: `@@TRANCOUNT`
