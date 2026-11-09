### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:27:36Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, logging each run and handling errors.

#### Purpose
- Aggregate supplier feed quantities into the warehouse `StockLevel`.
- Record each sync operation in `SyncLog`.

#### Responsibilities
- **Data ingestion**: read `SupplierFeed` rows since a given timestamp.
- **Inventory update**: merge aggregated quantities into `StockLevel`.
- **Audit & error handling**: log inserts/updates and any failures in `SyncLog`.

#### Key units
- **dbo.StockLevel** – table of current on‑hand quantities per warehouse and SKU.
- **dbo.SupplierFeed** – staging table for raw supplier feed rows.
- **dbo.SyncLog** – audit trail of each sync run (counts, timestamps, errors).
- **dbo.usp_SyncStock** – stored procedure orchestrating the nightly sync.

#### Data flow
1. `dbo.usp_SyncStock(@Since)` starts a transaction.  
2. Aggregates `SupplierFeed` rows received after `@Since` by `WarehouseId`/`Sku`.  
3. `MERGE` into `StockLevel`:  
   - `WHEN MATCHED`: `OnHand += Quantity`, update `UpdatedAt`.  
   - `WHEN NOT MATCHED`: insert new row with `OnHand = Quantity`.  
4. Capture merge actions into a table variable.  
5. Insert a single row into `SyncLog` with counts of inserted/updated rows or an error message.  
6. Commit or rollback on error.

#### Dependencies
- Internal tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.  
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.  
- SQL Server features: `MERGE`, `OUTPUT`, `IDENTITY`.

#### Cross-cutting
- Transactional integrity: all updates and logging occur within a single transaction.  
- Error handling: rollback on exception, log error message, rethrow.  
- T‑SQL patterns: use of `MERGE` with `OUTPUT` to capture row counts, aggregation via `GROUP BY`.
