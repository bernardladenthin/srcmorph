### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:04:15Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.

#### Purpose
- Aggregate supplier feed quantities into the warehouse stock table.
- Persist a log of each sync operation.

#### Responsibilities
- **Data ingestion**: read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update**: merge aggregated quantities into `dbo.StockLevel`.
- **Audit trail**: insert a record into `dbo.SyncLog` with counts and any error message.

#### Key units
- **`dbo.StockLevel`** – stores current on‑hand quantity per `(WarehouseId, Sku)`.
- **`dbo.SupplierFeed`** – raw nightly feed rows with `Quantity` and `ReceivedAt`.
- **`dbo.SyncLog`** – audit table for each sync run (`StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`).
- **`dbo.usp_SyncStock(@Since DATETIME2)`** – T‑SQL procedure that performs the merge and logging.

#### Data flow
1. Caller supplies `@Since` timestamp.
2. Procedure records `@Started` and begins a transaction.
3. Aggregates `SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`) filtered by `@Since`.
4. `MERGE` into `StockLevel`:
   - `WHEN MATCHED`: increment `OnHand`, update `UpdatedAt`.
   - `WHEN NOT MATCHED BY TARGET`: insert new row.
5. Capture counts of inserted/updated rows.
6. Insert a `SyncLog` row with those counts and `@Started`.
7. Commit transaction; on error, rollback, log error, rethrow.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Transactional integrity: all changes to `StockLevel` and `SyncLog` occur within a single transaction.
- Error handling: rollback on exception, log error message in `SyncLog`.
- Timestamp handling: uses UTC (`SYSUTCDATETIME()`) for consistency across feeds.
