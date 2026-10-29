### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:13:14Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises supplier inventory feeds into warehouse stock-level records with change logging.

#### Purpose
- Synchronises supplier feed data into `dbo.StockLevel` for warehouse inventory tracking.
- Logs sync outcomes (inserts, updates, errors) into `dbo.SyncLog`.

#### Responsibilities
- MERGE aggregated supplier feed data into warehouse stock-level table with change tracking.
- Manages transaction boundaries, error rollback, and sync outcome logging.
- Groups inbound feed rows by warehouse and SKU for batch processing.

#### Key units
- `dbo.StockLevel`: Warehouse stock table with PK `(WarehouseId, Sku)`.
- `dbo.SupplierFeed`: Inbound feed source filtered by `ReceivedAt >= @Since`.
- `dbo.SyncLog`: Sync outcome log recording rows inserted, updated, and errors.
- MERGE procedure: Nightly sync routine using `@Since` parameter for incremental processing.
- `@Since DATETIME2(0)`: Incremental sync cutoff parameter.

#### Data flow
- Read `dbo.SupplierFeed` rows where `ReceivedAt >= @Since`, grouped by `WarehouseId`, `Sku`.
- Aggregate quantities into `dbo.StockLevel` via MERGE: UPDATE OnHand when matched, INSERT when not matched.
- Capture merge actions into table variable `@Changes`.
- INSERT aggregated merge results into `dbo.SyncLog` with row counts.
- Commit on success; ROLLBACK and error log on failure.

#### Dependencies
- `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Parameter `@Since DATETIME2(0)`.
- Function `SYSUTCDATETIME()` for timestamp defaults.
- Table variable `@Changes` for merge action capture.

#### Cross-cutting
- Transactional integrity: BEGIN TRANSACTION, COMMIT on success, ROLLBACK on failure.
- Error handling: CATCH block inserts error details into `dbo.SyncLog` then THROWS.
- Incremental processing pattern via `@Since` parameter for batch-aware syncs.
