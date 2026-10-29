### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:16:06Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> This package synchronizes supplier-provided stock data with the central warehouse inventory system, ensuring data consistency through transactional updates and detailed audit logging.

#### Purpose  
- Synchronizes incoming supplier feed data with the central stock level table using merge operations.  
- Maintains auditability by logging the number of rows inserted, updated, and any errors encountered during sync.

#### Responsibilities  
- Stock data synchronization from supplier feeds to warehouse inventory.  
- Transactional integrity and error handling during data sync.  
- Audit logging of sync operations for traceability and debugging.

#### Key units  
- `dbo.StockLevel`: Central table storing current on-hand stock levels by warehouse and SKU, with a composite primary key.  
- `dbo.SupplierFeed`: Source table containing supplier-provided stock quantities and timestamps.  
- `dbo.SyncLog`: Audit table recording sync start time, row counts, and error messages.  
- `stock_sync_tsql.sql`: T-SQL procedure that performs the merge operation and logs results.

#### Data flow  
- Incoming supplier feed data is filtered by `@Since` timestamp.  
- Feed records are grouped by (WarehouseId, Sku) and compared against existing stock levels.  
- A MERGE statement updates or inserts stock levels based on match.  
- Inserted and updated row counts are aggregated and stored in `SyncLog`.  
- All operations occur within a transaction, with rollback on failure.

#### Dependencies  
- `dbo.StockLevel` (source and target for stock updates)  
- `dbo.SupplierFeed` (input data source)  
- `dbo.SyncLog` (audit logging)  
- `SYSUTCDATETIME()` (for timestamping updates and logs)

#### Cross-cutting  
- Uses transactional boundaries to ensure data consistency.  
- Logs detailed metrics (inserted/updated rows, errors) for operational visibility.  
- Relies on composite keys (WarehouseId, Sku) for data integrity.  
- Filters input data by a since timestamp to support incremental syncs.
