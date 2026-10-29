### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:18:20Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into a warehouse inventory, logging each run and handling errors.

#### Purpose
- Update `StockLevel` with aggregated supplier feed quantities.
- Record each sync run in `SyncLog`.

#### Responsibilities
- **Data ingestion**: read `SupplierFeed` rows since a given timestamp.
- **Inventory update**: merge aggregated quantities into `StockLevel`.
- **Audit & error handling**: log successes and failures in `SyncLog`.

#### Key units
- **dbo.StockLevel** – warehouse‑sku inventory table, primary key `(WarehouseId, Sku)`.
- **dbo.SupplierFeed** – raw supplier feed rows, identity `FeedRowId`.
- **dbo.SyncLog** – audit table for sync runs, primary key `SyncLogId`.
- **dbo.usp_SyncStock** – stored procedure that aggregates, merges, and logs.

#### Data flow
1. `usp_SyncStock(@Since)` starts a transaction and records `@Started`.
2. Aggregates `SupplierFeed` rows where `ReceivedAt >= @Since` by `(WarehouseId, Sku)` → `Quantity`.
3. MERGE into `StockLevel`:
   - Update `OnHand = OnHand + Quantity`, set `UpdatedAt`.
   - Insert new rows with `OnHand = Quantity`.
4. Capture counts of inserted and updated rows.
5. Insert a row into `SyncLog` with `StartedAt`, `RowsInserted`, `RowsUpdated`, and any `ErrorMessage`.
6. Commit; on error rollback, log error, rethrow.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control (`BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`).

#### Cross-cutting
- **Transactional integrity**: all updates and logging occur within a single transaction.
- **Error handling**: `TRY…CATCH` captures errors, logs them, and rolls back.
- **Audit trail**: every run is recorded in `SyncLog` for traceability.
