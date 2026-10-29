### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:23:26Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse inventory from recent supplier-feed quantities and records the outcome of each synchronization.

#### Purpose
- Updates `dbo.StockLevel` by warehouse and SKU using supplier quantities received since a cutoff.
- Records inserted and updated row counts, or an error message, in `dbo.SyncLog`.

#### Responsibilities
- Aggregate `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku` for rows with `ReceivedAt >= @Since`.
- Add aggregated quantities to existing stock levels or insert new warehouse/SKU levels, refreshing `UpdatedAt` for matches.
- Log synchronization counts on success and the error message on failure.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — aggregates recent feed rows, merges quantities into inventory, and logs the result.
- `dbo.StockLevel` — stores on-hand inventory keyed by `WarehouseId` and `Sku`.
- `dbo.SupplierFeed` — supplies timestamped quantities by warehouse and SKU.
- `dbo.SyncLog` — stores synchronization start time, inserted and updated counts, and optional error details.

#### Data flow
- `dbo.SupplierFeed` rows since `@Since` → grouped quantity totals by warehouse and SKU → merge into `dbo.StockLevel` → count merge actions → success entry in `dbo.SyncLog`.
- On failure, roll back the transaction, write `ERROR_MESSAGE()` to `dbo.SyncLog`, and rethrow.

#### Dependencies
- SQL Server tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- Built-ins: `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- Inventory changes and success logging run within a transaction.
- Error handling rolls back, logs the error, and rethrows.
