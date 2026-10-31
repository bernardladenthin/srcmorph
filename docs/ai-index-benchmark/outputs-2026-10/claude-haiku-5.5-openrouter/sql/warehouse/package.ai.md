### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:43Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock reconciliation that folds supplier feed receipts into on-hand inventory levels and audits each sync run.

#### Purpose
- Increase `dbo.StockLevel.OnHand` per warehouse and SKU by supplier feed quantities received since a cutoff.
- Log every sync run (counts or error) to `dbo.SyncLog`.

#### Responsibilities
- Inventory update: MERGE of aggregated feed receipts into stock levels, inside `dbo.usp_SyncStock`.
- Run auditing: per-run insert/update counts and failure messages written to `dbo.SyncLog`.
- Error handling: transactional rollback and re-raise on failure, with failure logged.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))`: stored procedure orchestrating the merge, audit logging, and TRY/CATCH recovery.
- `dbo.StockLevel`: on-hand inventory keyed by `(WarehouseId, Sku)`; merge target.
- `dbo.SupplierFeed`: received supplier quantities with `ReceivedAt`; merge source, aggregated per warehouse and SKU.
- `dbo.SyncLog`: per-run audit rows with `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.
- `@Changes` table variable: captures MERGE `$action` values for counting.
- `DF_StockLevel_OnHand`, `DF_StockLevel_UpdatedAt`, `PK_StockLevel`, `PK_SupplierFeed`, `PK_SyncLog`: defaults and keys.

#### Data flow
- `dbo.SupplierFeed` filtered by `ReceivedAt >= @Since`, summed `Quantity` grouped by `WarehouseId, Sku`.
- MERGE into `dbo.StockLevel`: matched rows add quantity to `OnHand` and stamp `UpdatedAt`; unmatched rows insert new levels.
- `OUTPUT $action INTO @Changes`, then counts of `'INSERT'` and `'UPDATE'` written as one `dbo.SyncLog` row.
- On error: rollback, `dbo.SyncLog` row with zero counts and `ERROR_MESSAGE()`, then `THROW`.

#### Dependencies
- Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`, `THROW`, MERGE `$action`.
- No declared foreign key; the `SupplierFeed` to `StockLevel` link is a logical join on `(WarehouseId, Sku)`.
- `dbo.SyncLog` has no declared relationships.

#### Cross-cutting
- Transactions: single explicit transaction around MERGE and audit insert, rolled back in CATCH.
- Error handling: failures recorded in `dbo.SyncLog.ErrorMessage` (NVARCHAR(4000)) then re-thrown.
- Timestamps: all times use UTC via `SYSUTCDATETIME()`, stored as `DATETIME2(0)`.
- Session setting: `SET NOCOUNT ON` inside the procedure.
