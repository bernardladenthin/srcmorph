### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:18:18Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock levels from a supplier feed on a nightly basis, updating inventory counts and logging sync operations.

#### Purpose
- Updates stock inventory based on supplier feed data
- Logs synchronization results and errors

#### Type
- table
- table
- table
- procedure

#### Schema

##### dbo.StockLevel
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- OnHand INT NOT NULL DEFAULT (0)
- UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- CONSTRAINT PK_StockLevel PRIMARY KEY (WarehouseId, Sku)

##### dbo.SupplierFeed
- FeedRowId BIGINT NOT NULL IDENTITY(1, 1)
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- Quantity INT NOT NULL
- ReceivedAt DATETIME2(0) NOT NULL
- CONSTRAINT PK_SupplierFeed PRIMARY KEY (FeedRowId)

##### dbo.SyncLog
- SyncLogId INT NOT NULL IDENTITY(1, 1)
- StartedAt DATETIME2(0) NOT NULL
- RowsInserted INT NOT NULL
- RowsUpdated INT NOT NULL
- ErrorMessage NVARCHAR(4000) NULL
- CONSTRAINT PK_SyncLog PRIMARY KEY (SyncLogId)

#### Reads
- dbo.SupplierFeed.WarehouseId
- dbo.SupplierFeed.Sku
- dbo.SupplierFeed.Quantity
- dbo.SupplierFeed.ReceivedAt

#### Writes
- dbo.StockLevel.WarehouseId
- dbo.StockLevel.Sku
- dbo.StockLevel.OnHand
- dbo.StockLevel.UpdatedAt
- dbo.SyncLog.StartedAt
- dbo.SyncLog.RowsInserted
- dbo.SyncLog.RowsUpdated
- dbo.SyncLog.ErrorMessage

#### Relationships
- dbo.StockLevel.WarehouseId, Sku -> dbo.SupplierFeed.WarehouseId, Sku

#### Routine logic
- Accepts @Since datetime parameter
- Merges supplier feed data since @Since into StockLevel
- Updates existing stock levels by adding quantities
- Inserts new stock items from supplier feed
- Logs sync results including inserted/updated row counts
- Logs errors with error message if transaction fails
- Uses transaction for data consistency

#### Dependencies
- dbo.StockLevel
- dbo.SupplierFeed
- dbo.SyncLog
