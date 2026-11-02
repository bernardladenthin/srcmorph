### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:47:20Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into a warehouse inventory, logging changes and errors.

#### Purpose
- Aggregate supplier feed quantities into `dbo.StockLevel`.
- Record each sync run in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit logging** – insert a sync record with insert/update counts and any error message.

#### Key units
- **`dbo.StockLevel`** – warehouse inventory table, primary key `(WarehouseId, Sku)`.
- **`dbo.SupplierFeed`** – raw supplier feed rows, identity primary key `FeedRowId`.
- **`dbo.SyncLog`** – audit table for each sync run.
- **`dbo.usp_SyncStock`** – T‑SQL procedure that performs the merge, counts, and logging.

#### Data flow
1. Caller supplies `@Since` timestamp to `dbo.usp_SyncStock`.
2. Procedure aggregates `dbo.SupplierFeed` rows where `ReceivedAt >= @Since`.
3. `MERGE` updates or inserts into `dbo.StockLevel`, capturing actions in a temp table.
4. Counts of `INSERT` and `UPDATE` actions are inserted into `dbo.SyncLog` with the run timestamp.
5. On error, transaction rolls back, error message is logged in `SyncLog`, and the error is rethrown.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of inventory updates and logging.
- Error handling via `TRY…CATCH`, rollback, and audit logging.
- Timestamps are stored in UTC (`DATETIME2(0)` with `SYSUTCDATETIME()` defaults).
