### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:53:07Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into a warehouse inventory, logging changes and errors.

#### Purpose
- Aggregate supplier quantities into `dbo.StockLevel`.
- Record each sync run in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit logging** – insert a sync record with counts of inserted/updated rows and any error message.

#### Key units
- **dbo.StockLevel** – warehouse inventory table (`WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`).
- **dbo.SupplierFeed** – raw supplier feed table (`WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`).
- **dbo.SyncLog** – audit table for sync runs (`SyncLogId`, `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`).
- **dbo.usp_SyncStock** – T‑SQL procedure that aggregates, merges, logs, and handles errors.

#### Data flow
1. `dbo.usp_SyncStock(@Since)` starts a transaction and records `StartedAt`.
2. Aggregates `SupplierFeed` rows received after `@Since` by `(WarehouseId, Sku)` → `Quantity`.
3. `MERGE` into `StockLevel`:
   - `WHEN MATCHED`: `OnHand += Quantity`, update `UpdatedAt`.
   - `WHEN NOT MATCHED BY TARGET`: `INSERT` new row.
4. Capture `$action` counts into `@Changes`.
5. Insert a row into `SyncLog` with `RowsInserted`, `RowsUpdated`, and any `ErrorMessage`.
6. Commit or rollback on error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- Uses `MERGE` for upsert logic.
- Error handling: rollback, log error message, rethrow.
- Default constraints on `OnHand` and `UpdatedAt` ensure non‑null values.
- No foreign key relationships; relies on composite primary keys for uniqueness.
