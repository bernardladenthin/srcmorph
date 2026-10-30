### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:40Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier inventory updates to warehouse stock and records synchronization results or failures.

#### Purpose
- Aggregate supplier feed quantities by warehouse and SKU, then update stock levels.
- Record synchronization counts and errors.

#### Responsibilities
- Read and aggregate `dbo.SupplierFeed` rows received since `@Since`.
- Insert or update `dbo.StockLevel` and log outcomes in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since)`: aggregates feed quantities, merges them into stock, and logs results.
- `dbo.StockLevel`: stores on-hand quantities by warehouse and SKU.
- `dbo.SupplierFeed`: stores supplier inventory updates.
- `dbo.SyncLog`: stores synchronization counts or error messages.

#### Data flow
- `dbo.SupplierFeed` rows filtered by `ReceivedAt >= @Since` → quantities grouped by `WarehouseId` and `Sku` → merge into `dbo.StockLevel` → outcome recorded in `dbo.SyncLog`.

#### Dependencies
- SQL Server functions `SYSUTCDATETIME()` and `ERROR_MESSAGE()`.

#### Cross-cutting
- Merge actions are logged; failures roll back, log the error with zero counts, and rethrow.
