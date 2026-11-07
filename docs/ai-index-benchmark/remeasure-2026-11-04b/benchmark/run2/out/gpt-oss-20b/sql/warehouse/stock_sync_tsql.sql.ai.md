### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:02:43Z
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
- **Table**: `dbo.StockLevel` (PostgreSQL? actually SQL Server T‑SQL)
- **Table**: `dbo.SupplierFeed`
- **Table**: `dbo.SyncLog`
- **Procedure**: `dbo.usp_SyncStock` (T‑SQL)

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
- `dbo.SupplierFeed` – `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `@Since`).
- `dbo.StockLevel` – `WarehouseId`, `Sku`, `OnHand` (for merge match).

#### Writes
- `dbo.StockLevel`
  - `INSERT`: one row per distinct `(WarehouseId, Sku)` not present.
  - `UPDATE`: one row per matched `(WarehouseId, Sku)`; `OnHand` incremented, `UpdatedAt` set.
- `dbo.SyncLog`
  - `INSERT`: one row per sync run; counts of inserted and updated rows derived from merge output.

#### Relationships
- None (no foreign keys defined).

#### Routine logic (`dbo.usp_SyncStock`)
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Capture start time `@Started`.
  2. Begin transaction.
  3. `MERGE` `dbo.StockLevel` with aggregated `dbo.SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`).
     - `WHEN MATCHED`: update `OnHand` and `UpdatedAt`.
     - `WHEN NOT MATCHED BY TARGET`: insert new stock level.
     - Capture merge action (`INSERT`/`UPDATE`) into table variable `@Changes`.
  4. Insert a log row into `dbo.SyncLog` with counts of inserted and updated rows.
  5. Commit transaction.
  6. On error: rollback, insert error log row, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
