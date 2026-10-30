### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:36Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly T-SQL sync that merges aggregated supplier feed quantities into warehouse on-hand stock levels and logs each run.

#### Purpose
- Adds `dbo.SupplierFeed` quantities received since `@Since` to `dbo.StockLevel.OnHand` per `WarehouseId` and `Sku`.
- Logs per-run insert/update counts and errors to `dbo.SyncLog`.

#### Responsibilities
- Stock reconciliation: `dbo.usp_SyncStock` runs a MERGE from aggregated feed rows into stock levels inside a transaction.
- Run auditing: `dbo.SyncLog` receives one row per run, with counts on success and `ERROR_MESSAGE()` on failure.
- Schema: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog` with composite PK and defaults.

#### Key units
- `dbo.usp_SyncStock`: stored procedure taking `@Since DATETIME2(0)`; performs the merge, logging, and error handling.
- `dbo.StockLevel`: on-hand inventory keyed by `WarehouseId` + `Sku` (`PK_StockLevel`).
- `dbo.SupplierFeed`: received supplier quantities with identity `FeedRowId` (`PK_SupplierFeed`).
- `dbo.SyncLog`: per-run sync audit with `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` (`PK_SyncLog`).
- `@Changes`: table variable capturing `MergeAction` from the MERGE `OUTPUT` clause.

#### Data flow
- `dbo.SupplierFeed` (filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku`, `SUM(Quantity)`) → MERGE → `dbo.StockLevel`.
- MERGE `OUTPUT $action` → `@Changes` → counted `INSERT`/`UPDATE` → `dbo.SyncLog`.
- Failure path: `CATCH` rolls back, writes a zero-count `dbo.SyncLog` row with `ERROR_MESSAGE()`, then re-throws.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Constraints: `PK_StockLevel`, `DF_StockLevel_OnHand`, `DF_StockLevel_UpdatedAt`, `PK_SupplierFeed`, `PK_SyncLog`.
- Logical (undeclared) relationship: `SupplierFeed(WarehouseId, Sku)` → `StockLevel(WarehouseId, Sku)`; no foreign key.

#### Cross-cutting
- Error handling: `TRY/CATCH` with `@@TRANCOUNT` check, rollback, audit row, then `THROW`.
- Transactions: single explicit transaction wraps the MERGE and the `SyncLog` insert.
- Time: all timestamps use UTC via `SYSUTCDATETIME()`; `DATETIME2(0)` precision.
- No secondary indexes declared on any table.
