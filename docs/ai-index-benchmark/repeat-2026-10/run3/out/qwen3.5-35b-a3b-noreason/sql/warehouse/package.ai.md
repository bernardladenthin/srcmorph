### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:39Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly T-SQL stored procedure that merges supplier feed quantities into warehouse stock levels and logs the synchronization outcome.

#### Purpose
- Synchronizes nightly supplier feed data into the `StockLevel` table by aggregating quantities per warehouse and SKU.
- Records synchronization metrics (inserts, updates, errors) into the `SyncLog` table.

#### Responsibilities
- **Data Aggregation**: Filters `SupplierFeed` by `ReceivedAt` and aggregates `Quantity` by `WarehouseId` and `Sku`.
- **Stock Synchronization**: Executes `MERGE` operations to update existing `StockLevel` rows or insert new ones based on feed data.
- **Audit & Error Handling**: Logs operation counts and error messages to `SyncLog` within a `TRY...CATCH` transaction block.

#### Key units
- `dbo.usp_SyncStock`: Main stored procedure orchestrating the feed merge and logging logic.
- `dbo.StockLevel`: Target table storing current `OnHand` quantities per warehouse and SKU.
- `dbo.SupplierFeed`: Source table containing raw incoming feed records with `Quantity` and `ReceivedAt`.
- `dbo.SyncLog`: Audit table recording `RowsInserted`, `RowsUpdated`, and `ErrorMessage` per sync run.

#### Data flow
1. Input `@Since` parameter filters `dbo.SupplierFeed` for records received after the specified time.
2. Aggregated feed data is joined against `dbo.StockLevel` using `WarehouseId` and `Sku` keys.
3. `MERGE` operation updates `OnHand` and `UpdatedAt` for matches; inserts new rows for non-matches.
4. `MERGE` output actions are captured to calculate row counts.
5. Summary counts and execution status are written to `dbo.SyncLog`.

#### Dependencies
- **Internal Tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **System Functions**: `SYSUTCDATETIME()` for timestamps, `ERROR_MESSAGE()` for error capture, `$action` for MERGE tracking.

#### Cross-cutting
- **Transaction Safety**: Logic wrapped in `TRY...CATCH` to ensure atomic commits or rollbacks.
- **Idempotency**: Sync logic relies on `@Since` parameter to process only new or unprocessed feed records.
