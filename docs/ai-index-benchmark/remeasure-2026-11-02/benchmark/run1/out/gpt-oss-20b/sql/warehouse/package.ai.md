### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:22Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.

#### Purpose
- Merge supplier feed rows into current stock levels.
- Log the number of rows inserted/updated and any errors.

#### Responsibilities
- **Data ingestion** – read `SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated feed data into `StockLevel`.
- **Audit logging** – record sync statistics and errors in `SyncLog`.

#### Key units
- **`dbo.StockLevel`** – warehouse inventory table (PK: `WarehouseId, Sku`).
- **`dbo.SupplierFeed`** – raw supplier feed rows (PK: `FeedRowId`).
- **`dbo.SyncLog`** – audit trail of each sync run (PK: `SyncLogId`).
- **`dbo.usp_SyncStock(@Since DATETIME2)`** – T‑SQL procedure that performs the merge and logging.

#### Data flow
1. `usp_SyncStock` receives a `@Since` timestamp.  
2. Aggregates `SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`) where `ReceivedAt >= @Since`.  
3. Executes a `MERGE` into `StockLevel`:  
   - `UPDATE` existing rows (`OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`).  
   - `INSERT` new rows (`OnHand = Quantity`).  
4. Captures merge actions in a table variable `@Changes`.  
5. Inserts a single row into `SyncLog` with counts of inserted/updated rows or an error message.  
6. Commits the transaction; on failure, rolls back, logs the error, and rethrows.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.  
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of the merge and log insert.  
- Error handling captures and records the error message in `SyncLog` before rethrowing.  
- All timestamps are stored in UTC (`DATETIME2(0)` with `SYSUTCDATETIME()` defaults).
