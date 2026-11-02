### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:09:05Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.

#### Purpose
- Update `dbo.StockLevel` with aggregated supplier quantities.
- Log sync statistics and any errors in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated feed data into `dbo.StockLevel`.
- **Audit logging** – record rows inserted, updated, and error messages in `dbo.SyncLog`.

#### Key units
- **dbo.StockLevel** – warehouse SKU inventory table (PK: WarehouseId, Sku).
- **dbo.SupplierFeed** – raw supplier feed rows (PK: FeedRowId).
- **dbo.SyncLog** – log of each sync run (PK: SyncLogId).
- **dbo.usp_SyncStock** – stored procedure performing the merge and logging.

#### Data flow
1. Caller supplies `@Since` timestamp to `dbo.usp_SyncStock`.
2. Procedure aggregates `dbo.SupplierFeed` rows received after `@Since` by `WarehouseId`/`Sku`.
3. `MERGE` updates existing `dbo.StockLevel` rows or inserts new ones.
4. Counts of inserted and updated rows are captured.
5. A single row is inserted into `dbo.SyncLog` with the counts and any error message.
6. Transaction is committed; on error, rollback and log the error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control statements: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.

#### Cross-cutting
- All writes occur within a single transaction to ensure atomicity.
- Error handling logs the message in `dbo.SyncLog` before rethrowing.
- Default constraints provide `OnHand` = 0 and `UpdatedAt` = current UTC time.
