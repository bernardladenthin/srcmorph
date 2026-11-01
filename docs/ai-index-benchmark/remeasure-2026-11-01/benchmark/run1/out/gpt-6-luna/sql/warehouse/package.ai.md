### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:24:01Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse stock balances from newly received supplier quantities and records each synchronization’s outcome.

#### Purpose
- Aggregate supplier-feed quantities by warehouse and SKU, then update or create corresponding stock levels.
- Record inserted/updated counts on success, or the error message on failure.

#### Responsibilities
- **Supplier quantity aggregation:** Select `dbo.SupplierFeed` rows received since `@Since` and sum quantities by `WarehouseId` and `Sku`.
- **Stock maintenance:** Add aggregated quantities to matching `dbo.StockLevel` rows or insert new warehouse/SKU levels.
- **Execution logging:** Record synchronization start time, change counts, and any error in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — runs the aggregation, stock update, and outcome logging.
- `dbo.SupplierFeed` — stores received supplier quantities and timestamps.
- `dbo.StockLevel` — stores on-hand quantities keyed by `(WarehouseId, Sku)`.
- `dbo.SyncLog` — stores synchronization counts and error messages.
- `@Changes` — captures `MERGE` actions for inserted and updated row counts.

#### Data flow
- Read supplier rows where `ReceivedAt >= @Since`; group by warehouse and SKU and sum `Quantity`.
- `MERGE` aggregated quantities into `dbo.StockLevel`, adding to existing `OnHand` values or inserting new rows; update `UpdatedAt` for matches.
- Log merge insert/update counts and commit; on error, roll back, log the error with zero counts, and rethrow.

#### Dependencies
- SQL Server tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- Built-in functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses a transaction with rollback on failure and rethrows errors after logging.
- Captures UTC execution time and uses `MERGE` actions to calculate outcome counts.
