### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:30:07Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse stock levels by aggregating quantities from a supplier feed and logging the operation results.

#### Purpose
* Maintain current stock levels per warehouse and SKU based on supplier feed updates.
* Log synchronization history and errors.

#### Type
* Tables, Procedure (T-SQL / SQL Server).

#### Schema
* dbo.StockLevel: `WarehouseId INT` (PK), `Sku NVARCHAR(40)` (PK), `OnHand INT` (NOT NULL, DEFAULT 0), `UpdatedAt DATETIME2(0)` (NOT NULL, DEFAULT SYSUTCDATETIME()).
* dbo.SupplierFeed: `FeedRowId BIGINT` (PK, IDENTITY), `WarehouseId INT` (NOT NULL), `Sku NVARCHAR(40)` (NOT NULL), `Quantity INT` (NOT NULL), `ReceivedAt DATETIME2(0)` (NOT NULL).
* dbo.SyncLog: `SyncLogId INT` (PK, IDENTITY), `StartedAt DATETIME2(0)` (NOT NULL), `RowsInserted INT` (NOT NULL), `RowsUpdated INT` (NOT NULL), `ErrorMessage NVARCHAR(4000)` (NULL).

#### Reads
* dbo.SupplierFeed: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`.
* dbo.StockLevel: `WarehouseId`, `Sku`, `OnHand`.

#### Writes
* dbo.StockLevel: `WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`.
* dbo.SyncLog: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.

#### Relationships
* dbo.SupplierFeed.WarehouseId -> dbo.StockLevel.WarehouseId
* dbo.SupplierFeed.Sku -> dbo.StockLevel.Sku

#### Routine logic
* dbo.usp_SyncStock:
    * Input: `@Since DATETIME2(0)` (filter for feed records).
    * Aggregates `Quantity` from `dbo.SupplierFeed` grouped by `WarehouseId` and `Sku` for records where `ReceivedAt >= @Since`.
    * MERGE operation:
        * If `WarehouseId` and `Sku` match in `dbo.StockLevel`, increments `OnHand` and updates `UpdatedAt`.
        * If no match, inserts new record into `dbo.StockLevel`.
    * Captures merge actions into a table variable `@Changes`.
    * Inserts summary counts (INSERTs vs UPDATEs) and start time into `dbo.SyncLog`.
    * Wraps logic in a transaction with a TRY/CATCH block to log `ERROR_MESSAGE()` on failure.

#### Dependencies
* dbo.StockLevel
* dbo.SupplierFeed
* dbo.SyncLog
