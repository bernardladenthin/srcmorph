### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:40:34Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed data.
- Records sync statistics and errors in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel`
- Table: `dbo.SupplierFeed`
- Table: `dbo.SyncLog`
- Procedure: `dbo.usp_SyncStock` (T‑SQL)

#### Schema
**dbo.StockLevel**
- `WarehouseId INT NOT NULL`
- `Sku NVARCHAR(40) NOT NULL`
- `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`
- `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`
- `CONSTRAINT PK_StockLevel PRIMARY KEY (WarehouseId, Sku)`

**dbo.SupplierFeed**
- `FeedRowId BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY`
- `WarehouseId INT NOT NULL`
- `Sku NVARCHAR(40) NOT NULL`
- `Quantity INT NOT NULL`
- `ReceivedAt DATETIME2(0) NOT NULL`

**dbo.SyncLog**
- `SyncLogId INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY`
- `StartedAt DATETIME2(0) NOT NULL`
- `RowsInserted INT NOT NULL`
- `RowsUpdated INT NOT NULL`
- `ErrorMessage NVARCHAR(4000) NULL`

#### Reads
- `dbo.SupplierFeed` (WarehouseId, Sku, Quantity, ReceivedAt) – filtered by `ReceivedAt >= @Since`.
- `dbo.StockLevel` (WarehouseId, Sku, OnHand) – for merge matching.

#### Writes
- `dbo.StockLevel` – UPDATE `OnHand`, `UpdatedAt` or INSERT new rows.
- `dbo.SyncLog` – INSERT one row per sync, with counts of INSERT/UPDATE actions or error details.

#### Relationships
- `dbo.StockLevel` PK: `(WarehouseId, Sku)`.
- `dbo.SupplierFeed` has no explicit FK but is joined on `(WarehouseId, Sku)` to `dbo.StockLevel`.

#### Routine logic
- Parameters: `@Since DATETIME2(0)`.
- Start transaction.
- Aggregate `SupplierFeed` rows since `@Since` by `(WarehouseId, Sku)` → `Quantity`.
- MERGE into `StockLevel`:
  - MATCHED: `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
  - NOT MATCHED: INSERT `WarehouseId`, `Sku`, `OnHand = Quantity`.
  - Capture `$action` into `@Changes`.
- Insert into `SyncLog`:
  - `StartedAt = @Started`.
  - `RowsInserted = count of 'INSERT' actions`.
  - `RowsUpdated = count of 'UPDATE' actions`.
- Commit transaction.
- On error: rollback, insert error message into `SyncLog`, rethrow.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
