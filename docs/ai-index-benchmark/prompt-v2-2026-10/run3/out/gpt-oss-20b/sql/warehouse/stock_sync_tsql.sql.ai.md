### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:09:22Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory table and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed data since a given timestamp.
- Logs the number of rows inserted/updated and any errors in `dbo.SyncLog`.

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
- `dbo.SupplierFeed` (columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`) filtered by `ReceivedAt >= @Since`.
- `dbo.StockLevel` (columns: `WarehouseId`, `Sku`, `OnHand`) for merge matching.

#### Writes
- `dbo.StockLevel`: `OnHand` and `UpdatedAt` updated or new rows inserted.
- `dbo.SyncLog`: one row inserted per execution; `RowsInserted` and `RowsUpdated` derived from merge output; `ErrorMessage` on failure.

#### Relationships
- None (no foreign keys defined).

#### Routine logic
- Parameters: `@Since DATETIME2(0)`
- Steps:
  1. Begin transaction.
  2. Aggregate `SupplierFeed` rows by `WarehouseId` and `Sku` where `ReceivedAt >= @Since`.
  3. Merge into `StockLevel`:
     - If match: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
     - If no match: insert new row with `OnHand = Quantity`.
  4. Capture merge actions in `@Changes` table.
  5. Insert a log row into `SyncLog` with counts of inserts/updates.
  6. Commit transaction; on error rollback and log error.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- System variables: `@@TRANCOUNT`
