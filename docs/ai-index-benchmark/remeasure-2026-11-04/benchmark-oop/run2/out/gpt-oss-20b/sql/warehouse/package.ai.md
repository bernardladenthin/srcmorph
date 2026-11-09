### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:42:30Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps a warehouse inventory in sync with incoming supplier stock feeds, logging each update run.

#### Purpose
- Aggregate supplier feed quantities into the warehouse stock table.
- Record each sync run’s statistics and errors.

#### Responsibilities
- **Data ingestion**: read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update**: merge aggregated quantities into `dbo.StockLevel`.
- **Audit logging**: insert a record into `dbo.SyncLog` with counts of inserted/updated rows or error details.

#### Key units
- **`dbo.StockLevel`** – table storing current on‑hand quantities per `(WarehouseId, Sku)`.
- **`dbo.SupplierFeed`** – table holding raw supplier feed rows.
- **`dbo.SyncLog`** – table logging each sync run.
- **`dbo.usp_SyncStock`** – stored procedure orchestrating the sync: aggregates, merges, logs, and handles errors.

#### Data flow
1. `dbo.usp_SyncStock(@Since)` starts a transaction.  
2. Aggregates `SupplierFeed` rows received after `@Since` by `(WarehouseId, Sku)` → `Quantity`.  
3. `MERGE` into `StockLevel`:  
   - `WHEN MATCHED`: increment `OnHand` and update `UpdatedAt`.  
   - `WHEN NOT MATCHED`: insert new row.  
4. Capture `$action` counts for inserts/updates.  
5. Insert a row into `SyncLog` with `RowsInserted`, `RowsUpdated`, and any `ErrorMessage`.  
6. Commit or rollback on error.

#### Dependencies
- Internal tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.  
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Transactional integrity: all operations wrapped in a single transaction.  
- Error handling: rollback on failure, log error message in `SyncLog`.  
- Time‑based filtering: uses `@Since` to process only new feed rows.
