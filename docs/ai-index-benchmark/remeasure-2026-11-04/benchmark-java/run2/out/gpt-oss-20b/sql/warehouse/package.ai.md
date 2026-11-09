### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:58Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.

#### Purpose
- Aggregate supplier feed data into the warehouse inventory.
- Persist a log of each sync operation.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit logging** – record insert/update counts and any errors in `dbo.SyncLog`.

#### Key units
- **dbo.StockLevel** – inventory table keyed by `WarehouseId`/`Sku`; tracks `OnHand` and `UpdatedAt`.
- **dbo.SupplierFeed** – staging table for raw supplier rows (`WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`).
- **dbo.SyncLog** – audit table for each sync run (`StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`).
- **dbo.usp_SyncStock** – T‑SQL procedure that aggregates, merges, and logs the sync.

#### Data flow
1. Caller supplies `@Since` timestamp to `usp_SyncStock`.
2. Procedure aggregates `SupplierFeed` rows by `WarehouseId`/`Sku` since `@Since`.
3. `MERGE` updates existing `StockLevel` rows or inserts new ones, adjusting `OnHand` and `UpdatedAt`.
4. Counts of inserted and updated rows are captured.
5. A single row is inserted into `SyncLog` with the counts and any error message.
6. Transaction is committed or rolled back on error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of inventory update and logging.
- Error handling captures `ERROR_MESSAGE()` and logs it in `SyncLog`.
- Default constraints provide safe defaults for `OnHand` and `UpdatedAt`.
