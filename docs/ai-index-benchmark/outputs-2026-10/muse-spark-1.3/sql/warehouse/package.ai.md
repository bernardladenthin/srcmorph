### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:19:02Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronization from supplier feeds into on-hand inventory with audit logging.
#### Purpose
- Synchronize `StockLevel` on-hand inventory from `SupplierFeed` quantities since cutoff
- Audit each sync run outcome in `SyncLog`
#### Responsibilities
- Inventory merge: aggregate `SupplierFeed.Quantity` by `WarehouseId, Sku` and `MERGE` into `StockLevel`
- Run auditing: record `RowsInserted`, `RowsUpdated` or error per `usp_SyncStock` execution
#### Key units
- `dbo.StockLevel`: on-hand inventory by `WarehouseId, Sku` with `OnHand`, `UpdatedAt`
- `dbo.SupplierFeed`: inbound quantities by `WarehouseId, Sku` with `Quantity`, `ReceivedAt`
- `dbo.SyncLog`: sync audit with `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`
- `dbo.usp_SyncStock @Since`: transactional `MERGE` + `SyncLog` insert with `TRY/CATCH`, `THROW`
#### Data flow
- `SupplierFeed` filtered by `ReceivedAt >= @Since` -> `SUM(Quantity)` grouped by `WarehouseId, Sku` -> `MERGE` matched `StockLevel.OnHand + Quantity` / not-matched `INSERT` -> `$action` into `@Changes` -> counted `INSERT SyncLog`
#### Dependencies
- Internal: `StockLevel` as `MERGE` target, `SupplierFeed` as source, `SyncLog` as audit sink
- External: T-SQL `MERGE`, `$action`, `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`
#### Cross-cutting
- Transactional pattern: `COMMIT` on success, `ROLLBACK` if `@@TRANCOUNT > 0` on catch
- Error handling: catch inserts `SyncLog` with `0,0` counts and `ERROR_MESSAGE()`, then `THROW`
- Time handling: `SYSUTCDATETIME()` for `StartedAt`, `UpdatedAt`; `DATETIME2(0)` cutoffs
