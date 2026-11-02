### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:51:59Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes recent supplier inventory feeds into warehouse stock levels and records each run’s results or failure.

#### Purpose
- Aggregate supplier quantities received since a cutoff and apply them to warehouse stock.
- Log synchronization row counts on success or the error message on failure.

#### Responsibilities
- **Feed aggregation and stock updates:** Group eligible `dbo.SupplierFeed` rows by warehouse and SKU; add quantities to existing `dbo.StockLevel` rows or insert new ones.
- **Run auditing:** Record start time and inserted/updated counts, or error details, in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — transactionally merges aggregated feed quantities into stock and logs the outcome.
- `dbo.StockLevel` — stores on-hand quantities keyed by `(WarehouseId, Sku)`, with `UpdatedAt` timestamps.
- `dbo.SupplierFeed` — stores received supplier quantities by warehouse, SKU, and receipt time.
- `dbo.SyncLog` — stores synchronization start time, row counts, and optional error message.

#### Data flow
- Select `SupplierFeed` rows where `ReceivedAt >= @Since`, group by `(WarehouseId, Sku)`, and sum `Quantity`.
- Merge aggregates into `StockLevel`: add quantities and refresh `UpdatedAt` for matches; insert unmatched keys.
- Count merge insert/update actions, then commit and log success; on error, roll back if active, log the error with zero counts, and rethrow.

#### Dependencies
- Uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- Uses `SYSUTCDATETIME()` for UTC timestamps and `ERROR_MESSAGE()` for failure logging.

#### Cross-cutting
- Stock identity is the `(WarehouseId, Sku)` pair; no foreign key is declared between feed and stock tables.
- Stock changes run in a transaction; failure handling rolls back active work, records the error, and rethrows.
