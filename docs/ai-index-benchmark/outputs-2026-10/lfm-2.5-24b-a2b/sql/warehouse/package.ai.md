### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:57:02Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Implements nightly warehouse stock synchronization by merging supplier feed quantities into stock levels and recording the merge outcome in a sync log.

#### Purpose
- Merge supplier feed data into dbo.StockLevel for nightly warehouse stock synchronization.
- Log merge outcome (rows inserted, rows updated, errors) in dbo.SyncLog.

#### Responsibilities
- Implement dbo.usp_SyncStock procedure to read SupplierFeed and StockLevel and execute MERGE.
- Maintain dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog objects.
- Record synchronization results in SyncLog with counts and error messages.

#### Key units
- dbo.usp_SyncStock — procedure that performs the MERGE and writes to StockLevel and SyncLog.
- dbo.StockLevel — target table for stock quantities (WarehouseId, Sku, OnHand, UpdatedAt).
- dbo.SupplierFeed — source of nightly feed rows (WarehouseId, Sku, Quantity, ReceivedAt).
- dbo.SyncLog — audit table for merge outcomes (StartedAt, RowsInserted, RowsUpdated, ErrorMessage).

#### Data flow
- Read supplier feed rows from dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt).
- Begin transaction; MERGE aggregated feed rows since @Since into StockLevel.
- WHEN MATCHED: update OnHand and UpdatedAt; WHEN NOT MATCHED: insert new row.
- Output action to @Changes; insert counts into SyncLog.
- Commit on success; ROLLBACK and log error on failure.

#### Dependencies
- References dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog.
- Uses system functions SYSUTCDATETIME, IDENTITY, ERROR_MESSAGE, THROW.

#### Cross-cutting
- Transactional: BEGIN/COMMIT/ROLLBACK with error handling via ERROR_MESSAGE/THROW.
- Parameterized procedure with @Since, @Started, @Changes.
- Logical join on WarehouseId and Sku between StockLevel and SupplierFeed.
- No explicit foreign key constraints defined in schema.
