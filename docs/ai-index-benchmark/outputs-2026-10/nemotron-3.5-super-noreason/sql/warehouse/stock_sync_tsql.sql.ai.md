### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:03:11Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronization aggregates supplier feed quantities received since a cutoff, merges them into stock levels, and logs insert/update counts or errors.

#### Purpose
- Synchronizes supplier feed quantities into warehouse stock levels for a given time window.
- Records sync statistics and any error messages in a sync log.

#### Type
Procedure, T-SQL (SQL Server).

#### Schema
dbo.StockLevel
- WarehouseId INT NOT NULL, part of PK
- Sku NVARCHAR(40) NOT NULL, part of PK
- OnHand INT NOT NULL DEFAULT (0)
- UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- PK_StockLevel PRIMARY KEY (WarehouseId, Sku)

dbo.SupplierFeed
- FeedRowId BIGINT IDENTITY(1, 1) NOT NULL, PK_SupplierFeed PRIMARY KEY
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- Quantity INT NOT NULL
- ReceivedAt DATETIME2(0) NOT NULL

dbo.SyncLog
- SyncLogId INT IDENTITY(1, 1) NOT NULL, PK_SyncLog PRIMARY KEY
- StartedAt DATETIME2(0) NOT NULL
- RowsInserted INT NOT NULL
- RowsUpdated INT NOT NULL
- ErrorMessage NVARCHAR(4000) NULL

#### Reads
- dbo.SupplierFeed.ReceivedAt, WarehouseId, Sku, Quantity filtered by @Since
- dbo.StockLevel.WarehouseId, Sku, OnHand, UpdatedAt as merge target

#### Writes
- dbo.StockLevel.OnHand and UpdatedAt updated when matched
- dbo.StockLevel.WarehouseId, Sku, OnHand inserted when not matched
- dbo.SyncLog.StartedAt, RowsInserted, RowsUpdated, ErrorMessage inserted

#### Relationships
- No foreign keys defined between dbo.SupplierFeed and dbo.StockLevel.
- Logical join: dbo.SupplierFeed.WarehouseId, Sku -> dbo.StockLevel.WarehouseId, Sku.

#### Routine logic
- Parameters: @Since DATETIME2(0).
- Captures @Started as SYSUTCDATETIME().
- Aggregates dbo.SupplierFeed by WarehouseId and Sku where ReceivedAt >= @Since.
- MERGE into dbo.StockLevel: matched rows add Quantity to OnHand and refresh UpdatedAt; unmatched rows insert new stock level.
- OUTPUT $action into @Changes table variable.
- Inserts one row into dbo.SyncLog with StartedAt, insert count, update count.
- On error, rolls back transaction, inserts error log row with zero counts, then THROW.

#### Dependencies
- dbo.StockLevel
- dbo.SupplierFeed
- dbo.SyncLog
- SYSUTCDATETIME, ERROR_MESSAGE, @@TRANCOUNT
