### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:59:04Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds and records the sync outcome.

#### Purpose
- Aggregate supplier feed quantities into the warehouse inventory.
- Persist sync statistics and errors.

#### Responsibilities
- **Data ingestion** – read `SupplierFeed` rows for a given window.
- **Inventory update** – merge aggregated quantities into `StockLevel`.
- **Audit logging** – record rows inserted/updated and any errors in `SyncLog`.

#### Key units
- **`dbo.StockLevel`** – warehouse SKU inventory table (PK: `WarehouseId, Sku`).
- **`dbo.SupplierFeed`** – raw supplier feed rows (PK: `FeedRowId`).
- **`dbo.SyncLog`** – log of each sync run (PK: `SyncLogId`).
- **`dbo.usp_SyncStock`** – T‑SQL procedure that performs the nightly sync.

#### Data flow
1. `usp_SyncStock(@Since)` starts a transaction.  
2. Aggregates `SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`) where `ReceivedAt >= @Since`.  
3. `MERGE` into `StockLevel`:  
   - If a match exists, `OnHand += Quantity` and `UpdatedAt` is refreshed.  
   - If no match, insert a new row with `OnHand = Quantity`.  
4. Counts of inserted and updated rows are captured.  
5. Inserts a record into `SyncLog` with the counts and any error message.  
6. Commits the transaction; on failure rolls back and logs the error.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.  
- Transaction control statements: `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of inventory updates and logging.  
- Error handling via `TRY…CATCH`, rolling back and recording `ERROR_MESSAGE()` in `SyncLog`.  
- Timestamps are stored in UTC (`DATETIME2(0)` with `SYSUTCDATETIME()`).
