### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:17:30Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronization merging supplier feed quantities into on-hand inventory levels with sync audit logging.
#### Purpose
- Synchronize dbo.StockLevel on-hand inventory from dbo.SupplierFeed deliveries received since cutoff
- Audit each sync run outcome in dbo.SyncLog with inserted/updated counts or error message
#### Type
- table, procedure, T-SQL dialect SQL Server
- Objects: dbo.StockLevel table, dbo.SupplierFeed table, dbo.SyncLog table, dbo.usp_SyncStock procedure
#### Schema
- dbo.StockLevel: WarehouseId INT NOT NULL PK, Sku NVARCHAR(40) NOT NULL PK, OnHand INT NOT NULL DEFAULT 0, UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(), CONSTRAINT PK_StockLevel PRIMARY KEY (WarehouseId, Sku), CONSTRAINT DF_StockLevel_OnHand, CONSTRAINT DF_StockLevel_UpdatedAt
- dbo.SupplierFeed: FeedRowId BIGINT IDENTITY(1,1) NOT NULL PK, WarehouseId INT NOT NULL, Sku NVARCHAR(40) NOT NULL, Quantity INT NOT NULL, ReceivedAt DATETIME2(0) NOT NULL, CONSTRAINT PK_SupplierFeed PRIMARY KEY (FeedRowId)
- dbo.SyncLog: SyncLogId INT IDENTITY(1,1) NOT NULL PK, StartedAt DATETIME2(0) NOT NULL, RowsInserted INT NOT NULL, RowsUpdated INT NOT NULL, ErrorMessage NVARCHAR(4000) NULL, CONSTRAINT PK_SyncLog PRIMARY KEY (SyncLogId)
#### Reads
- dbo.SupplierFeed.WarehouseId, Sku, Quantity, ReceivedAt filtered by ReceivedAt >= @Since, grouped by WarehouseId, Sku with SUM(Quantity)
- dbo.StockLevel.WarehouseId, Sku, OnHand as MERGE target for MATCHED check
- Table variable @Changes.MergeAction for SUM CASE INSERT / UPDATE counts
#### Writes
- dbo.StockLevel.WarehouseId, Sku, OnHand, UpdatedAt via MERGE UPDATE SET OnHand + Quantity and INSERT (WarehouseId, Sku, OnHand)
- dbo.SyncLog.StartedAt, RowsInserted, RowsUpdated, ErrorMessage via INSERT on success and CATCH failure path
#### Relationships
- dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId via MERGE ON match
- dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku via MERGE ON match
#### Routine logic
- Parameter: @Since DATETIME2(0) cutoff for SupplierFeed.ReceivedAt
- Sets @Started = SYSUTCDATETIME(), @Changes TABLE (MergeAction NVARCHAR(10)), SET NOCOUNT ON
- MERGE aggregated SupplierFeed source into StockLevel target, OUTPUT $action INTO @Changes
- INSERT SyncLog counts from @Changes, COMMIT TRANSACTION on TRY, ROLLBACK + INSERT SyncLog ErrorMessage + THROW on CATCH
#### Dependencies
- dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog
- Functions: SYSUTCDATETIME, SUM, ERROR_MESSAGE, @@TRANCOUNT
