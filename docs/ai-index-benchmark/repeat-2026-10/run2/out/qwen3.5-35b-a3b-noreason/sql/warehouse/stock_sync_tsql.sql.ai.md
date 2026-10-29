### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:01Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly T-SQL stored procedure that merges supplier feed quantities into warehouse stock levels and logs the sync outcome.

#### Purpose
- Synchronizes nightly supplier feed data into the `StockLevel` table by aggregating quantities per warehouse and SKU.
- Records sync execution metrics (inserts, updates, errors) in the `SyncLog` table.

#### Type
- Table: `dbo.StockLevel` (T-SQL)
- Table: `dbo.SupplierFeed` (T-SQL)
- Table: `dbo.SyncLog` (T-SQL)
- Stored Procedure: `dbo.usp_SyncStock` (T-SQL)

#### Schema
**dbo.StockLevel**
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `OnHand` INT NOT NULL DEFAULT (0)
- `UpdatedAt` DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- **PK**: `WarehouseId`, `Sku`

**dbo.SupplierFeed**
- `FeedRowId` BIGINT IDENTITY(1, 1) NOT NULL
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `Quantity` INT NOT NULL
- `ReceivedAt` DATETIME2(0) NOT NULL
- **PK**: `FeedRowId`

**dbo.SyncLog**
- `SyncLogId` INT IDENTITY(1, 1) NOT NULL
- `StartedAt` DATETIME2(0) NOT NULL
- `RowsInserted` INT NOT NULL
- `RowsUpdated` INT NOT NULL
- `ErrorMessage` NVARCHAR(4000) NULL
- **PK**: `SyncLogId`

#### Reads
- `dbo.SupplierFeed`: Columns `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `ReceivedAt >= @Since`).
- `dbo.StockLevel`: Columns `WarehouseId`, `Sku`, `OnHand` (target of MERGE).
- Input parameter: `@Since` (DATETIME2(0)).

#### Writes
- `dbo.StockLevel`: Updates `OnHand` and `UpdatedAt`; Inserts `WarehouseId`, `Sku`, `OnHand`.
- `dbo.SyncLog`: Inserts `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.

#### Relationships
- `dbo.SupplierFeed.WarehouseId` -> `dbo.StockLevel.WarehouseId` (Join key for MERGE)
- `dbo.SupplierFeed.Sku` -> `dbo.StockLevel.Sku` (Join key for MERGE)

#### Routine logic
- **Procedure**: `dbo.usp_SyncStock`
- **Parameter**: `@Since` (DATETIME2(0))
- Aggregates `Quantity` from `dbo.SupplierFeed` grouped by `WarehouseId` and `Sku` where `ReceivedAt >= @Since`.
- Executes `MERGE` on `dbo.StockLevel`:
  - **Matched**: Updates `OnHand` by adding source quantity; refreshes `UpdatedAt`.
  - **Not Matched**: Inserts new row with `WarehouseId`, `Sku`, and `Quantity`.
- Captures `INSERT` vs `UPDATE` counts via `OUTPUT $action`.
- Logs counts to `dbo.SyncLog` on success.
- On error: Rolls back transaction, logs `ErrorMessage` to `dbo.SyncLog`, and re-throws exception.

#### Dependencies
- `dbo.SupplierFeed`
- `dbo.StockLevel`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`
