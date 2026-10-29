### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:14:35Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock levels from supplier feed data with audit logging and transactional integrity

#### Purpose
- Merges incoming supplier feed data into the central stock level table with insert and update operations.  
- Logs the number of rows inserted and updated, along with any errors during the sync process.

#### Type
Procedure (T-SQL)

#### Schema
**dbo.StockLevel**  
- WarehouseId INT NOT NULL  
- Sku NVARCHAR(40) NOT NULL  
- OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)  
- UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())  
- PRIMARY KEY (WarehouseId, Sku)  

**dbo.SupplierFeed**  
- FeedRowId BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY  
- WarehouseId INT NOT NULL  
- Sku NVARCHAR(40) NOT NULL  
- Quantity INT NOT NULL  
- ReceivedAt DATETIME2(0) NOT NULL  

**dbo.SyncLog**  
- SyncLogId INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY  
- StartedAt DATETIME2(0) NOT NULL  
- RowsInserted INT NOT NULL  
- RowsUpdated INT NOT NULL  
- ErrorMessage NVARCHAR(4000) NULL  

#### Reads
- dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt)  
- dbo.StockLevel (WarehouseId, Sku, OnHand)  

#### Writes
- dbo.StockLevel (WarehouseId, Sku, OnHand, UpdatedAt)  
- dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage)  

#### Relationships
- dbo.SupplierFeed.WarehouseId → dbo.StockLevel.WarehouseId  
- dbo.SupplierFeed.Sku → dbo.StockLevel.Sku  

#### Routine logic
- Takes a @Since datetime parameter to filter feed records received since that point.  
- Uses a MERGE statement to either update existing stock levels or insert new ones based on grouped feed data.  
- Tracks merge actions (INSERT/UPDATE) in a temporary table and aggregates them into the SyncLog.  
- Logs start time, number of inserted and updated rows, and any error messages.  
- Wraps execution in a transaction with rollback on error.  

#### Dependencies
- dbo.StockLevel  
- dbo.SupplierFeed  
- dbo.SyncLog  
- SYSUTCDATETIME() function
