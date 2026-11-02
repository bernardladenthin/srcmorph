### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:29:51Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into a warehouse inventory table and records the sync outcome.

#### Purpose
- Merge supplier feed data into `StockLevel`.
- Log sync statistics and errors in `SyncLog`.

#### Responsibilities
- **Data ingestion** – read `SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated feed quantities into `StockLevel`.
- **Audit logging** – record rows inserted/updated and any errors in `SyncLog`.

#### Key units
- **dbo.StockLevel** – warehouse inventory table (`WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`).
- **dbo.SupplierFeed** – raw supplier feed rows (`WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`).
- **dbo.SyncLog** – sync audit table (`SyncLogId`, `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`).
- **dbo.usp_SyncStock** – T‑SQL procedure that performs the nightly merge and logging.

#### Data flow
1. `usp_SyncStock(@Since)` starts, records `@Started`.
2. Begins a transaction.
3. Aggregates `SupplierFeed` rows where `ReceivedAt >= @Since`.
4. `MERGE` into `StockLevel` on `WarehouseId`/`Sku`:
   - `WHEN MATCHED`: increment `OnHand`, update `UpdatedAt`.
   - `WHEN NOT MATCHED BY TARGET`: insert new row with `OnHand = Quantity`.
5. Counts `INSERT` and `UPDATE` actions into `@Changes`.
6. Inserts a row into `SyncLog` with `StartedAt`, `RowsInserted`, `RowsUpdated`, optional `ErrorMessage`.
7. Commits transaction; on error rolls back, logs error, rethrows.

#### Dependencies
- Internal tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of inventory updates and log entry.
- Error handling: rollback on exception, log error message, rethrow for external visibility.
- No foreign key constraints; logical join performed in the `MERGE` statement.
