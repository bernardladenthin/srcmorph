### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:00:26Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier-feed quantities to warehouse inventory by SKU and records each synchronization outcome.

#### Purpose
- Maintain `dbo.StockLevel` from supplier quantities received since a specified timestamp.
- Record inserted/updated row counts or synchronization errors in `dbo.SyncLog`.

#### Responsibilities
- Aggregate `dbo.SupplierFeed` quantities by warehouse and SKU, filtered by `@Since`.
- Insert new stock rows or add aggregated quantities to existing rows, refreshing `UpdatedAt`.
- Log successful synchronization counts and capture failures.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — transactional stock synchronization and outcome logging.
- `dbo.StockLevel` — inventory keyed by `(WarehouseId, Sku)`, with `OnHand` and `UpdatedAt`.
- `dbo.SupplierFeed` — timestamped supplier quantities by warehouse and SKU.
- `dbo.SyncLog` — synchronization start time, insert/update counts, and optional error message.

#### Data flow
- Read recent feed rows → group and sum `Quantity` by `(WarehouseId, Sku)` → `MERGE` into `dbo.StockLevel` → count insert/update actions → write success record to `dbo.SyncLog`.
- On failure, roll back any active transaction, log `ERROR_MESSAGE()` with zero counts, and rethrow.

#### Dependencies
- SQL Server tables `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- `@Changes` table variable captures `MERGE` actions for insert/update counts.

#### Cross-cutting
- Uses a transaction for the merge and success logging; rolls back on error.
- Captures synchronization start time in UTC and refreshes `UpdatedAt` for matched stock rows.
