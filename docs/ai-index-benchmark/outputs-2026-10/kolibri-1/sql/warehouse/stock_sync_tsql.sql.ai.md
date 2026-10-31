### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:34:03Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly stock synchronisation merges supplier feed quantities into warehouse stock levels and logs the outcome.

#### Purpose
- Nightly stock synchronisation from a supplier feed into warehouse stock levels.
- Merges received quantities into existing stock and logs insert/update counts and errors.

#### Type
- Procedure: T-SQL stored procedure `dbo.usp_SyncStock`.

#### Schema
- `dbo.StockLevel`: WarehouseId INT NOT NULL, Sku NVARCHAR(40) NOT NULL, OnHand INT NOT NULL DEFAULT 0, UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(); PK (WarehouseId, Sku).
- `dbo.SupplierFeed`: FeedRowId BIGINT IDENTITY(1,1) NOT NULL PK, WarehouseId INT NOT NULL, Sku NVARCHAR(40) NOT NULL, Quantity INT NOT NULL, ReceivedAt DATETIME2(0) NOT NULL.
- `dbo.SyncLog`: SyncLogId INT IDENTITY(1,1) NOT NULL PK, StartedAt DATETIME2(0) NOT NULL, RowsInserted INT NOT NULL, RowsUpdated INT NOT NULL, ErrorMessage NVARCHAR(4000) NULL.

#### Reads
- `dbo.StockLevel` (target): WarehouseId, Sku, OnHand, UpdatedAt.
- `dbo.SupplierFeed`: WarehouseId, Sku, Quantity, ReceivedAt.
- `dbo.SyncLog`: StartedAt, RowsInserted, RowsUpdated, ErrorMessage.

#### Writes
- `dbo.StockLevel`: OnHand, UpdatedAt.
- `dbo.SyncLog`: StartedAt, RowsInserted, RowsUpdated, ErrorMessage.

#### Relationships
- `dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId`.
- `dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku`.

#### Routine logic
- Parameter: @Since DATETIME2(0).
- Merges supplier feed rows received since @Since into StockLevel by WarehouseId and Sku.
- Matched rows: updates OnHand by adding source.Quantity and sets UpdatedAt to SYSUTCDATETIME().
- Unmatched rows: inserts new StockLevel rows with source.Quantity as OnHand.
- Outputs merge actions into a table variable for logging.
- Logs StartedAt, RowsInserted, RowsUpdated into SyncLog.
- On error: rolls back transaction, logs error message, and rethrows.

#### Dependencies
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
