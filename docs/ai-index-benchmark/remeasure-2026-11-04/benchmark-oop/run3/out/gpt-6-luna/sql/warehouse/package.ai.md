### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:54:06Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier-feed quantities to warehouse stock by SKU and records synchronization results or errors.

#### Purpose
- Consolidate supplier feed rows from a cutoff time into stock updates or new warehouse/SKU stock records.
- Track successful merge counts and failed runs in a synchronization log.

#### Responsibilities
- Aggregate qualifying `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`.
- Merge aggregates into `dbo.StockLevel`, updating existing quantities and timestamps or inserting new rows.
- Record run start time, insert/update counts, and any error message in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — performs the transactional stock synchronization and records its outcome.
- `dbo.StockLevel` — stores on-hand quantity per warehouse and SKU.
- `dbo.SupplierFeed` — provides timestamped warehouse/SKU quantities.
- `dbo.SyncLog` — stores synchronization counts or error details.

#### Data flow
- Filter `dbo.SupplierFeed` on `ReceivedAt >= @Since` and aggregate `Quantity` by `WarehouseId` and `Sku`.
- `MERGE` aggregates into `dbo.StockLevel`; capture insert/update actions in `@Changes`.
- Count actions and write a success row to `dbo.SyncLog`; on error, roll back if needed, log the error, and rethrow.

#### Dependencies
- SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- `SYSUTCDATETIME()` for the run start time and `ERROR_MESSAGE()` for error logging.

#### Cross-cutting
- The stock merge and success logging run in a transaction; errors trigger rollback when needed, error logging, and rethrow.
- `dbo.StockLevel` uses `(WarehouseId, Sku)` as its composite primary key; quantity and update-time columns have defaults.
