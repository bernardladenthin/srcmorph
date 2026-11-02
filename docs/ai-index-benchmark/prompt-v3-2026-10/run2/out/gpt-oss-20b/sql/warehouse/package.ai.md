### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:23:03Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into the warehouse inventory and logs the results.

#### Purpose
- Aggregate supplier feed data into the warehouse stock level table.
- Persist sync statistics and error information.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Stock reconciliation** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit logging** – record each sync run in `dbo.SyncLog` with row counts and errors.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` – T‑SQL stored procedure that performs the merge and logging.
- `dbo.StockLevel` – inventory table keyed by `(WarehouseId, Sku)`.
- `dbo.SupplierFeed` – raw feed table with `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`.
- `dbo.SyncLog` – audit table capturing `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.

#### Data flow
1. Caller supplies `@Since` timestamp to `usp_SyncStock`.
2. Procedure aggregates `SupplierFeed` rows where `ReceivedAt >= @Since` by `WarehouseId` and `Sku`.
3. `MERGE` updates or inserts into `StockLevel`, adjusting `OnHand` and `UpdatedAt`.
4. Counts of `INSERT` and `UPDATE` actions are captured.
5. A row is inserted into `SyncLog` with the counts and any error message.
6. Transaction is committed; on failure, rollback and log error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- All writes occur within a single transaction to ensure atomicity.
- Error handling logs to `SyncLog` before rethrowing.
- Default constraints provide safe defaults for `OnHand` and `UpdatedAt`.
