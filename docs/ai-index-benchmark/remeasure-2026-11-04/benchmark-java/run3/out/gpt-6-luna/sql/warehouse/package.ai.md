### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:47:06Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse on-hand inventory from recent supplier quantities and records the outcome of each run.

#### Purpose
- Apply supplier-feed quantities received since a supplied timestamp to stock levels grouped by warehouse and SKU.
- Record successful insert/update counts or synchronization errors.

#### Responsibilities
- Aggregate `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`, filtered by `ReceivedAt >= @Since`.
- Merge aggregates into `dbo.StockLevel`, inserting missing warehouse/SKU pairs and updating matched stock and timestamps.
- Log synchronization results in `dbo.SyncLog`; roll back and log the error if processing fails.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — transactionally aggregates feed rows, merges stock, logs counts, and rethrows errors after logging.
- `dbo.StockLevel` — stores on-hand inventory keyed by `(WarehouseId, Sku)`, with defaulted `OnHand` and UTC `UpdatedAt`.
- `dbo.SupplierFeed` — stores supplier quantities and receipt timestamps for warehouse/SKU aggregation.
- `dbo.SyncLog` — stores run start time, inserted and updated row counts, and an optional error message.
- `@Changes` — table variable capturing `MERGE` actions through `OUTPUT $action` for insert/update counts.

#### Data flow
- `@Since` filters `dbo.SupplierFeed`; quantities are summed per warehouse and SKU.
- Aggregates are matched against `dbo.StockLevel` by `(WarehouseId, Sku)`; unmatched keys are inserted, matched keys have `OnHand` and `UpdatedAt` updated.
- Captured merge actions produce success counts for `dbo.SyncLog`; on failure, the transaction is rolled back and the error is logged with zero counts before rethrowing.

#### Dependencies
- SQL Server T-SQL; `SYSUTCDATETIME()` supplies timestamps and `ERROR_MESSAGE()` supplies failure details.
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and `dbo.StockLevel`, and writes `dbo.StockLevel` and `dbo.SyncLog`.

#### Cross-cutting
- Stock identity is the `(WarehouseId, Sku)` primary key; supplier rows are grouped on the same pair.
- A transaction wraps the merge and success logging; error handling rolls back an active transaction, logs the failure, and rethrows.
