### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:50Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronization from supplier deliveries into on-hand inventory with audit logging.
#### Purpose
- Synchronize on-hand inventory levels from supplier feed deliveries received since cutoff
- Audit each sync run outcome with inserted/updated counts or error message
#### Responsibilities
- Inventory merge: aggregate `SupplierFeed` quantities by `WarehouseId, Sku` and `MERGE` into `StockLevel`
- Sync auditing: record `StartedAt, RowsInserted, RowsUpdated, ErrorMessage` per run in `SyncLog`
#### Key units
- `dbo.usp_SyncStock`: stored procedure with `@Since DATETIME2(0)` cutoff, `MERGE` + `OUTPUT $action` + `TRY/CATCH` transaction
- `dbo.StockLevel`: on-hand inventory table keyed by `WarehouseId, Sku` with `OnHand, UpdatedAt`
- `dbo.SupplierFeed`: delivery feed table with `WarehouseId, Sku, Quantity, ReceivedAt`
- `dbo.SyncLog`: sync audit table with `SyncLogId, StartedAt, RowsInserted, RowsUpdated, ErrorMessage`
#### Data flow
- `SupplierFeed` filtered by `ReceivedAt >= @Since` -> grouped `SUM(Quantity)` by `WarehouseId, Sku` -> `MERGE` into `StockLevel` on `WarehouseId, Sku` match
- `MERGE OUTPUT` into `@Changes.MergeAction` -> `SUM CASE INSERT/UPDATE` counts -> `INSERT` into `SyncLog`
#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- T-SQL functions: `SYSUTCDATETIME`, `SUM`, `ERROR_MESSAGE`, `@@TRANCOUNT`
#### Cross-cutting
- `SET NOCOUNT ON`, `SYSUTCDATETIME()` for `StartedAt/UpdatedAt` timestamps
- Explicit transaction with `COMMIT` on success, `ROLLBACK + THROW` on `CATCH` with error logged to `SyncLog.ErrorMessage`
