### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:54:26Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier quantities.
- Records sync statistics in `dbo.SyncLog`.

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
- `dbo.SupplierFeed` (WHERE `ReceivedAt >= @Since`, GROUP BY `WarehouseId, Sku`)
- `dbo.StockLevel` (for merge matching)

#### Writes
- `dbo.StockLevel`
  - `UPDATE` on matched rows: `OnHand += source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`
  - `INSERT` on non‑matched rows: `WarehouseId, Sku, OnHand = source.Quantity`
- `dbo.SyncLog`
  - `INSERT` once per sync: `StartedAt`, `RowsInserted`, `RowsUpdated`
  - On error: `INSERT` with `ErrorMessage`

#### Relationships
- `dbo.StockLevel (WarehouseId, Sku)` ↔ `dbo.SupplierFeed (WarehouseId, Sku)` (used in MERGE)

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Capture start time `@Started`.
  2. Aggregate supplier feed rows since `@Since` into `source` table.
  3. MERGE `source` into `dbo.StockLevel`:
     - `WHEN MATCHED`: update `OnHand` and `UpdatedAt`.
     - `WHEN NOT MATCHED BY TARGET`: insert new stock level.
     - Capture `$action` into `@Changes`.
  4. Insert sync statistics into `dbo.SyncLog` from `@Changes`.
  5. Commit transaction; on error rollback and log error.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- Transaction control: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`
