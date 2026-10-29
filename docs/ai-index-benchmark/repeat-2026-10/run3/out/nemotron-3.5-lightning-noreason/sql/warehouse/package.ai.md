### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:42:02Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly stock levels from a supplier feed into the warehouse stock table and logs the operation.

#### Purpose
- Merges supplier feed data into the warehouse stock table and records sync results in a log table.

#### Responsibilities
- **Stock merge**: Upserts new and updated stock quantities from the supplier feed into the StockLevel table using a MERGE operation.
- **Sync logging**: Inserts a record into SyncLog capturing the start time, row counts (inserted/updated), and any error messages.
- **Error handling**: Rolls back the transaction and logs errors to SyncLog before re-throwing.

#### Key units
- **dbo.StockLevel** (WarehouseId, Sku): Target table for the merge; stores current on-hand quantities and timestamps.
- **dbo.SupplierFeed** (WarehouseId, Sku, Quantity, ReceivedAt): Source feed table filtered by ReceivedAt to identify changes since the last sync.
- **dbo.SyncLog** (SyncLogId, StartedAt, RowsInserted, RowsUpdated, ErrorMessage): Log table capturing the outcome of each sync execution.
- **StockSync procedure**: The T-SQL routine that orchestrates the merge and logging logic.

#### Data flow
1. Procedure declares start timestamp and a table variable to capture merge actions.
2. Executes MERGE on dbo.StockLevel, grouped by WarehouseId and Sku from dbo.SupplierFeed where ReceivedAt >= @Since.
3. When MATCHED: updates OnHand (adds source Quantity) and sets UpdatedAt.
4. WHEN NOT MATCHED BY TARGET: inserts new StockLevel rows with source WarehouseId, Sku, and OnHand.
5. Outputs $action into a table variable; aggregated counts are inserted into dbo.SyncLog.
6. On success, the transaction commits; on error, it rolls back, logs the error, and re-throws.

#### Dependencies
- dbo.StockLevel
- dbo.SupplierFeed
- dbo.SyncLog

#### Cross-cutting
- Uses a table variable (@Changes) to capture MERGE $action outcomes for subsequent logging.
- Error handling pattern: explicit transaction management with rollback and error logging into dbo.SyncLog before re-throwing.
