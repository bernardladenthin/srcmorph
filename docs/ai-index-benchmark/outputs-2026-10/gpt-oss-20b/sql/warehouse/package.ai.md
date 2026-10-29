### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:54:43Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds and records each sync run.

#### Purpose
- Aggregate supplier feed quantities and update the warehouse stock levels.
- Persist a log of each synchronization run.

#### Responsibilities
- **Data ingestion** – read new rows from `dbo.SupplierFeed` since a supplied timestamp.
- **Stock reconciliation** – merge aggregated quantities into `dbo.StockLevel`, inserting new SKUs or updating existing ones.
- **Audit trail** – record the outcome of each sync in `dbo.SyncLog`, including counts of inserts/updates and any error message.

#### Key units
- **`dbo.StockLevel`** – table holding current on‑hand quantities per warehouse and SKU.
- **`dbo.SupplierFeed`** – staging table for incoming supplier quantities.
- **`dbo.SyncLog`** – table logging each sync run.
- **`dbo.usp_SyncStock(@Since DATETIME2)`** – stored procedure that performs the aggregation, merge, and logging.

#### Data flow
1. `dbo.usp_SyncStock` receives a `@Since` timestamp.  
2. It aggregates `dbo.SupplierFeed` rows with `ReceivedAt >= @Since` by `WarehouseId, Sku`.  
3. A `MERGE` updates `dbo.StockLevel`: matched rows have `OnHand` incremented; unmatched rows are inserted.  
4. The `$action` of each merge row is captured to count inserts vs. updates.  
5. A record is inserted into `dbo.SyncLog` with the start time, counts, and any error message.  
6. Transaction is committed or rolled back on error.

#### Dependencies
- **Internal tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
- **SQL Server system functions**: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.  
- **Transaction control**: `@@TRANCOUNT`.

#### Cross-cutting
- All updates to `StockLevel` use a single `MERGE` statement for atomicity.  
- Logging is centralized in `SyncLog` to provide a single audit trail.  
- Error handling captures the message, rolls back the transaction, and rethrows for external handling.
