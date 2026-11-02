### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:49Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, recording changes and errors.

#### Purpose
- Update `dbo.StockLevel` with aggregated supplier quantities.
- Log each sync run in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Inventory update** – merge aggregated quantities into `dbo.StockLevel`.
- **Audit trail** – insert a log row into `dbo.SyncLog` with counts of inserted/updated rows or error details.

#### Key units
- **dbo.StockLevel** – warehouse SKU inventory table (PK: `WarehouseId, Sku`).
- **dbo.SupplierFeed** – raw supplier feed rows (PK: `FeedRowId`).
- **dbo.SyncLog** – sync run audit table (PK: `SyncLogId`).
- **dbo.usp_SyncStock** – T‑SQL procedure that orchestrates the nightly sync.

#### Data flow
1. `dbo.usp_SyncStock(@Since)` starts a transaction.  
2. Aggregates `SupplierFeed` rows received after `@Since` by `WarehouseId` and `Sku`.  
3. `MERGE` into `StockLevel`:  
   - `WHEN MATCHED`: add quantity to `OnHand`, update `UpdatedAt`.  
   - `WHEN NOT MATCHED BY TARGET`: insert new row with `OnHand = Quantity`.  
4. Counts of `INSERT` and `UPDATE` actions are captured.  
5. A row is inserted into `SyncLog` with those counts and any error message.  
6. Transaction is committed; on error, it rolls back, logs the error, and rethrows.

#### Dependencies
- Internal tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.  
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Transactional integrity: all updates and logging occur within a single transaction.  
- Error handling: rollback on failure, error message stored in `SyncLog`.  
- Time‑stamping: `UpdatedAt` and `StartedAt` use UTC timestamps.
