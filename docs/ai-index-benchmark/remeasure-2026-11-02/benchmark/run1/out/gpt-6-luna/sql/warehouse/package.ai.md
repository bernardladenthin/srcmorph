### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:30:05Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies newly received supplier quantities to warehouse SKU stock and records synchronization results.

#### Purpose
- Aggregate supplier-feed quantities since a cutoff and add them to warehouse stock.
- Log inserted and updated row counts, or the error if synchronization fails.

#### Responsibilities
- **Feed aggregation and stock updates:** Group `dbo.SupplierFeed` quantities by warehouse and SKU, then insert new stock rows or increase existing balances.
- **Sync outcome logging:** Record successful row counts in `dbo.SyncLog`; on failure, log the error and rethrow it.

#### Key units
- `dbo.usp_SyncStock(@Since)`: transactionally aggregates recent feed rows, merges quantities into stock, and logs the result.
- `dbo.StockLevel`: stores on-hand quantity and update time for each `(WarehouseId, Sku)`.
- `dbo.SupplierFeed`: holds received supplier quantities, keyed by `FeedRowId`.
- `dbo.SyncLog`: records sync start time, inserted and updated counts, and optional error message.

#### Data flow
- Read `dbo.SupplierFeed` rows with `ReceivedAt >= @Since`; sum `Quantity` per `WarehouseId, Sku`.
- Match aggregates against `dbo.StockLevel`: add to `OnHand` for matches or insert new rows; capture merge actions.
- Log inserted and updated counts in `dbo.SyncLog`, then commit; on error, roll back, log `ERROR_MESSAGE()` with zero counts, and rethrow.

#### Dependencies
- SQL Server T-SQL; `dbo.usp_SyncStock` operates on `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- `dbo.StockLevel` primary key on `(WarehouseId, Sku)` supports the merge match.

#### Cross-cutting
- Uses a transaction with rollback on failure and rethrows errors after logging.
- Captures a UTC start time; stock updates set `UpdatedAt`.
