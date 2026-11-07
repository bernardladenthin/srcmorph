### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:50:05Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.

#### Purpose
- Merge supplier feed data into current stock levels.
- Log each sync run with counts of inserted and updated rows or errors.

#### Responsibilities
- **Data ingestion** – read new supplier feed rows since a given timestamp.
- **Inventory update** – merge aggregated feed quantities into the `StockLevel` table.
- **Audit logging** – record the outcome of each sync run in `SyncLog`.

#### Key units
- **`dbo.StockLevel`** – warehouse inventory table (PK: `WarehouseId, Sku`).
- **`dbo.SupplierFeed`** – staging table for nightly supplier rows (PK: `FeedRowId`).
- **`dbo.SyncLog`** – audit table for sync runs (PK: `SyncLogId`).
- **`dbo.usp_SyncStock`** – T‑SQL procedure that orchestrates the merge and logging.

#### Data flow
1. `usp_SyncStock(@Since)` starts a transaction.  
2. Aggregates `SupplierFeed` rows received after `@Since` by `WarehouseId, Sku`.  
3. `MERGE` into `StockLevel`:  
   - `WHEN MATCHED`: add `Quantity` to `OnHand`, update `UpdatedAt`.  
   - `WHEN NOT MATCHED BY TARGET`: insert new record with `OnHand = Quantity`.  
4. Counts of `INSERT` and `UPDATE` actions are captured.  
5. A row is inserted into `SyncLog` with those counts or an error message.  
6. Transaction is committed; on error, rollback and log the error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
- System functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of the merge and log insert.  
- Error handling captures and logs exceptions via `SyncLog`.  
- No foreign key constraints; relationships are implicit through merge logic.
