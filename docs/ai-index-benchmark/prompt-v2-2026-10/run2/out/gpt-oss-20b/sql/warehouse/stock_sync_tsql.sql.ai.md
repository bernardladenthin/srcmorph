### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:53:58Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory and records the operation outcome.

#### Purpose
- Updates `dbo.StockLevel` with aggregated supplier feed data since a given timestamp.
- Logs the number of rows inserted/updated and any errors in `dbo.SyncLog`.

#### Type
- Table: `dbo.StockLevel` (SQL Server, T-SQL)
- Table: `dbo.SupplierFeed` (SQL Server, T-SQL)
- Table: `dbo.SyncLog` (SQL Server, T-SQL)
- Procedure: `dbo.usp_SyncStock` (SQL Server, T-SQL)

#### Schema
**dbo.StockLevel**
- `WarehouseId INT NOT NULL`
- `Sku NVARCHAR(40) NOT NULL`
- `OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)`
- `UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`
- **PK**: `PK_StockLevel (WarehouseId, Sku)`

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
- `dbo.SupplierFeed` columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`
- `dbo.StockLevel` columns: `WarehouseId`, `Sku`, `OnHand`

#### Writes
- `dbo.StockLevel`: `OnHand`, `UpdatedAt` (UPDATE), `WarehouseId`, `Sku`, `OnHand` (INSERT)
- `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` (INSERT)

#### Relationships
- None defined via foreign keys; logical join in `MERGE` on `WarehouseId` and `Sku`.

#### Routine logic
- **Parameters**: `@Since DATETIME2(0)`
- **Steps**:
  1. Capture start time `@Started`.
  2. Aggregate `SupplierFeed` rows received after `@Since` by `WarehouseId` and `Sku`.
  3. `MERGE` into `StockLevel`:
     - If match: increment `OnHand` by aggregated `Quantity`, update `UpdatedAt`.
     - If no match: insert new row with `OnHand = Quantity`.
  4. Record merge actions in table variable `@Changes`.
  5. Insert a log row into `SyncLog` with counts of INSERT and UPDATE actions.
  6. Commit transaction; on error rollback, log error, rethrow.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
- Transaction control: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`
