### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:44:00Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronisation: merges supplier feed quantities into per-warehouse SKU stock levels and logs sync outcomes.

#### Purpose
- Models warehouse stock levels per SKU and the inbound supplier feed that drives updates.
- Provides a stored procedure to merge feed rows into stock levels and record sync statistics.

#### Type
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock`
- Dialect: T-SQL (SQL Server)

#### Schema

**dbo.StockLevel**
- `WarehouseId INT NOT NULL` — part of PK
- `Sku NVARCHAR(40) NOT NULL` — part of PK
- `OnHand INT NOT NULL DEFAULT (0)`
- `UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())`
- PK: `PK_StockLevel (WarehouseId, Sku)`

**dbo.SupplierFeed**
- `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — PK
- `WarehouseId INT NOT NULL`
- `Sku NVARCHAR(40) NOT NULL`
- `Quantity INT NOT NULL`
- `ReceivedAt DATETIME2(0) NOT NULL`
- PK: `PK_SupplierFeed (FeedRowId)`

**dbo.SyncLog**
- `SyncLogId INT IDENTITY(1,1) NOT NULL` — PK
- `StartedAt DATETIME2(0) NOT NULL`
- `RowsInserted INT NOT NULL`
- `RowsUpdated INT NOT NULL`
- `ErrorMessage NVARCHAR(4000) NULL`
- PK: `PK_SyncLog (SyncLogId)`

#### Reads
- `dbo.SupplierFeed` — columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku`)

#### Writes
- `dbo.StockLevel` — MERGE target: updates `OnHand`, `UpdatedAt`; inserts `WarehouseId`, `Sku`, `OnHand`
- `dbo.SyncLog` — INSERT: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`

#### Relationships
- `dbo.StockLevel.WarehouseId` ↔ `dbo.SupplierFeed.WarehouseId` (logical join, no FK constraint)
- `dbo.StockLevel.Sku` ↔ `dbo.SupplierFeed.Sku` (logical join, no FK constraint)

#### Routine logic
- **Procedure:** `dbo.usp_SyncStock`
- **Parameter:** `@Since DATETIME2(0)` — lower bound for feed rows to process
- **Steps:**
  1. Opens a transaction.
  2. MERGE: aggregates `SupplierFeed` rows since `@Since` by `(WarehouseId, Sku)` summing `Quantity`; on match increments `StockLevel.OnHand` and stamps `UpdatedAt`; on no-match inserts new row.
  3. Captures `$action` into a table variable `@Changes`.
  4. Inserts a `SyncLog` row with counts of INSERT vs UPDATE actions.
  5. Commits; on error rolls back, logs `ErrorMessage`, and re-throws.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
