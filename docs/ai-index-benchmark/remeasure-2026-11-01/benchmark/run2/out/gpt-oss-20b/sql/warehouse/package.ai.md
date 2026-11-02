### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:42Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, logging each run.

#### Purpose
- Aggregate supplier feed quantities into the warehouse stock table.
- Record each sync operation in a log table.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Stock reconciliation** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit trail** – insert a summary record into `dbo.SyncLog` for each run.

#### Key units
- **dbo.StockLevel** – holds current on‑hand quantities per warehouse and SKU.
- **dbo.SupplierFeed** – raw feed rows from the supplier.
- **dbo.SyncLog** – audit table for sync runs.
- **dbo.usp_SyncStock** – stored procedure that performs the aggregation, merge, and logging.

#### Data flow
1. Caller supplies `@Since` timestamp to `usp_SyncStock`.
2. Procedure aggregates `SupplierFeed` rows (`WarehouseId, Sku`) received after `@Since`.
3. `MERGE` updates existing `StockLevel` rows or inserts new ones, adjusting `OnHand` and `UpdatedAt`.
4. Counts of inserted and updated rows are captured.
5. A single row is inserted into `SyncLog` with the counts and any error message.
6. Transaction is committed or rolled back on error.

#### Dependencies
- **Internal** – `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- **System** – `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`.

#### Cross-cutting
- All writes occur within a single transaction for atomicity.
- Error handling logs the message in `SyncLog` and rethrows to propagate failure.
- Uses `MERGE` with a table variable to capture action counts for logging.
