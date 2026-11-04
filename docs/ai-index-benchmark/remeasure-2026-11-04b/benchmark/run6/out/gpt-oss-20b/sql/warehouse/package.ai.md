### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:27Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps warehouse inventory in sync with nightly supplier feeds and records each sync run.

#### Purpose
- Aggregate supplier feed quantities into the warehouse inventory.
- Persist a log of each sync operation.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit trail** – record run metadata and errors in `dbo.SyncLog`.

#### Key units
- **`dbo.StockLevel`** – inventory table keyed by `WarehouseId, Sku`; holds `OnHand` and `UpdatedAt`.
- **`dbo.SupplierFeed`** – raw feed rows with `WarehouseId, Sku, Quantity, ReceivedAt`.
- **`dbo.SyncLog`** – log table capturing `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.
- **`dbo.usp_SyncStock`** – T‑SQL stored procedure that performs the nightly sync.

#### Data flow
1. Caller supplies `@Since` (last sync timestamp).
2. Procedure aggregates `SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`) received after `@Since`.
3. `MERGE` into `StockLevel`:
   - **Matched**: increment `OnHand`, update `UpdatedAt`.
   - **Unmatched**: insert new row.
4. Capture counts of inserts/updates.
5. Insert a single row into `SyncLog` with the counts and any error message.
6. Commit or rollback transaction.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses a single transaction per sync run.
- Error handling: rollback on failure, log error message, rethrow exception.
- No foreign keys; logical joins on `WarehouseId, Sku`.
