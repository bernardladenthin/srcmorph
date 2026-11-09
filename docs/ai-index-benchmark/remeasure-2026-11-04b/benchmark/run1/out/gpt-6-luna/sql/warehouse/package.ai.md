### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:51:03Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Supplier inventory updates are aggregated by warehouse and SKU, applied to stock levels, and recorded with synchronization results.

#### Purpose
- Synchronize `dbo.StockLevel` using supplier feed quantities received since a specified time.
- Record inserted and updated row counts, or error details, for each synchronization attempt.

#### Responsibilities
- Aggregate recent `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`.
- Insert new stock keys or add feed totals to existing `OnHand` values, updating timestamps.
- Log synchronization outcomes in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since)`: aggregates eligible feed rows, merges totals into stock, and logs success or failure.
- `dbo.SupplierFeed`: source rows keyed by warehouse, SKU, quantity, and received time.
- `dbo.StockLevel`: current quantities keyed by `WarehouseId` and `Sku`.
- `dbo.SyncLog`: per-call start time, insert/update counts, and optional error message.

#### Data flow
- Filter `dbo.SupplierFeed` by `ReceivedAt >= @Since`; group by warehouse and SKU and sum quantities.
- `MERGE` totals into `dbo.StockLevel`: insert unmatched keys or increment matching `OnHand` values and refresh `UpdatedAt`.
- Capture `MERGE` actions in `@Changes`, count inserts and updates, and write the result to `dbo.SyncLog`.

#### Dependencies
- SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- `SYSUTCDATETIME()` for start and update timestamps; `ERROR_MESSAGE()` for failure logging.

#### Cross-cutting
- `dbo.usp_SyncStock` wraps the merge in a transaction; on failure it rolls back any active transaction, logs zero counts and the error, then rethrows.
