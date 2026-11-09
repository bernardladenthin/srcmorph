### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:32:11Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse on-hand inventory from recent supplier-feed quantities and records each synchronization’s outcome.

#### Purpose
- Apply aggregated supplier quantities to warehouse/SKU stock levels for feed rows received since a specified timestamp.
- Record inserted and updated row counts on success, or the synchronization error on failure.

#### Responsibilities
- Aggregate recent `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`.
- Insert new `dbo.StockLevel` rows or add quantities to existing stock, updating timestamps.
- Log synchronization results in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since)` — runs the feed aggregation, stock merge, and result logging.
- `dbo.StockLevel` — stores on-hand quantities keyed by warehouse and SKU.
- `dbo.SupplierFeed` — stores supplier quantities and their received timestamps.
- `dbo.SyncLog` — stores synchronization start time, inserted/updated counts, and optional error message.
- `@Changes` — captures merge actions used to calculate success-log counts.

#### Data flow
- Filter `dbo.SupplierFeed` on `ReceivedAt >= @Since` and sum `Quantity` per warehouse/SKU.
- Merge aggregated totals into `dbo.StockLevel`: add quantities to matches or insert unmatched stock rows.
- Count merge inserts and updates through `@Changes`, then write a success row to `dbo.SyncLog`.
- On failure, roll back if needed, log `ERROR_MESSAGE()` with zero counts, and rethrow.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and `dbo.StockLevel`, and writes `dbo.StockLevel` and `dbo.SyncLog`.
- Stock rows are matched by `WarehouseId` and `Sku`.

#### Cross-cutting
- Synchronization runs in a transaction and rolls back on failure before error logging.
- Start time uses UTC; `dbo.StockLevel.UpdatedAt` defaults to `SYSUTCDATETIME()`.
