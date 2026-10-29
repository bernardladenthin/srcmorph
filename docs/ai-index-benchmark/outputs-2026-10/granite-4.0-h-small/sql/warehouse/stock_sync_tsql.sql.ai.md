### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T05:10:16Z
- G: 
- A: 0.0.0
- X: file
---
> Warehouse: nightly stock synchronisation from a supplier feed (SQL Server, T-SQL).

#### Purpose
- Synchronises supplier‑provided stock quantities into the `StockLevel` table.
- Tracks the import in `SyncLog` with counts of inserted/updated rows and any error.

#### Type
- Table: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock`

#### Schema
**dbo.StockLevel**
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `OnHand` INT NOT NULL DEFAULT (0)
- `UpdatedAt` DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- **PK**: `(WarehouseId, Sku)`

**dbo.SupplierFeed**
- `FeedRowId` BIGINT IDENTITY(1,1) PRIMARY KEY
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `Quantity` INT NOT NULL
- `ReceivedAt` DATETIME2(0) NOT NULL

**dbo.SyncLog**
- `SyncLogId` INT IDENTITY(1,1) PRIMARY KEY
- `StartedAt` DATETIME2(0) NOT NULL
- `RowsInserted` INT NOT NULL
- `RowsUpdated` INT NOT NULL
- `ErrorMessage` NVARCHAR(4000) NULL

#### Reads
- `dbo.SupplierFeed` (filtered by `ReceivedAt >= @Since`)

#### Writes
- `dbo.StockLevel` (INSERT, UPDATE)
- `dbo.SyncLog` (INSERT)

#### Relationships
- `StockLevel.WarehouseId -> SupplierFeed.WarehouseId`
- `StockLevel.Sku -> SupplierFeed.Sku`

#### Routine logic
- **Parameters**: `@Since DATETIME2(0)` – start of feed window.
- **Logic**:
  - Begin transaction.
  - `MERGE` from aggregated `SupplierFeed` rows into `StockLevel`:
    - MATCHED: add `Quantity` to `OnHand`, set `UpdatedAt`.
    - NOT MATCHED BY TARGET: insert new row.
  - Capture `$action` into `@Changes`.
  - Insert a row into `SyncLog` with counts of inserted/updated rows from `@Changes`.
  - Commit transaction.
- **Error handling**: on catch, rollback, log error message, re‑throw.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
