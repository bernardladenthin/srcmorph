### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:54:11Z
- G: 
- A: 0.0.0
- X: file
---
> The file defines a nightly stock synchronization process that merges supplier feed data into a stock level table and records the operation results.

#### Purpose
- Synchronizes supplier feed quantities into the current stock levels per warehouse and SKU.
- Logs the number of rows inserted, updated, and any error messages.

#### Type
SQL Server (T-SQL) script containing tables, a stored procedure, and primary keys.

#### Schema
**dbo.StockLevel**
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- OnHand INT NOT NULL DEFAULT 0
- UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME()
- Primary key: (WarehouseId, Sku)

**dbo.SupplierFeed**
- FeedRowId BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY
- WarehouseId INT NOT NULL
- Sku NVARCHAR(40) NOT NULL
- Quantity INT NOT NULL
- ReceivedAt DATETIME2(0) NOT NULL

**dbo.SyncLog**
- SyncLogId INT IDENTITY(1,1) NOT NULL PRIMARY KEY
- StartedAt DATETIME2(0) NOT NULL
- RowsInserted INT NOT NULL
- RowsUpdated INT NOT NULL
- ErrorMessage NVARCHAR(4000) NULL

#### Reads
- `dbo.SupplierFeed` (reads ReceivedAt, WarehouseId, Sku, Quantity in the WHERE/GROUP BY subquery)
- `dbo.StockLevel` (reads in the MERGE target)
- `dbo.SyncLog` (reads in the INSERT statement for logging)

#### Writes
- `dbo.StockLevel` (via MERGE UPDATE and INSERT actions)
- `dbo.SyncLog` (via INSERT in success path and error path)

#### Relationships
- `dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId`
- `dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku`
- `dbo.SupplierFeed.ReceivedAt` used for filtering by `@Since`
- `dbo.StockLevel.UpdatedAt` updated on match
- `dbo.SyncLog.StartedAt` set to procedure start time

#### Routine logic
- Procedure `dbo.usp_SyncStock` takes `@Since DATETIME2(0)` parameter.
- Sets `@Started` to current UTC time.
- Begins a transaction.
- Performs a MERGE from a grouped supplier feed (WarehouseId, Sku, summed Quantity) where ReceivedAt >= @Since.
  - MATCHED: updates OnHand = OnHand + Quantity and sets UpdatedAt.
  - NOT MATCHED BY TARGET: inserts new row with OnHand = Quantity.
  - OUTPUT $action into a local table `@Changes`.
- Inserts into `dbo.SyncLog` counting inserts and updates from `@Changes`.
- Commits transaction on success.
- On error: rolls back, inserts log row with error message, re-throws error.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- System function `SYSUTCDATETIME()`
- Error function `ERROR_MESSAGE()`
