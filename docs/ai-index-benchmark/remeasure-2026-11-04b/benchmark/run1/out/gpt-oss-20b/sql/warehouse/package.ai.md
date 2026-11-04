### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:55:40Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, logging each update.

#### Purpose
- Aggregate supplier stock data and update the warehouse `StockLevel` table.
- Record sync metrics and errors in `SyncLog`.

#### Responsibilities
- **Data ingestion** – read `SupplierFeed` rows since a given timestamp.
- **Data transformation** – aggregate quantities by `WarehouseId` and `Sku`.
- **Data persistence** – merge aggregates into `StockLevel` (update or insert).
- **Audit & error handling** – log start time, row counts, and any error messages.

#### Key units
- **dbo.StockLevel** – warehouse inventory table (`WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`).
- **dbo.SupplierFeed** – raw supplier feed rows (`WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`).
- **dbo.SyncLog** – audit table (`SyncLogId`, `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`).
- **dbo.usp_SyncStock** – T‑SQL procedure that orchestrates the sync.

#### Data flow
1. `usp_SyncStock(@Since)` starts a transaction and records `@Started`.
2. Supplier rows with `ReceivedAt >= @Since` are aggregated into a source table.
3. `MERGE` the source into `StockLevel`:
   - `WHEN MATCHED` → `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
   - `WHEN NOT MATCHED BY TARGET` → insert new row.
4. Capture `$action` counts into `@Changes`.
5. Insert a row into `SyncLog` with `StartedAt`, `RowsInserted`, `RowsUpdated`, and any `ErrorMessage`.
6. Commit or rollback on error.

#### Dependencies
- Internal tables: `StockLevel`, `SupplierFeed`, `SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control (`BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`).

#### Cross-cutting
- **Merge logic** – single `MERGE` statement handles both updates and inserts.
- **Audit pattern** – consistent logging of start time and row counts.
- **Error handling** – transaction rollback and error message capture in `SyncLog`.
