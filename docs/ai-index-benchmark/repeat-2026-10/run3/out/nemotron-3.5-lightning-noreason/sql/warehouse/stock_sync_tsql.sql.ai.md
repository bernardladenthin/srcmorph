### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:08Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronises nightly stock levels from a supplier feed into the warehouse stock table and logs the operation.

#### Purpose
- Merges new and updated stock quantities from the supplier feed into the StockLevel table.
- Logs sync results (inserts, updates, errors) into SyncLog.

#### Type
Procedure (T-SQL)

#### Schema
**dbo.StockLevel**
- WarehouseId INT NOT NULL (part of PK)
- Sku NVARCHAR(40) NOT NULL (part of PK)
- OnHand INT NOT NULL DEFAULT (0)
- UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
- PK: (WarehouseId, Sku)

**dbo.SupplierFeed**
- FeedRowId BIGINT IDENTITY(1,1) NOT NULL PK
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- Quantity INT NOT NULL
- ReceivedAt DATETIME2(0) NOT NULL

**dbo.SyncLog**
- SyncLogId INT IDENTITY(1,1) NOT NULL PK
- StartedAt DATETIME2(0) NOT NULL
- RowsInserted INT NOT NULL
- RowsUpdated INT NOT NULL
- ErrorMessage NVARCHAR(4000) NULL

#### Reads
- dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt) – filtered by ReceivedAt >= @Since, grouped by WarehouseId, Sku
- dbo.StockLevel (WarehouseId, Sku, OnHand, UpdatedAt) – target of merge

#### Writes
- dbo.StockLevel – merged: OnHand and UpdatedAt updated when matched; new rows inserted when not matched
- dbo.SyncLog – inserts StartedAt, RowsInserted, RowsUpdated, and optionally ErrorMessage

#### Relationships
- StockLevel.WarehouseId → SupplierFeed.WarehouseId (join key in MERGE)
- StockLevel.Sku → SupplierFeed.Sku (join key in MERGE)

#### Routine logic
- Declares @Started and a table variable @Changes to capture merge actions.
- Executes MERGE on dbo.StockLevel using a grouped subquery from dbo.SupplierFeed where ReceivedAt >= @Since.
- When MATCHED: updates OnHand by adding source Quantity and sets UpdatedAt.
- WHEN NOT MATCHED BY TARGET: inserts new row with WarehouseId, Sku, and OnHand from source.
- Outputs $action into @Changes.
- Inserts aggregated insert/update counts into dbo.SyncLog.
- On error: rolls back transaction, logs error into dbo.SyncLog, and re-throws.

#### Dependencies
- dbo.StockLevel
- dbo.SupplierFeed
- dbo.SyncLog
