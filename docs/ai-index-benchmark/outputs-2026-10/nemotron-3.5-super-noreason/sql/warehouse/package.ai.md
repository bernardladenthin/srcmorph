### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:05:11Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronization aggregates supplier feed quantities received since a cutoff, merges them into stock levels, and logs insert/update counts or errors.

#### Purpose
- Synchronizes supplier feed quantities into warehouse stock levels for a given time window.
- Records sync statistics and any error messages in a sync log.

#### Responsibilities
- T-SQL stored procedure logic for nightly warehouse stock synchronization.

#### Key units
- `dbo.StockLevel` — target table keyed by WarehouseId and Sku holding OnHand and UpdatedAt.
- `dbo.SupplierFeed` — source table of supplier feed rows keyed by FeedRowId with WarehouseId, Sku, Quantity, and ReceivedAt.
- `dbo.SyncLog` — log table recording StartedAt, RowsInserted, RowsUpdated, and optional ErrorMessage.
- `stock_sync_tsql.sql` — T-SQL procedure parameterized by @Since that aggregates feeds, MERGEs into stock levels, outputs change counts, and logs results or errors.

#### Data flow
- Reads dbo.SupplierFeed rows where ReceivedAt >= @Since, grouped by WarehouseId and Sku.
- MERGEs aggregated quantities into dbo.StockLevel: matched rows increment OnHand and refresh UpdatedAt; unmatched rows insert new stock levels.
- Captures $action into a table variable to count inserts and updates.
- Inserts one dbo.SyncLog row with StartedAt and counts; on error rolls back, inserts an error log row with zero counts, and THROWs.

#### Dependencies
- Internal: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog.
- SQL Server functions: SYSUTCDATETIME, ERROR_MESSAGE, @@TRANCOUNT.

#### Cross-cutting
- Transactional error handling with rollback and error logging.
- Logical join between dbo.SupplierFeed and dbo.StockLevel on WarehouseId and Sku without declared foreign keys.
