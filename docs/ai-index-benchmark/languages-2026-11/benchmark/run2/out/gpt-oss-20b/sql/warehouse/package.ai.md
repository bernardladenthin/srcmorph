### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:44:44Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.

#### Purpose
- Update `dbo.StockLevel` with aggregated supplier data.
- Log sync statistics and errors in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – read new rows from `dbo.SupplierFeed` since a given timestamp.
- **Inventory update** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit trail** – record rows inserted, updated, and any errors in `dbo.SyncLog`.

#### Key units
- **dbo.StockLevel** – warehouse SKU inventory table (PK: `WarehouseId, Sku`).
- **dbo.SupplierFeed** – staging table for nightly supplier rows.
- **dbo.SyncLog** – log of each sync run (counts, timestamps, error message).
- **dbo.usp_SyncStock** – stored procedure that performs the merge, counts, and logging.

#### Data flow
1. Caller supplies `@Since` timestamp to `dbo.usp_SyncStock`.
2. Procedure aggregates `dbo.SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`) where `ReceivedAt >= @Since`.
3. MERGE into `dbo.StockLevel`: update `OnHand` and `UpdatedAt` for matches; insert new rows for non‑matches.
4. Capture `$action` to count `INSERT` and `UPDATE` rows.
5. Insert a single row into `dbo.SyncLog` with counts and `StartedAt`.
6. Commit transaction; on error, rollback and log error message.

#### Dependencies
- Internal tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Transactional integrity: all merge and log writes occur within a single transaction.
- Error handling: rollback on failure, error message stored in `SyncLog`.
- T‑SQL MERGE pattern for upsert logic.
