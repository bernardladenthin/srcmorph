### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:14:12Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Consolidates newly received supplier-feed quantities into warehouse stock levels and records the outcome of each synchronization.

#### Purpose
- Add feed quantities received since a cutoff to stock for each warehouse and SKU, creating stock rows when needed.
- Log inserted and updated row counts on success, or the error message on failure.

#### Responsibilities
- **Stock synchronization:** Aggregate `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`, then merge them into `dbo.StockLevel`.
- **Outcome logging:** Record synchronization start time and row counts in `dbo.SyncLog`; capture failure details if synchronization errors.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — runs the transactional feed aggregation, stock merge, and outcome logging.
- `dbo.StockLevel` — stores on-hand quantities keyed by warehouse and SKU, with update timestamps.
- `dbo.SupplierFeed` — holds supplier quantities and receipt timestamps.
- `dbo.SyncLog` — stores synchronization start times, insert/update counts, and optional error messages.
- `@Changes` — table variable capturing `MERGE` actions for counting inserts and updates.

#### Data flow
- Read `dbo.SupplierFeed` rows with `ReceivedAt >= @Since`; group by `WarehouseId` and `Sku` and sum `Quantity`.
- `MERGE` the aggregates into `dbo.StockLevel`: insert unmatched pairs or add quantities to matched rows and update `UpdatedAt`.
- Count captured insert/update actions and write a success row to `dbo.SyncLog`; on failure, roll back, log the error with zero counts, and rethrow.

#### Dependencies
- Internal tables: `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- SQL Server built-ins: `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- The synchronization runs in a transaction; errors trigger rollback when a transaction is active, are logged, and are rethrown.
- Stock identity is the `WarehouseId` and `Sku` pair; the schema declares no foreign keys between the feed and stock tables.
