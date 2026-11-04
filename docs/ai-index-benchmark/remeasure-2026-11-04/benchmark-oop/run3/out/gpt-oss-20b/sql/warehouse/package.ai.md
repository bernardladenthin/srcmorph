### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:57:09Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with incoming supplier feeds and records each sync run.

#### Purpose
- Aggregate supplier feed data into the warehouse stock level table.
- Persist a log of each synchronization run.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Stock reconciliation** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit trail** – write a summary of the operation to `dbo.SyncLog`.

#### Key units
- **`dbo.StockLevel`** – table holding current on‑hand quantities per warehouse and SKU.
- **`dbo.SupplierFeed`** – staging table for raw supplier feed rows.
- **`dbo.SyncLog`** – table recording each sync run (start time, rows inserted/updated, error message).
- **`dbo.usp_SyncStock`** – stored procedure that orchestrates the sync: aggregates, merges, logs, and handles errors.

#### Data flow
1. Caller supplies `@Since` timestamp to `usp_SyncStock`.
2. Procedure aggregates `SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`) where `ReceivedAt >= @Since`.
3. `MERGE` into `StockLevel`: update existing rows or insert new ones, adjusting `OnHand` and `UpdatedAt`.
4. Capture counts of inserted and updated rows.
5. Insert a single row into `SyncLog` with the counts and any error message.
6. Commit or rollback the transaction.

#### Dependencies
- **Internal** – `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **System** – `SYSUTCDATETIME()`, `@@TRANCOUNT`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Transactional integrity: all changes are wrapped in a single transaction with rollback on error.
- Error handling: logs error message in `SyncLog` and rethrows for external handling.
- Timestamping: uses UTC (`SYSUTCDATETIME()`) for consistency across warehouses.
