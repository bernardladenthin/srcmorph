### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:06Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes stock levels in the warehouse by merging data from a supplier feed into the master stock level table and logging the operation.

#### Purpose
* Executes nightly stock synchronization from supplier feed into StockLevel.
* Logs the results (inserts/updates) of the stock synchronization process in SyncLog.

#### Type
Table (T-SQL/SQL Server), Table (T-SQL/SQL Server), Table (T-SQL/SQL Server), Procedure (T-SQL/SQL Server).

#### Schema
dbo.StockLevel:
WarehouseId INT NOT NULL (PK)
Sku NVARCHAR(40) NOT NULL (PK)
OnHand INT NOT NULL DEFAULT 0
UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
dbo.SupplierFeed:
FeedRowId BIGINT IDENTITY(1, 1) NOT NULL (PK)
WarehouseId INT NOT NULL
Sku NVARCHAR(40) NOT NULL
Quantity INT NOT NULL
ReceivedAt DATETIME2(0) NOT NULL
dbo.SyncLog:
SyncLogId INT IDENTITY(1, 1) NOT NULL (PK)
StartedAt DATETIME2(0) NOT NULL
RowsInserted INT NOT NULL
RowsUpdated INT NOT NULL
ErrorMessage NVARCHAR(4000) NULL

#### Reads
dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt)
dbo.StockLevel (WarehouseId, Sku, OnHand, UpdatedAt)

#### Writes
dbo.StockLevel (WarehouseId, Sku, OnHand, UpdatedAt) - Updated/Inserted by MERGE
dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage) - Inserted by procedure
@Changes (Temporary table)

#### Relationships
dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId
dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku

#### Routine logic
dbo.usp_SyncStock:
* Accepts parameter @Since (DATETIME2(0)) defining the start time for feed processing.
* Executes within a transaction block.
* Aggregates quantities from dbo.SupplierFeed where ReceivedAt >= @Since, grouped by WarehouseId and Sku.
* MERGE operation updates dbo.StockLevel.OnHand if matched, or inserts new records if not matched.
* Logs the count of INSERT and UPDATE actions into dbo.SyncLog using the OUTPUT clause.
* Catches errors, rolls back the transaction, and logs the error message in dbo.SyncLog.

#### Dependencies
dbo.SupplierFeed
dbo.StockLevel
dbo.SyncLog
SYSUTCDATETIME()
