### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:48:56Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory and records the operation outcome.

#### Purpose
- Merge supplier feed data into current stock levels.
- Log each sync run with counts of inserted and updated rows or errors.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL)

#### Schema
- **dbo.StockLevel**
  - `WarehouseId INT NOT NULL`
  - `Sku NVARCHAR(40) NOT NULL`
  - `OnHand INT NOT NULL DEFAULT (0)`
  - `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`
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
- `dbo.SupplierFeed` (WarehouseId, Sku, Quantity, ReceivedAt)
- `dbo.StockLevel` (WarehouseId, Sku, OnHand)

#### Writes
- `dbo.StockLevel`
  - `INSERT`: rows where no existing record (count per merge action).
  - `UPDATE`: rows where existing record updated (count per merge action).
- `dbo.SyncLog`
  - `INSERT`: one row per sync run, with counts of inserts/updates or error message.

#### Relationships
- None defined via foreign keys.

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Start transaction.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId, Sku`.
  3. `MERGE` into `StockLevel`:
     - `WHEN MATCHED`: add `Quantity` to `OnHand`, update `UpdatedAt`.
     - `WHEN NOT MATCHED BY TARGET`: insert new record with `OnHand = Quantity`.
     - Capture `$action` into table variable `@Changes`.
  4. Insert a log row into `SyncLog` with counts of `INSERT` and `UPDATE` actions.
  5. Commit transaction.
  6. On error: rollback, insert log row with `ErrorMessage`, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
