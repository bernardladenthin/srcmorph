### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:33Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse stock balances from recent supplier inventory feeds and records each synchronization’s results or errors.

#### Purpose
- Aggregate supplier quantities by warehouse and SKU, then apply them to stock balances.
- Audit successful row counts and failures in a synchronization log.

#### Responsibilities
- Define stock, supplier-feed, and sync-log tables with their keys, defaults, and fields.
- Implement transactional stock merging, success logging, and error handling.

#### Key units
- `dbo.usp_SyncStock(@Since)` — aggregates feed rows received since the cutoff, updates stock, logs merge counts, and rolls back and logs caught errors.
- `dbo.SupplierFeed` — stores supplier inventory quantities by warehouse, SKU, and receipt time.
- `dbo.StockLevel` — stores on-hand balances keyed by warehouse and SKU.
- `dbo.SyncLog` — records synchronization start time, inserted and updated counts, and optional error messages.

#### Data flow
- Filter `dbo.SupplierFeed` by `ReceivedAt >= @Since` and aggregate `Quantity` by `WarehouseId` and `Sku`.
- Merge aggregates into `dbo.StockLevel`: insert unmatched pairs and add quantities to matched balances.
- Capture merge actions to count inserts and updates; log counts and commit, or roll back and log the caught error.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and merges into `dbo.StockLevel`; it writes outcomes to `dbo.SyncLog`.

#### Cross-cutting
- Stock changes run in a transaction; caught failures roll back any active transaction, log `ERROR_MESSAGE()`, and rethrow.
- Stock timestamps use `SYSUTCDATETIME()` by default; procedure logs capture a UTC start time.
