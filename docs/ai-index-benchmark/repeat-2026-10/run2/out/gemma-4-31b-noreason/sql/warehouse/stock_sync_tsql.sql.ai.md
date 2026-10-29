### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:57:49Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the synchronization of warehouse stock levels by aggregating data from a supplier feed and logging the operation results.

#### Purpose
* Synchronize `StockLevel` based on `SupplierFeed` entries received after a specific timestamp.
* Log synchronization metrics and errors into `SyncLog`.

#### Type
* Tables, Procedure (T-SQL / SQL Server).

#### Schema
* `dbo.StockLevel`:
    * `WarehouseId INT` NOT NULL (PK)
    * `Sku NVARCHAR(40)` NOT NULL (PK)
    * `OnHand INT` NOT NULL DEFAULT (0)
    * `UpdatedAt DATETIME2(0)` NOT NULL DEFAULT (SYSUTCDATETIME())
* `dbo.SupplierFeed`:
    * `FeedRowId BIGINT` IDENTITY(1, 1) NOT NULL (PK)
    * `WarehouseId INT` NOT NULL
    * `Sku NVARCHAR(40)` NOT NULL
    * `Quantity INT` NOT NULL
    * `ReceivedAt DATETIME2(0)` NOT NULL
* `dbo.SyncLog`:
    * `SyncLogId INT` IDENTITY(1, 1) NOT NULL (PK)
    * `StartedAt DATETIME2(0)` NOT NULL
    * `RowsInserted INT` NOT NULL
    * `RowsUpdated INT` NOT NULL
    * `ErrorMessage NVARCHAR(4000)` NULL

#### Reads
* `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`.
* `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand`.

#### Writes
* `dbo.StockLevel`: `WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`.
* `dbo.SyncLog`: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.

#### Relationships
* `dbo.StockLevel.WarehouseId` -> `dbo.SupplierFeed.WarehouseId`
* `dbo.StockLevel.Sku` -> `dbo.SupplierFeed.Sku`

#### Routine logic
* `dbo.usp_SyncStock`:
    * Parameter: `@Since DATETIME2(0)` (filter for feed records).
    * Aggregates `Quantity` from `dbo.SupplierFeed` grouped by `WarehouseId` and `Sku`.
    * Performs `MERGE` into `dbo.StockLevel`:
        * If match: Increments `OnHand` and updates `UpdatedAt`.
        * If no match: Inserts new record.
    * Captures merge actions (`INSERT`/`UPDATE`) into a table variable.
    * Records total rows inserted and updated into `dbo.SyncLog`.
    * Wraps operation in a transaction with a `TRY...CATCH` block to log `ERROR_MESSAGE()` on failure.

#### Dependencies
* `dbo.StockLevel`
* `dbo.SupplierFeed`
* `dbo.SyncLog`
