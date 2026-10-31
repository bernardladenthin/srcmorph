### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:51:42Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock levels are synchronized from a supplier feed by aggregating received quantities per warehouse/SKU, upserting them into StockLevel, and logging insert/update counts or errors in SyncLog.

#### Purpose
- Merges aggregated supplier feed rows into dbo.StockLevel by WarehouseId and Sku.
- Inserts a summary row into dbo.SyncLog with insert/update counts and any error message.

#### Type
- Tables: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog
- Stored procedure: dbo.usp_SyncStock
- Dialect: T-SQL (SQL Server)

#### Schema
dbo.StockLevel
- WarehouseId INT NOT NULL (part of PK)
- Sku NVARCHAR(40) NOT NULL (part of PK)
- OnHand INT NOT NULL DEFAULT (0)
- UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- PK_StockLevel PRIMARY KEY (WarehouseId, Sku)

dbo.SupplierFeed
- FeedRowId BIGINT IDENTITY(1,1) NOT NULL PK_SupplierFeed PRIMARY KEY
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- Quantity INT NOT NULL
- ReceivedAt DATETIME2(0) NOT NULL

dbo.SyncLog
- SyncLogId INT IDENTITY(1,1) NOT NULL PK_SyncLog PRIMARY KEY
- StartedAt DATETIME2(0) NOT NULL
- RowsInserted INT NOT NULL
- RowsUpdated INT NOT NULL
- ErrorMessage NVARCHAR(4000) NULL

#### Reads
- dbo.SupplierFeed.ReceivedAt filtered by @Since
- dbo.SupplierFeed.WarehouseId, Sku, Quantity grouped for MERGE source
- dbo.StockLevel.WarehouseId, Sku as MERGE target keys
- @Since DATETIME2(0) procedure parameter

#### Writes
- dbo.StockLevel.OnHand and dbo.StockLevel.UpdatedAt via MERGE WHEN MATCHED UPDATE
- dbo.StockLevel.WarehouseId, Sku, OnHand via MERGE WHEN NOT MATCHED BY TARGET INSERT
- dbo.SyncLog.StartedAt, RowsInserted, RowsUpdated, ErrorMessage

#### Relationships
- MERGE join: dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId
- MERGE join: dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku
- No declared foreign key constraints between tables.

#### Routine logic
- Parameter: @Since DATETIME2(0)
- Captures @Started = SYSUTCDATETIME()
- Begins transaction
- MERGE dbo.StockLevel target using aggregated dbo.SupplierFeed where ReceivedAt >= @Since grouped by WarehouseId, Sku
- WHEN MATCHED: updates OnHand = target.OnHand + source.Quantity, UpdatedAt = SYSUTCDATETIME()
- WHEN NOT MATCHED BY TARGET: inserts WarehouseId, Sku, OnHand
- OUTPUT $action into @Changes table variable
- INSERT into dbo.SyncLog with StartedAt, RowsInserted, RowsUpdated aggregated from @Changes
- COMMIT TRANSACTION
- CATCH: rolls back if @@TRANCOUNT > 0, inserts error row into dbo.SyncLog with ErrorMessage = ERROR_MESSAGE(), then THROW

#### Dependencies
- dbo.SupplierFeed
- dbo.StockLevel
- dbo.SyncLog
- SYSUTCDATETIME
- ERROR_MESSAGE
