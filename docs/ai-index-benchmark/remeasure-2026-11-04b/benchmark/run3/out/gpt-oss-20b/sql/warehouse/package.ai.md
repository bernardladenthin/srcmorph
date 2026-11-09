### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:12:12Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, logging each run and handling inserts, updates, and errors.

#### Purpose
- Aggregate supplier stock data into the warehouse inventory.
- Persist a log of each synchronization run.

#### Responsibilities
- **Data ingestion**: read `SupplierFeed` rows since a given timestamp.
- **Inventory update**: merge aggregated quantities into `StockLevel`.
- **Audit trail**: record run metadata and error messages in `SyncLog`.

#### Key units
- **`dbo.StockLevel`** – table holding current on‑hand quantities per warehouse and SKU.
- **`dbo.SupplierFeed`** – staging table for nightly supplier feed rows.
- **`dbo.SyncLog`** – audit table logging start time, row counts, and errors.
- **`dbo.usp_SyncStock`** – stored procedure orchestrating the sync: aggregates, merges, logs, and error handling.

#### Data flow
1. Caller supplies `@Since` timestamp to `usp_SyncStock`.
2. Procedure aggregates `SupplierFeed` rows (`WarehouseId`, `Sku`, `Quantity`) received after `@Since`.
3. Aggregated rows are merged into `StockLevel`: existing rows are updated (`OnHand += Quantity`), new rows are inserted.
4. Counts of inserted and updated rows are captured.
5. A single row is inserted into `SyncLog` with start time, counts, and any error message.
6. Transaction commits; on failure, rollback and log error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of merge and logging.
- Error handling captures `ERROR_MESSAGE()` and records it in `SyncLog`.
- T‑SQL `MERGE` pattern for upsert logic.
