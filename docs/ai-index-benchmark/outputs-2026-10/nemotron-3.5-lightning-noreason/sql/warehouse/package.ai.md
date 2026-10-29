### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:55:06Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly stock levels from a supplier feed into the warehouse inventory, merging new and updated quantities and logging the result.

#### Purpose
- Merges supplier feed changes into StockLevel since a given timestamp
- Logs insert/update counts and errors into SyncLog

#### Responsibilities
- Executes a T-SQL MERGE to upsert aggregated supplier quantities into StockLevel
- Captures and logs merge outcomes (inserts, updates, errors) into SyncLog
- Provides atomic error handling with transaction rollback and error logging

#### Key units
- **stock_sync_tsql.sql**: Stored procedure implementing the merge logic, aggregating SupplierFeed changes and writing to StockLevel and SyncLog

#### Data flow
- Reads dbo.SupplierFeed filtered by @Since, aggregates by WarehouseId and Sku
- MERGE into dbo.StockLevel matching on WarehouseId and Sku, updating OnHand and UpdatedAt or inserting new rows
- Captures $action into a table variable @Changes
- Inserts aggregated counts (RowsInserted, RowsUpdated) into dbo.SyncLog
- On error: rolls back transaction and inserts error row into dbo.SyncLog before re-throwing

#### Dependencies
- dbo.StockLevel
- dbo.SupplierFeed
- dbo.SyncLog
- System function SYSUTCDATETIME()

#### Cross-cutting
- Uses table variable @Changes to capture merge actions
- Follows pattern of atomic transaction with error logging into SyncLog
- Relies on merge join between StockLevel and aggregated SupplierFeed on WarehouseId, Sku
