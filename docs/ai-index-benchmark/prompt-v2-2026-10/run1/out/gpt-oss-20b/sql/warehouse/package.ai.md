### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:43:30Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, logging each update.

#### Purpose
- Aggregate supplier feed data and update `dbo.StockLevel`.
- Record sync statistics and errors in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit logging** – insert a sync record into `dbo.SyncLog` with counts of inserts/updates or error details.

#### Key units
- **`dbo.StockLevel`** – warehouse inventory table, PK `(WarehouseId, Sku)`.
- **`dbo.SupplierFeed`** – raw supplier feed rows, PK `FeedRowId`.
- **`dbo.SyncLog`** – audit table for each sync run.
- **`dbo.usp_SyncStock`** – T‑SQL procedure that orchestrates the sync.

#### Data flow
1. `dbo.usp_SyncStock(@Since)` starts a transaction.  
2. Aggregates `dbo.SupplierFeed` rows (`WarehouseId, Sku, SUM(Quantity)`) where `ReceivedAt >= @Since`.  
3. `MERGE` into `dbo.StockLevel`:  
   - `MATCHED`: `OnHand += Quantity`, `UpdatedAt = SYSUTCDATETIME()`.  
   - `NOT MATCHED`: insert new row with `OnHand = Quantity`.  
4. Counts `INSERT` and `UPDATE` actions.  
5. Inserts a row into `dbo.SyncLog` with `StartedAt`, `RowsInserted`, `RowsUpdated`.  
6. Commit; on error rollback and log error message.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Transactional integrity: all changes are wrapped in a single transaction.  
- Error handling: rollback on exception, log error in `SyncLog`.  
- Default constraints on `StockLevel` (`OnHand`, `UpdatedAt`) ensure safe inserts.
