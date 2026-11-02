### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:03:20Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier inventory quantities to warehouse stock levels and records the synchronization results.

#### Purpose
- Aggregate recent supplier feed quantities by warehouse and SKU, then add them to existing stock or create stock-level rows.
- Record per-run insert/update counts, or log an error and rethrow it.

#### Responsibilities
- **Feed aggregation:** Filter `dbo.SupplierFeed` by `ReceivedAt >= @Since` and sum `Quantity` per warehouse and SKU.
- **Stock synchronization:** Match aggregates against `dbo.StockLevel`, updating existing quantities and timestamps or inserting new stock rows.
- **Run logging:** Write successful action counts or failure details to `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — aggregates recent feed rows, applies stock changes, and logs the outcome.
- `dbo.SupplierFeed` — stores supplier quantities by warehouse, SKU, and receipt time.
- `dbo.StockLevel` — stores on-hand quantities keyed by `(WarehouseId, Sku)`.
- `dbo.SyncLog` — records synchronization start time, inserted and updated counts, and optional error message.
- `@Changes` — captures `MERGE` actions for counting inserts and updates.

#### Data flow
- Read feed rows since `@Since` → sum quantities by `(WarehouseId, Sku)` → `MERGE` into stock levels → count captured insert/update actions → write a success log.
- On failure, roll back if needed → log the error with zero counts → rethrow.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and writes `dbo.StockLevel` and `dbo.SyncLog`.
- Stock matching uses `WarehouseId` and `Sku`.

#### Cross-cutting
- Stock changes run in a transaction; failures trigger rollback when needed.
- Start times use UTC; `StockLevel.UpdatedAt` is refreshed on matched-row updates.
- Errors are recorded using `ERROR_MESSAGE()` and rethrown.
