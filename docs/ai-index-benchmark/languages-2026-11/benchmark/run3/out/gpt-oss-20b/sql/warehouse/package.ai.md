### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:07:12Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Keeps the warehouse inventory in sync with nightly supplier feeds, logging changes and errors.

#### Purpose
- Merge supplier feed data into current stock levels.
- Record counts of inserted/updated rows and any errors.

#### Responsibilities
- **Data ingestion** – read `dbo.SupplierFeed` rows since a given timestamp.
- **Stock reconciliation** – update or insert into `dbo.StockLevel` via a MERGE.
- **Audit logging** – write a summary record to `dbo.SyncLog` with operation counts and error details.

#### Key units
- **`dbo.usp_SyncStock`** – T‑SQL procedure that aggregates feed rows, performs a MERGE into `dbo.StockLevel`, and logs the result in `dbo.SyncLog`.
- **`dbo.StockLevel`** – inventory table keyed by `(WarehouseId, Sku)` with `OnHand` and `UpdatedAt`.
- **`dbo.SupplierFeed`** – staging table for nightly supplier rows, keyed by `FeedRowId`.
- **`dbo.SyncLog`** – audit table recording `StartedAt`, `RowsInserted`, `RowsUpdated`, and optional `ErrorMessage`.

#### Data flow
1. Caller supplies `@Since` timestamp.  
2. Procedure aggregates `SupplierFeed` rows received after `@Since` by `WarehouseId` and `Sku`.  
3. Aggregated quantities are MERGED into `StockLevel`: matched rows are updated, unmatched rows are inserted.  
4. Counts of INSERT and UPDATE actions are captured.  
5. A `SyncLog` record is inserted with the start time, row counts, and any error message.  
6. Transaction is committed; on failure, rollback and error log entry are made.

#### Dependencies
- **Internal** – `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.  
- **System** – `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`.

#### Cross-cutting
- Transactional integrity: all operations wrapped in a single transaction with rollback on error.  
- Error handling: captures `ERROR_MESSAGE()` into `SyncLog` and rethrows.  
- Default constraints on `OnHand` and `UpdatedAt` ensure data consistency.
