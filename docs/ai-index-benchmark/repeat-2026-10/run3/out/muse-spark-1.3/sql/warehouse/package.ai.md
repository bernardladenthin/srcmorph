### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:26:05Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronisation applying supplier feed quantities to on-hand levels with audit logging.
#### Purpose
- Synchronise `StockLevel.OnHand` from aggregated `SupplierFeed.Quantity` received since cutoff
- Audit each run in `SyncLog` with inserted / updated counts and error capture
#### Responsibilities
- Feed aggregation: filter `SupplierFeed` by `ReceivedAt >= @Since`, group by `WarehouseId, Sku` with `SUM(Quantity)`
- Upsert: `MERGE` aggregated feed into `StockLevel` — `INSERT` when not matched, `OnHand + Quantity` + `UpdatedAt` when matched
- Run auditing: record `StartedAt, RowsInserted, RowsUpdated, ErrorMessage` per execution
#### Key units
- `dbo.usp_SyncStock` — transactional sync procedure with `@Since` cutoff, `@Started` capture, `@Changes` output
- `dbo.StockLevel` — inventory target keyed by `PK_StockLevel (WarehouseId, Sku)`, fields `OnHand, UpdatedAt`
- `dbo.SupplierFeed` — inbound feed source keyed by `PK_SupplierFeed (FeedRowId)`, fields `WarehouseId, Sku, Quantity, ReceivedAt`
- `dbo.SyncLog` — audit log keyed by `PK_SyncLog (SyncLogId)`, fields `StartedAt, RowsInserted, RowsUpdated, ErrorMessage`
#### Data flow
- `@Since` + `SYSUTCDATETIME()` → `SupplierFeed` scan → `SUM(Quantity)` per `WarehouseId, Sku` → `MERGE ON (WarehouseId, Sku)` → `StockLevel` insert / update → `OUTPUT $action INTO @Changes` → `SUM(CASE MergeAction)` → `SyncLog` insert → `COMMIT`
- On failure: `ROLLBACK` if `@@TRANCOUNT > 0` → log `0,0,ERROR_MESSAGE()` to `SyncLog` → `THROW`
#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`
- System functions / vars: `SYSUTCDATETIME()`, `$action`, `ERROR_MESSAGE()`, `@@TRANCOUNT`
- Dialect: T-SQL SQL Server — `GO`, `IDENTITY`, `MERGE`, `DATETIME2(0)`, `NVARCHAR`
#### Cross-cutting
- Transactional pattern: `BEGIN TRANSACTION` + `COMMIT`, guarded `ROLLBACK` in `CATCH`
- Error handling: `TRY/CATCH` logging to `SyncLog` then re-`THROW`
- Timestamping: `SYSUTCDATETIME()` defaults `DF_StockLevel_OnHand, DF_StockLevel_UpdatedAt` and `UpdatedAt` on update
- Join-equivalent without FKs: `StockLevel.WarehouseId -> SupplierFeed.WarehouseId`, `StockLevel.Sku -> SupplierFeed.Sku` on `(WarehouseId, Sku)`
