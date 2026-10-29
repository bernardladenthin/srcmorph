### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:03:05Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Warehouse stock synchronisation from a supplier feed, merging new and updated rows into the StockLevel table and logging the operation.

#### Purpose
- Merges supplier feed rows received since a given timestamp into the StockLevel table and logs the operation results.

#### Responsibilities
- Ingest and merge supplier feed data into the warehouse stock level table.
- Track merge outcomes (inserts, updates) and capture errors in a dedicated log table.

#### Key units
- **stock_sync_tsql.sql**: T-SQL stored procedure that performs a MERGE operation to upsert stock levels from a supplier feed and logs the results into SyncLog.

#### Data flow
- Reads aggregated supplier feed rows (filtered by `@Since` timestamp, grouped by WarehouseId and Sku).
- Merges into StockLevel: updates OnHand for existing rows, inserts new rows for missing keys.
- Captures merge actions into a table variable, then inserts summary counts and any error message into SyncLog.

#### Dependencies
- `dbo.StockLevel`: target table for the MERGE operation.
- `dbo.SupplierFeed`: source table of feed rows, filtered by `@Since`.
- `dbo.SyncLog`: log table for operation results and errors.
- `SYSUTCDATETIME()`: system function used for timestamps.

#### Cross-cutting
- Uses explicit transaction handling (try/catch with commit/rollback) to ensure atomicity of the merge and log operations.
- Relies on merge-key joins between `StockLevel.WarehouseId` / `StockLevel.Sku` and `SupplierFeed.WarehouseId` / `SupplierFeed.Sku`.
