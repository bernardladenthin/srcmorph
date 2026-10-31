### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:51:55Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly synchronization pipeline that incrementally merges supplier delivery quantities into per-warehouse on-hand stock levels per SKU while auditing run metrics and errors.

#### Purpose
- Incrementally synchronize warehouse on-hand inventory from supplier feed deltas and persist the results with a full audit trail of each sync run.

#### Responsibilities
- Persist current on-hand stock per warehouse/SKU in `dbo.StockLevel`, raw supplier deliveries in `dbo.SupplierFeed`, and run audit records in `dbo.SyncLog`.
- Execute transactional merge logic via `dbo.usp_SyncStock` to apply aggregated feed quantities since `@Since` into `StockLevel`, logging inserted/updated counts or error details.

#### Key units
- `dbo.StockLevel` — composite-PK table (`WarehouseId`, `Sku`) holding `OnHand` quantity and `UpdatedAt` timestamp.
- `dbo.SupplierFeed` — append-only feed table with `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt` per delivery row.
- `dbo.SyncLog` — audit table recording `StartedAt`, `RowsInserted`, `RowsUpdated`, and optional `ErrorMessage` per execution.
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — stored procedure that aggregates feed deltas, MERGEs them into `StockLevel`, captures actions via `OUTPUT $action`, and writes success/error rows to `SyncLog`.

#### Data flow
- `dbo.SupplierFeed` rows filtered by `ReceivedAt >= @Since` are grouped by `WarehouseId`, `Sku` and aggregated with `SUM(Quantity)`.
- Aggregated deltas are MERGED into `dbo.StockLevel` on `WarehouseId + Sku`: matched rows get `OnHand += Quantity`, unmatched rows are inserted; merge actions are captured into `@Changes` via `OUTPUT $action`.
- Results are written to `dbo.SyncLog`: success row with `RowsInserted`/`RowsUpdated` counts from `@Changes`, or error row with `ERROR_MESSAGE()` on failure after rollback.

#### Dependencies
- Internal: `dbo.SupplierFeed` → `dbo.StockLevel` logical join on `WarehouseId`, `Sku` (no declared FK); `dbo.usp_SyncStock` reads `SupplierFeed` and writes `StockLevel` and `SyncLog`.
- External: SQL Server system functions `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`; T-SQL `MERGE` statement with `$action` output; table variable `@Changes`.

#### Cross-cutting
- Transactional integrity: `BEGIN TRANSACTION`/`COMMIT` with `CATCH` block performing `ROLLBACK` (if `@@TRANCOUNT > 0`) and rethrow via `THROW`.
- Audit pattern: every procedure execution writes exactly one row to `dbo.SyncLog`, success or failure.
- Composite key identity: `PK_StockLevel (WarehouseId, Sku)` defines stock grain; `PK_SupplierFeed` and `PK_SyncLog` are surrogate identity keys.
- UTC timestamping: `SYSUTCDATETIME()` used for defaults (`DF_StockLevel_UpdatedAt`) and update stamps.
