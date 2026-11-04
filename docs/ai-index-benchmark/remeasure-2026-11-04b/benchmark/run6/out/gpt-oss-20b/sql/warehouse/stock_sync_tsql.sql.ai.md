### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:37Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed quantities.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- **Table**: `dbo.StockLevel` (PostgreSQL dialect inferred from T‑SQL syntax, but actual target is SQL Server).
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
- `dbo.StockLevel`
  - `UPDATE` on matched rows: increments `OnHand`, updates `UpdatedAt`.
  - `INSERT` on non‑matched rows: inserts `WarehouseId`, `Sku`, `OnHand`.
- `dbo.SyncLog`
  - `INSERT` once per sync: `StartedAt`, `RowsInserted`, `RowsUpdated` (or `ErrorMessage` on failure).

#### Relationships
- None defined via foreign keys; logical join: `dbo.StockLevel.WarehouseId, Sku` ↔ `dbo.SupplierFeed.WarehouseId, Sku`.

#### Routine logic
- Parameters: `@Since DATETIME2(0)`.
- Steps:
  1. Capture start time `@Started`.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `WarehouseId, Sku`.
  3. `MERGE` into `StockLevel`:
     - `WHEN MATCHED`: `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
     - `WHEN NOT MATCHED BY TARGET`: `INSERT (WarehouseId, Sku, OnHand)`.
  4. Capture merge actions in table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of inserts and updates.
  6. Commit transaction; on error rollback, log error, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
