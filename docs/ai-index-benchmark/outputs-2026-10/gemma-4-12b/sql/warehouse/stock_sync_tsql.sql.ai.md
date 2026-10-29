### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T04:13:34Z
- G: 
- A: 0.0.0
- X: file
---
> This script manages the nightly synchronization of warehouse stock levels by aggregating and merging data from a supplier feed into a master inventory table.

#### Purpose
* Synchronizes `StockLevel` inventory counts based on incoming `SupplierFeed` data.
* Records execution metrics and errors in a `SyncLog` table.

#### Type
Table, Procedure (T-SQL / SQL Server)

#### Schema
#### dbo.StockLevel
* WarehouseId INT NOT NULL
* Sku NVARCHAR(40) NOT NULL
* OnHand INT NOT NULL DEFAULT (0)
* UpdatedAt DATETIME2(0) NOT NULL DEFAULT (SYSUTCDATETIME())
* PK (WarehouseId, Sku)

#### dbo.SupplierFeed
* FeedRowId BIGINT IDENTITY(1, 1) NOT NULL PK
* WarehouseId INT NOT NULL
* Sku NVARCHAR(40) NOT NULL
* Quantity INT NOT NULL
* ReceivedAt DATETIME2(0) NOT NULL

#### dbo.SyncLog
* SyncLogId INT IDENTITY(1, 1) NOT NULL PK
* StartedAt DATETIME2(0) NOT NULL
* RowsInserted INT NOT NULL
* RowsUpdated INT NOT NULL
* ErrorMessage NVARCHAR(4000) NULL

#### Reads
* dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt)

#### Writes
* dbo.StockLevel (WarehouseId, Sku, OnHand, UpdatedAt)
* dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage)

#### Relationships
* dbo.StockLevel (WarehouseId, Sku) matches dbo.SupplierFeed (WarehouseId, Sku)

#### Routine logic
#### dbo.usp_SyncStock
* Parameters: @Since DATETIME2(0)
* Aggregates `SupplierFeed` records by `WarehouseId` and `Sku` where `ReceivedAt` >= @Since.
* Performs a `MERGE` into `StockLevel`:
    * MATCHED: Increments `OnHand` by the sum of feed quantities and updates `UpdatedAt`.
    * NOT MATCHED: Inserts new `WarehouseId`, `Sku`, and `OnHand`.
* Captures `INSERT` and `UPDATE` counts via the `OUTPUT` clause into a table variable.
* Inserts summary statistics into `SyncLog`.
* Implements transaction handling with `ROLLBACK` on error and logs `ERROR_MESSAGE()` to `SyncLog`.

#### Dependencies
* dbo.StockLevel
* dbo.SupplierFeed
* dbo.SyncLog
