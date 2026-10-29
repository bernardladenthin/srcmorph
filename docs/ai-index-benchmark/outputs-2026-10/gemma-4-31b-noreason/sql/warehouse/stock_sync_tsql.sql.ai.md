### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:53:54Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the synchronization of warehouse stock levels by aggregating quantities from a supplier feed and updating the inventory state.

#### Purpose
* Maintain current stock levels per warehouse and SKU.
* Process incremental supplier feed data into stock totals.
* Log synchronization execution results and errors.

#### Type
Tables, Procedure (T-SQL / SQL Server).

#### Schema
dbo.StockLevel
* WarehouseId INT NOT NULL (PK)
* Sku NVARCHAR(40) NOT NULL (PK)
* OnHand INT NOT NULL DEFAULT (0)
* UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())

dbo.SupplierFeed
* FeedRowId BIGINT IDENTITY(1, 1) NOT NULL (PK)
* WarehouseId INT NOT NULL
* Sku NVARCHAR(40) NOT NULL
* Quantity INT NOT NULL
* ReceivedAt DATETIME2(0) NOT NULL

dbo.SyncLog
* SyncLogId INT IDENTITY(1, 1) NOT NULL (PK)
* StartedAt DATETIME2(0) NOT NULL
* RowsInserted INT NOT NULL
* RowsUpdated INT NOT NULL
* ErrorMessage NVARCHAR(4000) NULL

#### Reads
* dbo.SupplierFeed: WarehouseId, Sku, Quantity, ReceivedAt.
* dbo.StockLevel: WarehouseId, Sku, OnHand.

#### Writes
* dbo.StockLevel: OnHand, UpdatedAt, WarehouseId, Sku.
* dbo.SyncLog: StartedAt, RowsInserted, RowsUpdated, ErrorMessage.

#### Relationships
* dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId
* dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku

#### Routine logic
dbo.usp_SyncStock
* Parameter: @Since (DATETIME2(0)) filters the feed data.
* Aggregates `Quantity` from `dbo.SupplierFeed` grouped by `WarehouseId` and `Sku` for records since @Since.
* Performs a MERGE into `dbo.StockLevel`:
    * If match: Increments `OnHand` by the aggregated quantity and updates `UpdatedAt`.
    * If no match: Inserts new record with aggregated quantity.
* Captures merge actions into a table variable `@Changes`.
* Inserts a summary record into `dbo.SyncLog` counting 'INSERT' and 'UPDATE' actions.
* Wraps operation in a transaction with a TRY/CATCH block to log `ERROR_MESSAGE()` on failure.

#### Dependencies
* dbo.StockLevel
* dbo.SupplierFeed
* dbo.SyncLog
