### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:22:29Z
- G: 
- A: 0.0.0
- X: file
---
> Maintains and updates warehouse stock levels based on supplier feed data, synchronizing nightly.

#### Purpose
- Manages stock levels in a warehouse
- Updates stock quantities from supplier feeds
- Logs synchronization activities

#### Type
Procedure / T-SQL

#### Schema
##### dbo.StockLevel
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- OnHand INT NOT NULL DEFAULT (0)
- UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- PK_StockLevel: PRIMARY KEY (WarehouseId, Sku)

##### dbo.SupplierFeed
- FeedRowId BIGINT IDENTITY(1, 1) NOT NULL
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- Quantity INT NOT NULL
- ReceivedAt DATETIME2(0) NOT NULL
- PK_SupplierFeed: PRIMARY KEY (FeedRowId)

##### dbo.SyncLog
- SyncLogId INT IDENTITY(1, 1) NOT NULL
- StartedAt DATETIME2(0) NOT NULL
- RowsInserted INT NOT NULL
- RowsUpdated INT NOT NULL
- ErrorMessage NVARCHAR(4000) NULL
- PK_SyncLog: PRIMARY KEY (SyncLogId)

#### Reads
- dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt)
- dbo.StockLevel (WarehouseId, Sku, OnHand, UpdatedAt)

#### Writes
- dbo.StockLevel (OnHand, UpdatedAt)
- dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage)

#### Relationships
- dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId
- dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku

#### Routine logic
- Accepts @Since parameter for synchronization timeframe
- Merges supplier feed data into StockLevel table
- Updates existing stock levels or inserts new items
- Logs number of inserted and updated rows
- Handles errors and logs error messages

#### Dependencies
- dbo.StockLevel
- dbo.SupplierFeed
- dbo.SyncLog
