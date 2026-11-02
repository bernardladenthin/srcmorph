### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:22:18Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into the warehouse inventory and records the sync outcome.

#### Purpose
- Update `dbo.StockLevel` with aggregated supplier feed quantities.
- Log each sync run in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion**: read `dbo.SupplierFeed` rows received since a given timestamp.
- **Inventory update**: merge aggregated feed data into `dbo.StockLevel` (insert or update).
- **Audit logging**: record run statistics and errors in `dbo.SyncLog`.

#### Key units
- **Table** `dbo.StockLevel` – warehouse SKU inventory snapshot.
- **Table** `dbo.SupplierFeed` – raw supplier feed rows.
- **Table** `dbo.SyncLog` – audit trail of sync runs.
- **Procedure** `dbo.usp_SyncStock` – orchestrates aggregation, merge, and logging.

#### Data flow
1. Caller supplies `@Since` timestamp to `dbo.usp_SyncStock`.
2. Procedure aggregates `dbo.SupplierFeed` rows received after `@Since` by `WarehouseId, Sku`.
3. Aggregated source is MERGE‑ed into `dbo.StockLevel`:
   - `UPDATE` existing rows (`OnHand += Quantity`).
   - `INSERT` new rows.
4. Counts of `INSERT` and `UPDATE` actions are captured.
5. A single row is inserted into `dbo.SyncLog` with start time, row counts, and any error message.
6. Transaction is committed; on error, rollback and log error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control statements: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.

#### Cross-cutting
- All writes occur within a single transaction to guarantee consistency.
- Error handling captures and logs the exception message before rethrowing.
- Uses `MERGE` with `$action` to differentiate inserts vs. updates for logging.
