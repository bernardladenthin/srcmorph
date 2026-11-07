### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:41:09Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Synchronises supplier stock feed into the warehouse inventory, recording insert/update counts and errors.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier quantities.
- Logs each sync run in `dbo.SyncLog`.

#### Type
- **Table**: `dbo.StockLevel` (SQL Server, T-SQL)
- **Table**: `dbo.SupplierFeed` (SQL Server, T-SQL)
- **Table**: `dbo.SyncLog` (SQL Server, T-SQL)
- **Procedure**: `dbo.usp_SyncStock` (SQL Server, T-SQL)

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
- `dbo.StockLevel` – `WarehouseId`, `Sku`, `OnHand` (for merge target).

#### Writes
- `dbo.StockLevel`
  - `INSERT`: one row per distinct `(WarehouseId, Sku)` not present in target.
  - `UPDATE`: one row per matched `(WarehouseId, Sku)`; `OnHand` incremented, `UpdatedAt` set.
- `dbo.SyncLog`
  - `INSERT`: one row per sync run; counts derived from merge output.

#### Relationships
- None (no foreign keys defined).

#### Routine logic (`dbo.usp_SyncStock`)
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Begin transaction.
  2. Aggregate `SupplierFeed` rows received since `@Since` by `(WarehouseId, Sku)` → `Quantity`.
  3. `MERGE` into `StockLevel`:
     - `WHEN MATCHED`: `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
     - `WHEN NOT MATCHED BY TARGET`: `INSERT (WarehouseId, Sku, OnHand)`.
     - Capture `$action` into `@Changes`.
  4. Insert into `SyncLog`:
     - `RowsInserted` = count of `INSERT` actions.
     - `RowsUpdated` = count of `UPDATE` actions.
  5. Commit transaction.
  6. On error: rollback, insert error message into `SyncLog`, rethrow.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
