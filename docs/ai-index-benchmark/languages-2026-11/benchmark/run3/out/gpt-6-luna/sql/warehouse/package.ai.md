### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:02:11Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies newly received supplier quantities to warehouse stock by warehouse and SKU, while recording synchronization results.

#### Purpose
- Aggregate supplier feed quantities since a supplied cutoff and add them to existing stock or create stock rows.
- Record inserted and updated counts on success, or log and rethrow synchronization errors.

#### Responsibilities
- **Feed aggregation and stock updates:** Group recent `dbo.SupplierFeed` rows by `WarehouseId` and `Sku`, sum `Quantity`, then update or insert matching `dbo.StockLevel` rows.
- **Synchronization auditing:** Write start time and merge-action counts to `dbo.SyncLog`; record error details with zero counts on failure.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — synchronizes aggregated supplier quantities and logs the outcome.
- `dbo.SupplierFeed` — stores warehouse/SKU quantities with receipt timestamps.
- `dbo.StockLevel` — stores on-hand quantities keyed by warehouse and SKU.
- `dbo.SyncLog` — stores synchronization start time, inserted and updated counts, and optional error message.

#### Data flow
- Read `dbo.SupplierFeed` rows with `ReceivedAt >= @Since`, grouped by warehouse and SKU with summed quantities.
- `MERGE` the aggregates into `dbo.StockLevel`: add quantities and update `UpdatedAt` for matches, or insert unmatched rows.
- Log merge-action counts and commit; on error, roll back any active transaction, log the error, and rethrow.

#### Dependencies
- Uses `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- No declared foreign keys.

#### Cross-cutting
- Wraps stock changes and success logging in a transaction; captures a UTC start time.
- Error handling rolls back an active transaction, writes `ERROR_MESSAGE()` with zero counts, and rethrows.
