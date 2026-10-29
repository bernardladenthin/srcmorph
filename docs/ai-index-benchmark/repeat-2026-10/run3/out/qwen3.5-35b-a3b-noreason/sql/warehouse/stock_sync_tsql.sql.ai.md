### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:18Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly T-SQL stored procedure that merges supplier feed quantities into warehouse stock levels and logs the synchronization outcome.

#### Purpose
- Synchronizes nightly supplier feed data into the `StockLevel` table by aggregating quantities per warehouse and SKU.
- Records synchronization metrics (inserts, updates, errors) into the `SyncLog` table.

#### Type
- Table: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` (T-SQL / SQL Server)
- Stored Procedure: `dbo.usp_SyncStock` (T-SQL / SQL Server)

#### Schema
**Table: dbo.StockLevel**
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `OnHand` INT NOT NULL DEFAULT (0)
- `UpdatedAt` DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- **PK**: `WarehouseId`, `Sku`

**Table: dbo.SupplierFeed**
- `FeedRowId` BIGINT IDENTITY(1, 1) NOT NULL
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `Quantity` INT NOT NULL
- `ReceivedAt` DATETIME2(0) NOT NULL
- **PK**: `FeedRowId`

**Table: dbo.SyncLog**
- `SyncLogId` INT IDENTITY(1, 1) NOT NULL
- `StartedAt` DATETIME2(0) NOT NULL
- `RowsInserted` INT NOT NULL
- `RowsUpdated` INT NOT NULL
- `ErrorMessage` NVARCHAR(4000) NULL
- **PK**: `SyncLogId`

#### Reads
- `dbo.SupplierFeed`: Columns `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `ReceivedAt >= @Since`).
- `dbo.StockLevel`: Columns `WarehouseId`, `Sku`, `OnHand` (target of MERGE).
- Input Parameter: `@Since` (DATETIME2(0)).

#### Writes
- `dbo.StockLevel`: Updates `OnHand` and `UpdatedAt` on match; Inserts `WarehouseId`, `Sku`, `OnHand` on not matched.
- `dbo.SyncLog`: Inserts `StartedAt`, `RowsInserted`, `RowsUpdated`, and optionally `ErrorMessage`.

#### Relationships
- `dbo.SupplierFeed.WarehouseId` -> `dbo.StockLevel.WarehouseId` (Join key for MERGE).
- `dbo.SupplierFeed.Sku` -> `dbo.StockLevel.Sku` (Join key for MERGE).

#### Routine logic
- **Procedure**: `dbo.usp_SyncStock`
- **Parameter**: `@Since` (DATETIME2(0)) - start time for filtering feed.
- **Logic Steps**:
  - Aggregates `Quantity` from `dbo.SupplierFeed` where `ReceivedAt >= @Since` grouped by `WarehouseId` and `Sku`.
  - Executes `MERGE` against `dbo.StockLevel`:
    - Updates `OnHand` by adding source quantity and refreshes `UpdatedAt` if row exists.
    - Inserts new row with `WarehouseId`, `Sku`, and `Quantity` if row does not exist.
  - Captures `MERGE` action types (INSERT/UPDATE) into a table variable.
  - Calculates counts of inserted and updated rows from the action log.
  - Inserts summary counts into `dbo.SyncLog`.
  - Wraps logic in `TRY...CATCH`: commits on success, rolls back on error, and logs error message to `dbo.SyncLog`.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `$action`.
