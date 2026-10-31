### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:12:54Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory table and records the operation outcome.

#### Purpose
- Update `dbo.StockLevel` with aggregated supplier feed data since a given timestamp.  
- Log the number of rows inserted/updated and any errors in `dbo.SyncLog`.

#### Responsibilities
- **Data ingestion** – read and aggregate `dbo.SupplierFeed` rows.  
- **Inventory update** – merge aggregated data into `dbo.StockLevel`.  
- **Audit logging** – record sync statistics and errors in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock` – T‑SQL procedure that performs the nightly sync.  
- `dbo.StockLevel` – inventory table keyed by `(WarehouseId, Sku)`.  
- `dbo.SupplierFeed` – staging table holding raw supplier quantities.  
- `dbo.SyncLog` – log table capturing sync start time, row counts, and error messages.

#### Data flow
1. `usp_SyncStock` receives `@Since`.  
2. Aggregates `SupplierFeed` rows (`WarehouseId`, `Sku`, `Quantity`) where `ReceivedAt >= @Since`.  
3. Merges the aggregation into `StockLevel`:  
   - If a key exists, `OnHand` is incremented and `UpdatedAt` refreshed.  
   - If not, a new row is inserted.  
4. Counts of inserted and updated rows are captured.  
5. A single row is inserted into `SyncLog` with the counts and any error message.  
6. Transaction is committed or rolled back on failure.

#### Dependencies
- **Internal** – `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.  
- **System** – `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`.

#### Cross-cutting
- All operations occur within a single transaction to ensure atomicity.  
- Default constraints provide `OnHand = 0` and `UpdatedAt` as the current UTC time.  
- Error handling logs the message in `SyncLog` and rolls back the transaction.
