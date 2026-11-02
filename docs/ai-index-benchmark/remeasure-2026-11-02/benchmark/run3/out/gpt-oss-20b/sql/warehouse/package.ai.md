### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:04:07Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, logging each update.

#### Purpose
- Aggregate supplier quantities into the warehouse stock table.
- Record each sync run and any errors.

#### Responsibilities
- **Data ingestion** – read new rows from `dbo.SupplierFeed` since a given timestamp.
- **Stock reconciliation** – merge aggregated quantities into `dbo.StockLevel`, inserting or updating rows.
- **Audit trail** – insert a summary row into `dbo.SyncLog` with counts of inserts, updates, and any error message.

#### Key units
- **`dbo.StockLevel`** – table holding current on‑hand quantities per `(WarehouseId, Sku)`.
- **`dbo.SupplierFeed`** – staging table for supplier‑provided stock rows.
- **`dbo.SyncLog`** – table logging each sync run.
- **`dbo.usp_SyncStock(@Since DATETIME2)`** – stored procedure performing the aggregation, merge, and logging.

#### Data flow
1. `usp_SyncStock` receives a `@Since` timestamp.
2. Reads `SupplierFeed` rows with `ReceivedAt >= @Since`, grouping by `WarehouseId, Sku` to sum `Quantity`.
3. Executes a `MERGE` into `StockLevel`:
   - **Matched** rows: increment `OnHand` and update `UpdatedAt`.
   - **Unmatched** rows: insert new `StockLevel` record.
4. Captures the number of rows inserted and updated.
5. Inserts a record into `SyncLog` with `StartedAt`, `RowsInserted`, `RowsUpdated`, and any `ErrorMessage`.
6. Commits the transaction; on error, rolls back and logs the error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of the merge and log insert.
- Error handling captures and records the message in `SyncLog` before rethrowing.
- Default constraints provide safe defaults for `OnHand` and `UpdatedAt`.
