### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:24:07Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps warehouse inventory in sync with nightly supplier feeds, recording success and error metrics.

#### Purpose
- Aggregate supplier feed quantities into the warehouse stock ledger.
- Persist sync statistics and error details for auditability.

#### Responsibilities
- **Data ingestion** – read `SupplierFeed` rows since a given timestamp.
- **Stock reconciliation** – merge aggregated quantities into `StockLevel`.
- **Audit logging** – record insert/update counts and any errors in `SyncLog`.

#### Key units
- **`dbo.StockLevel`** – warehouse inventory table keyed by `(WarehouseId, Sku)`.
- **`dbo.SupplierFeed`** – raw supplier quantity feed.
- **`dbo.SyncLog`** – log of each sync run, including timestamps and row counts.
- **`dbo.usp_SyncStock`** – stored procedure orchestrating the nightly sync.

#### Data flow
1. `usp_SyncStock(@Since)` starts a transaction and notes `@Started`.
2. Aggregates `SupplierFeed` rows (`WarehouseId`, `Sku`, `Quantity`) received after `@Since`.
3. `MERGE` into `StockLevel`:
   - Update `OnHand` and `UpdatedAt` for existing keys.
   - Insert new rows for new `(WarehouseId, Sku)` combinations.
4. Capture counts of inserted and updated rows.
5. Insert a `SyncLog` record with `StartedAt`, `RowsInserted`, `RowsUpdated`, and any `ErrorMessage`.
6. Commit or rollback on error, rethrowing the exception.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control primitives: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of stock updates and logging.
- Error handling captures and logs the message before rethrowing, ensuring visibility in `SyncLog`.
- No foreign key constraints; logical joins are performed in the `MERGE` statement.
