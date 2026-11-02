### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:48:44Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, logging the result.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier quantities.
- Records the number of rows inserted/updated in `dbo.SyncLog`.

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
- `dbo.StockLevel`
  - `UPDATE` rows where `WarehouseId` and `Sku` match (count depends on source data)
  - `INSERT` new rows for unmatched keys (count depends on source data)
- `dbo.SyncLog`
  - `INSERT` one row per sync run with counts of inserted/updated rows or error message

#### Relationships
- None defined (no foreign keys).

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Start transaction.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId` and `Sku`.
  3. `MERGE` into `StockLevel`:
     - `WHEN MATCHED`: add quantity to `OnHand`, update `UpdatedAt`.
     - `WHEN NOT MATCHED BY TARGET`: insert new row with `OnHand = Quantity`.
  4. Capture merge actions in table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of `INSERT` and `UPDATE` actions.
  6. Commit transaction.
  7. On error, rollback, log error, rethrow.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

---
