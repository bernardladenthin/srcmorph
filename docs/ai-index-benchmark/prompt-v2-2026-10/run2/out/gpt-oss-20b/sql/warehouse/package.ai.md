### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:56:50Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds and records the results.

#### Purpose
- Periodically updates `dbo.StockLevel` from `dbo.SupplierFeed`.
- Persists operation metrics and errors in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – aggregates new supplier feed rows since a given timestamp.
- **Inventory maintenance** – merges aggregated quantities into `dbo.StockLevel`, inserting new SKUs or updating existing ones.
- **Audit logging** – records counts of inserted/updated rows and any error messages in `dbo.SyncLog`.

#### Key units
- **dbo.usp_SyncStock** – T‑SQL stored procedure that performs the nightly sync.
- **dbo.StockLevel** – inventory table keyed by `WarehouseId`/`Sku`.
- **dbo.SupplierFeed** – staging table holding raw supplier rows.
- **dbo.SyncLog** – audit table logging each sync run.

#### Data flow
1. Caller supplies `@Since` timestamp to `usp_SyncStock`.
2. Procedure aggregates `SupplierFeed` rows received after `@Since` by `WarehouseId` and `Sku`.
3. `MERGE` updates `StockLevel`: increments `OnHand` for matches, inserts new rows otherwise.
4. Counts of INSERT and UPDATE actions are captured.
5. A log row is inserted into `SyncLog` with start time, row counts, and any error message.
6. Transaction is committed; on failure, rollback and log the error.

#### Dependencies
- **Tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **SQL Server functions**: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- **Transaction control**: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of the sync.
- Error handling captures and logs exceptions via `ERROR_MESSAGE()` before rethrowing.
- Default constraints on `StockLevel` (`OnHand`, `UpdatedAt`) provide safe initial values.
