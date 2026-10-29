### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:59:02Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes stock levels in the warehouse by merging recent supplier feed data into the master stock level table and logging the operation.

#### Purpose
* Executes nightly stock synchronization from a supplier feed into the StockLevel table.
* Logs the outcome (inserts/updates) of the stock synchronization process in SyncLog.

#### Type
Procedure (T-SQL)

#### Schema
dbo.StockLevel:
    WarehouseId INT NOT NULL (PK part)
    Sku NVARCHAR(40) NOT NULL (PK part)
    OnHand INT NOT NULL (Default: 0)
    UpdatedAt DATETIME2(0) NOT NULL (Default: SYSUTCDATETIME())
    PK_StockLevel (WarehouseId, Sku)

dbo.SupplierFeed:
    FeedRowId BIGINT NOT NULL (PK)
    WarehouseId INT NOT NULL
    Sku NVARCHAR(40) NOT NULL
    Quantity INT NOT NULL
    ReceivedAt DATETIME2(0) NOT NULL
    PK_SupplierFeed (FeedRowId)

dbo.SyncLog:
    SyncLogId INT NOT NULL (PK)
    StartedAt DATETIME2(0) NOT NULL
    RowsInserted INT NOT NULL
    RowsUpdated INT NOT NULL
    ErrorMessage NVARCHAR(4000) NULL
    PK_SyncLog (SyncLogId)

#### Reads
dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt)
dbo.StockLevel (WarehouseId, Sku, OnHand)

#### Writes
dbo.StockLevel (OnHand, UpdatedAt) - Updated/Inserted via MERGE
dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage) - Inserted via MERGE output

#### Relationships
dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId
dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku

#### Routine logic
usp_SyncStock(@Since DATETIME2(0)):
* Initializes transaction and start time.
* Aggregates data from dbo.SupplierFeed where ReceivedAt >= @Since, grouping by WarehouseId and Sku.
* MERGE operation targets dbo.StockLevel:
    * WHEN MATCHED: Updates OnHand = target.OnHand + source.Quantity, UpdatedAt = SYSUTCDATETIME().
    * WHEN NOT MATCHED BY TARGET: Inserts new records into dbo.StockLevel using source data.
* Captures MERGE actions (INSERT/UPDATE) into a temporary table @Changes.
* Inserts summary counts (RowsInserted, RowsUpdated) into dbo.SyncLog using @Changes.
* Commits transaction on success or rolls back and logs error on failure.

#### Dependencies
dbo.SupplierFeed
dbo.StockLevel
dbo.SyncLog
