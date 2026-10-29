### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:41:38Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse inventory state from supplier feed data using transactional MERGE operations with audit logging.

#### Purpose
*   Maintains `dbo.StockLevel` inventory state based on `dbo.SupplierFeed` incoming data.
*   Records synchronization metrics and failures in `dbo.SyncLog`.

#### Responsibilities
*   Aggregates `dbo.SupplierFeed` by `WarehouseId` and `Sku` filtered by `ReceivedAt`.
*   Executes `MERGE` operations to update or insert `dbo.StockLevel` rows.
*   Captures merge action counts and writes summary to `dbo.SyncLog`.

#### Key units
*   `dbo.StockLevel`: Stores `OnHand` inventory counts keyed by `WarehouseId` and `Sku`.
*   `dbo.SupplierFeed`: Holds incoming `Quantity` records with `ReceivedAt` timestamps.
*   `dbo.SyncLog`: Records `RowsInserted`, `RowsUpdated`, and `ErrorMessage` per sync run.
*   `dbo.usp_SyncStock`: Stored procedure orchestrating the synchronization logic.

#### Data flow
*   Input `@Since` datetime filters `dbo.SupplierFeed` records.
*   Aggregated feed data drives `MERGE` operations against `dbo.StockLevel`.
*   Merge action counts populate temp table `@Changes`.
*   Summary metrics and status written to `dbo.SyncLog`.

#### Dependencies
*   Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
*   Functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
*   Transactional integrity via rollback on error.
*   Error handling inserts `ErrorMessage` into `dbo.SyncLog`.
*   Timezone handling via `DATETIME2(0)` and `SYSUTCDATETIME()`.
