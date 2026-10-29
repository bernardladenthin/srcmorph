### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:58:20Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Orchestrates nightly batch merging of supplier feed data into warehouse inventory levels with transactional integrity and audit logging.

#### Purpose
- Executes a nightly batch merge to update `StockLevel` inventory counts based on new entries in `SupplierFeed`.
- Logs successful row counts or errors into `SyncLog` for operational monitoring.

#### Responsibilities
- **Ingestion & Aggregation**: Reads `SupplierFeed` records filtered by `ReceivedAt` and aggregates quantities by `WarehouseId` and `Sku`.
- **Inventory Synchronization**: Performs `MERGE` operations on `StockLevel` to insert new SKUs or increment existing `OnHand` counts.
- **Audit & Error Handling**: Records merge statistics (`RowsInserted`, `RowsUpdated`) or error details into `SyncLog` within a transactional scope.

#### Key units
- `dbo.usp_SyncStock`: Stored procedure orchestrating the nightly synchronization logic.
- `dbo.StockLevel`: Target table storing current `OnHand` inventory counts per warehouse and SKU.
- `dbo.SupplierFeed`: Source table containing raw incoming stock quantities from suppliers.
- `dbo.SyncLog`: Audit table tracking execution status, row counts, and error messages.

#### Data flow
1. Input `@Since` parameter filters `dbo.SupplierFeed` for new records.
2. Aggregated quantities are joined against `dbo.StockLevel` using `WarehouseId` and `Sku`.
3. `MERGE` operation updates `OnHand`/`UpdatedAt` or inserts new rows in `dbo.StockLevel`.
4. Merge actions (`INSERT`/`UPDATE`) are captured via `OUTPUT $action`.
5. Aggregate counts are written to `dbo.SyncLog`; errors trigger rollback and error logging.

#### Dependencies
- **Internal Tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **System Functions**: `SYSUTCDATETIME()` for timestamps, `ERROR_MESSAGE()` for exception details.

#### Cross-cutting
- **Transaction Management**: Uses explicit transaction blocks to ensure atomicity of inventory updates and log entries.
- **Error Handling**: Catches exceptions to roll back changes and persist error messages to `SyncLog` before re-throwing.
- **Concurrency**: Relies on `MERGE` semantics to handle concurrent updates safely within the batch window.
