### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:50Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.

#### Purpose
- Aggregate supplier feed quantities into the warehouse stock table.
- Persist a log of each synchronization run.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Stock reconciliation** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit trail** – insert a record into `dbo.SyncLog` summarizing the run.

#### Key units
- **dbo.StockLevel** – warehouse SKU inventory table (PK: `WarehouseId, Sku`).
- **dbo.SupplierFeed** – raw supplier feed rows (identity PK `FeedRowId`).
- **dbo.SyncLog** – log of each sync run (identity PK `SyncLogId`).
- **dbo.usp_SyncStock(@Since DATETIME2(0))** – T‑SQL procedure that performs the merge and logging.

#### Data flow
1. Caller supplies `@Since` timestamp to `dbo.usp_SyncStock`.
2. Procedure aggregates `SupplierFeed` rows received after `@Since` by `WarehouseId, Sku`.
3. `MERGE` updates existing `StockLevel` rows or inserts new ones, incrementing `OnHand` and setting `UpdatedAt`.
4. Counts of `INSERT` and `UPDATE` actions are captured.
5. A new `SyncLog` row is inserted with `StartedAt`, `RowsInserted`, `RowsUpdated`, and any error message.
6. Transaction is committed; on error, rollback and log the error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- All writes occur within a single transaction for atomicity.
- Error handling logs the message in `SyncLog` and rethrows for external handling.
- Default constraints provide `OnHand` zero and `UpdatedAt` UTC timestamp.
