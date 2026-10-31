### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:58:29Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock levels are synchronized from a supplier feed by aggregating received quantities per warehouse/SKU, upserting them into StockLevel, and logging insert/update counts or errors in SyncLog.

#### Purpose
- Merges aggregated supplier feed rows into dbo.StockLevel by WarehouseId and Sku.
- Inserts a summary row into dbo.SyncLog with insert/update counts and any error message.

#### Responsibilities
- T-SQL stock synchronization stored procedure (dbo.usp_SyncStock) for warehouse inventory.
- Aggregation of dbo.SupplierFeed receipts since a given timestamp grouped by WarehouseId and Sku.
- Upsert (MERGE) into dbo.StockLevel updating OnHand and UpdatedAt or inserting new stock rows.
- Audit logging of insert/update counts and errors into dbo.SyncLog within a transaction with rollback on failure.

#### Key units
- dbo.usp_SyncStock (T-SQL stored procedure): parameterized @Since, performs MERGE upsert and SyncLog logging.
- dbo.StockLevel (table): warehouse/SKU stock target with OnHand and UpdatedAt.
- dbo.SupplierFeed (table): source feed with WarehouseId, Sku, Quantity, ReceivedAt.
- dbo.SyncLog (table): audit log with StartedAt, RowsInserted, RowsUpdated, ErrorMessage.

#### Data flow
- Input @Since DATETIME2(0) filters dbo.SupplierFeed by ReceivedAt >= @Since.
- Rows are grouped by WarehouseId, Sku and summed into Quantity.
- MERGE targets dbo.StockLevel on WarehouseId and Sku: matched rows add Quantity to OnHand and refresh UpdatedAt; unmatched rows insert new stock.
- OUTPUT $action captures insert/update counts into @Changes.
- A dbo.SyncLog row is written with StartedAt, aggregated counts, and any error message.
- Transaction commits on success; on error, rolls back, logs ERROR_MESSAGE(), and THROWs.

#### Dependencies
- Internal: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog.
- System: SYSUTCDATETIME, ERROR_MESSAGE, @@TRANCOUNT, transaction control.
- No declared foreign keys between tables.

#### Cross-cutting
- Single T-SQL file in sql/warehouse.
- Transaction scope with try/catch rollback and rethrow.
- UTC timestamps via SYSUTCDATETIME.
- MERGE pattern with OUTPUT clause into table variable for change counting.
