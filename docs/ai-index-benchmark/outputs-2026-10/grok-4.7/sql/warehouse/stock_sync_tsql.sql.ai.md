### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:44:29Z
- G: 
- A: 0.0.0
- X: file
---
I need to index the provided SQL source file according to the specified format instructions.

The file contains two tables. `dbo.StockLevel` has a composite primary key on `(WarehouseId, Sku)`. It stores `OnHand` and `UpdatedAt` with default constraints. `dbo.SupplierFeed` has an identity primary key `FeedRowId` and a `WarehouseId` column.> Nightly warehouse stock synchronisation that merges a supplier feed into on-hand stock levels and logs the outcome.

#### Purpose
- Models warehouse stock, inbound supplier feed rows, and sync run history for a nightly stock synchronisation.
- `dbo.usp_SyncStock` merges feed quantities received since `@Since` into `dbo.StockLevel` and records insert/update counts or the error in `dbo.SyncLog`.

#### Type
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Procedure: `dbo.usp_SyncStock`.
- Dialect: SQL Server T-SQL (`SYSUTCDATETIME`, `MERGE`, `IDENTITY`, `THROW`, `DATETIME2`).

#### Schema
- `dbo.StockLevel`: `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `OnHand INT NOT NULL` default `0` (`DF_StockLevel_OnHand`), `UpdatedAt DATETIME2(0) NOT NULL` default `SYSUTCDATETIME()` (`DF_StockLevel_UpdatedAt`); primary key `PK_StockLevel (WarehouseId, Sku)`.
- `dbo.SupplierFeed`: `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` primary key `PK_SupplierFeed`, `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `Quantity INT NOT NULL`, `ReceivedAt DATETIME2(0) NOT NULL`.
- `dbo.SyncLog`: `SyncLogId INT IDENTITY(1,1) NOT NULL` primary key `PK_SyncLog`, `StartedAt DATETIME2(0) NOT NULL`, `RowsInserted INT NOT NULL`, `RowsUpdated INT NOT NULL`, `ErrorMessage NVARCHAR(4000) NULL`.

#### Reads
- `dbo.SupplierFeed`: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` filtered by `ReceivedAt >= @Since`, aggregated as `SUM(Quantity)` grouped by `WarehouseId`, `Sku`.
- `dbo.StockLevel` as `MERGE` target match on `WarehouseId`, `Sku`; reads `OnHand` when matched.

#### Writes
- `dbo.StockLevel`: matched rows update `OnHand` (`target.OnHand + source.Quantity`) and `UpdatedAt` (`SYSUTCDATETIME()`); unmatched rows insert `WarehouseId`, `Sku`, `OnHand`.
- `dbo.SyncLog`: success insert of `StartedAt`, `RowsInserted`, `RowsUpdated`; catch insert of `StartedAt`, `RowsInserted` `0`, `RowsUpdated` `0`, `ErrorMessage` (`ERROR_MESSAGE()`).

#### Relationships
- Logical match only (no declared foreign keys): `SupplierFeed.WarehouseId` + `SupplierFeed.Sku` -> `StockLevel.WarehouseId` + `StockLevel.Sku`.

#### Routine logic
- Parameter: `@Since DATETIME2(0)`.
- Sets `NOCOUNT ON`; captures `@Started` as `SYSUTCDATETIME()`; table variable `@Changes (MergeAction NVARCHAR(10))`.
- In a transaction, `MERGE` aggregated `SupplierFeed` into `StockLevel`; `OUTPUT $action` into `@Changes`.
- Inserts one `SyncLog` row with counts of `INSERT` and `UPDATE` actions, then `COMMIT`.
- On error: `ROLLBACK` if `@@TRANCOUNT > 0`, insert failed `SyncLog` row, `THROW`.

#### Dependencies
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `dbo.usp_SyncStock`.
- Built-ins: `SYSUTCDATETIME`, `ERROR_MESSAGE`.
