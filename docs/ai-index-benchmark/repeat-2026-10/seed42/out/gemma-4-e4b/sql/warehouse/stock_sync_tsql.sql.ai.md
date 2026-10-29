### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:52:05Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes current stock levels in the warehouse by merging recent supplier feed data into the master stock table and logging the operation.

#### Purpose
* Executes nightly stock synchronization from supplier feed data into the StockLevel table.
* Logs the results (inserts/updates) and any errors of the synchronization process in SyncLog.

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
dbo.StockLevel (OnHand, UpdatedAt) - via MERGE UPDATE/INSERT
dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage) - via INSERT

#### Relationships
dbo.StockLevel.WarehouseId -> dbo.SupplierFeed.WarehouseId
dbo.StockLevel.Sku -> dbo.SupplierFeed.Sku

#### Routine logic
usp_SyncStock(@Since DATETIME2(0)):
* Initializes transaction and start time.
* Aggregates data from dbo.SupplierFeed where ReceivedAt >= @Since, grouping by WarehouseId and Sku.
* MERGE dbo.StockLevel:
    * MATCHED: Updates OnHand = target.OnHand + source.Quantity, UpdatedAt = SYSUTCDATETIME().
    * NOT MATCHED BY TARGET: Inserts new records into StockLevel.
* Inserts summary counts (RowsInserted, RowsUpdated) into dbo.SyncLog based on MERGE output.
* Commits transaction on success or rolls back and logs error on failure.

#### Dependencies
dbo.SupplierFeed
dbo.StockLevel
dbo.SyncLog
