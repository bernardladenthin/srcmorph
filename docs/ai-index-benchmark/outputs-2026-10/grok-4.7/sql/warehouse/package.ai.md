### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:47:29Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
I will produce a dense, structured markdown summary of the package based on the provided file summaries.> Nightly warehouse stock synchronisation that merges a supplier feed into on-hand stock levels and logs the outcome.

#### Purpose
- Models warehouse stock, inbound supplier feed rows, and sync run history for a nightly stock synchronisation.
- `dbo.usp_SyncStock` merges feed quantities received since `@Since` into `dbo.StockLevel` and records insert/update counts or the error in `dbo.SyncLog`.

#### Responsibilities
- Persist on-hand stock by warehouse and SKU (`dbo.StockLevel`).
- Accept inbound supplier feed quantities (`dbo.SupplierFeed`).
- Merge aggregated feed rows into stock and log success or failure (`dbo.usp_SyncStock`, `dbo.SyncLog`).

#### Key units
- `dbo.StockLevel`: composite key `(WarehouseId, Sku)` with `OnHand` and `UpdatedAt` defaults.
- `dbo.SupplierFeed`: identity `FeedRowId` plus `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`.
- `dbo.SyncLog`: identity `SyncLogId` with `StartedAt`, `RowsInserted`, `RowsUpdated`, nullable `ErrorMessage`.
- `dbo.usp_SyncStock`: T-SQL procedure taking `@Since DATETIME2(0)`.

#### Data flow
- Parameter `@Since` filters `dbo.SupplierFeed` on `ReceivedAt >= @Since`.
- Feed is aggregated as `SUM(Quantity)` grouped by `WarehouseId`, `Sku`.
- In a transaction, `MERGE` matches `dbo.StockLevel` on `WarehouseId` and `Sku`: matched rows add quantity to `OnHand` and set `UpdatedAt`; unmatched rows insert `WarehouseId`, `Sku`, `OnHand`.
- `$action` is captured in table variable `@Changes`; success writes one `dbo.SyncLog` row with insert and update counts, then `COMMIT`.
- On error: `ROLLBACK` if `@@TRANCOUNT > 0`, insert a failed `dbo.SyncLog` row with `ERROR_MESSAGE()`, then `THROW`.

#### Dependencies
- Internal: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`, `dbo.usp_SyncStock`.
- Logical match only (no declared foreign keys): `SupplierFeed.WarehouseId` + `SupplierFeed.Sku` to `StockLevel.WarehouseId` + `StockLevel.Sku`.
- Built-ins: `SYSUTCDATETIME`, `ERROR_MESSAGE`; dialect SQL Server T-SQL (`MERGE`, `IDENTITY`, `THROW`, `DATETIME2`).

#### Cross-cutting
- Defaults: `DF_StockLevel_OnHand` (`0`), `DF_StockLevel_UpdatedAt` (`SYSUTCDATETIME()`).
- Keys: `PK_StockLevel`, `PK_SupplierFeed`, `PK_SyncLog`.
- Procedure sets `NOCOUNT ON`, captures `@Started` as `SYSUTCDATETIME()`, and uses a transactional `MERGE` with catch-path logging and rethrow.
