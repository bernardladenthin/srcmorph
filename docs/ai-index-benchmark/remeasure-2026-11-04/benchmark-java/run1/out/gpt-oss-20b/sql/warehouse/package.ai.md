### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:19:24Z
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
- **Audit trail** – write a summary record to `dbo.SyncLog`.

#### Key units
- **dbo.usp_SyncStock(@Since DATETIME2(0))** – T‑SQL procedure that performs the nightly sync.
- **dbo.StockLevel** – target table holding current on‑hand quantities per warehouse and SKU.
- **dbo.SupplierFeed** – source table containing raw feed rows.
- **dbo.SyncLog** – log table capturing start time, rows inserted/updated, and any error message.

#### Data flow
1. `usp_SyncStock` receives a `@Since` timestamp.  
2. It aggregates `SupplierFeed` rows received after `@Since` by `WarehouseId` and `Sku`.  
3. A `MERGE` statement updates existing `StockLevel` rows (incrementing `OnHand`) or inserts new rows.  
4. The merge’s output is captured to count inserts and updates.  
5. A single row is inserted into `SyncLog` with the counts and any error message.  
6. Transaction is committed; on failure, rollback and log the error.

#### Dependencies
- **Internal** – `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
- **SQL Server functions** – `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.  
- **Transaction control** – `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.

#### Cross-cutting
- All operations run within a single transaction to ensure atomicity.  
- Error handling captures `ERROR_MESSAGE()` and records it in `SyncLog`.  
- Timestamps use UTC (`SYSUTCDATETIME()`) for consistency across feeds.
