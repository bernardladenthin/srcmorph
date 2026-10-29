### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:57:16Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly synchronization of supplier stock quantities into warehouse inventory levels with audit logging.

#### Purpose
- Executes a nightly batch merge to update `StockLevel` inventory counts based on new entries in `SupplierFeed`.
- Logs successful row counts or errors into `SyncLog` for operational monitoring.

#### Type
T-SQL (SQL Server) stored procedure and tables.

#### Schema
**Table: dbo.StockLevel**
- `WarehouseId INT NOT NULL` (Part of PK)
- `Sku NVARCHAR(40) NOT NULL` (Part of PK)
- `OnHand INT NOT NULL DEFAULT 0`
- `UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()`
- **PK**: `PRIMARY KEY (WarehouseId, Sku)`

**Table: dbo.SupplierFeed**
- `FeedRowId BIGINT IDENTITY(1, 1) NOT NULL` (PK)
- `WarehouseId INT NOT NULL`
- `Sku NVARCHAR(40) NOT NULL`
- `Quantity INT NOT NULL`
- `ReceivedAt DATETIME2(0) NOT NULL`
- **PK**: `PRIMARY KEY (FeedRowId)`

**Table: dbo.SyncLog**
- `SyncLogId INT IDENTITY(1, 1) NOT NULL` (PK)
- `StartedAt DATETIME2(0) NOT NULL`
- `RowsInserted INT NOT NULL`
- `RowsUpdated INT NOT NULL`
- `ErrorMessage NVARCHAR(4000) NULL`
- **PK**: `PRIMARY KEY (SyncLogId)`

#### Reads
- `dbo.SupplierFeed`: Columns `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` (filtered by `ReceivedAt >= @Since`).
- `dbo.StockLevel`: Columns `WarehouseId`, `Sku`, `OnHand` (target for merge).
- Input parameter `@Since` (DATETIME2).

#### Writes
- `dbo.StockLevel`: Updates `OnHand` and `UpdatedAt`; Inserts `WarehouseId`, `Sku`, `OnHand`.
- `dbo.SyncLog`: Inserts `StartedAt`, `RowsInserted`, `RowsUpdated`, and optionally `ErrorMessage`.

#### Relationships
- `dbo.SupplierFeed.WarehouseId` -> `dbo.StockLevel.WarehouseId` (Join key for merge).
- `dbo.SupplierFeed.Sku` -> `dbo.StockLevel.Sku` (Join key for merge).

#### Routine logic
- Procedure `dbo.usp_SyncStock(@Since DATETIME2(0))`:
    - Aggregates `Quantity` from `dbo.SupplierFeed` where `ReceivedAt >= @Since` grouped by `WarehouseId` and `Sku`.
    - Performs `MERGE` against `dbo.StockLevel`:
        - Matches existing rows: increments `OnHand` by aggregated quantity, updates `UpdatedAt`.
        - No match: inserts new row with `WarehouseId`, `Sku`, and `Quantity`.
    - Captures merge actions (`INSERT`/`UPDATE`) via `OUTPUT $action`.
    - Records aggregate counts of inserted and updated rows into `dbo.SyncLog`.
    - Catches errors: rolls back transaction if active, logs error message to `dbo.SyncLog`, re-throws exception.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- System Functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
