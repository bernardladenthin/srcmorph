### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:39:51Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier inventory feeds to warehouse stock balances and records each synchronization result.

#### Purpose
- Aggregate supplier quantities by warehouse and SKU, then increment existing stock or create missing stock records.
- Track successful and failed synchronization runs in an audit log.

#### Responsibilities
- **Feed aggregation and stock updates:** Filter supplier feed rows by cutoff, group by warehouse and SKU, and merge totals into stock levels.
- **Run auditing and error reporting:** Record inserted/updated counts on success, or zero counts and the error message on failure.

#### Key units
- `dbo.usp_SyncStock(@Since)`: Transactionally applies eligible feed quantities, logs the outcome, and rethrows failures.
- `dbo.StockLevel`: Current on-hand quantities keyed by `(WarehouseId, Sku)`.
- `dbo.SupplierFeed`: Timestamped supplier quantities for warehouse and SKU.
- `dbo.SyncLog`: Per-run start time, inserted/updated counts, and optional error message.
- `@Changes`: Captures `MERGE` actions to calculate insert and update counts.

#### Data flow
- `dbo.SupplierFeed` rows with `ReceivedAt >= @Since` are grouped by `(WarehouseId, Sku)` and their quantities summed.
- The aggregates are merged into `dbo.StockLevel`: unmatched keys are inserted; matched balances are incremented and `UpdatedAt` refreshed.
- Merge actions determine success counts, which are written to `dbo.SyncLog` before commit; on error, the transaction is rolled back, a failure log row is written, and the error is rethrown.

#### Dependencies
- SQL Server tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- SQL Server built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses a transaction for stock changes and success logging; failure handling rolls back any active transaction before logging.
- Uses UTC timestamps for run start and stock updates.
