### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:08Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly T-SQL stored procedure that merges supplier feed quantities into warehouse stock levels and logs the sync outcome.

#### Purpose
- Synchronizes nightly supplier feed data into the `StockLevel` table by aggregating quantities per warehouse and SKU.
- Records sync execution metrics (inserts, updates, errors) in the `SyncLog` table.

#### Responsibilities
- **Data Aggregation**: Groups `Quantity` from `SupplierFeed` by `WarehouseId` and `Sku` based on the `@Since` timestamp.
- **Stock Synchronization**: Executes `MERGE` operations to update existing `StockLevel` rows or insert new ones.
- **Audit Logging**: Tracks row counts and error states in `SyncLog` for every execution cycle.

#### Key units
- `dbo.usp_SyncStock`: Main stored procedure orchestrating the nightly sync logic.
- `dbo.StockLevel`: Target table storing current `OnHand` quantities per warehouse and SKU.
- `dbo.SupplierFeed`: Source table containing raw incoming feed rows with `ReceivedAt` timestamps.
- `dbo.SyncLog`: Audit table recording `RowsInserted`, `RowsUpdated`, and `ErrorMessage` per run.

#### Data flow
1. **Input**: Procedure receives `@Since` datetime parameter.
2. **Read**: Filters `dbo.SupplierFeed` for records where `ReceivedAt >= @Since`.
3. **Aggregate**: Sums `Quantity` by `WarehouseId` and `Sku`.
4. **Merge**: Joins aggregated data against `dbo.StockLevel` on `WarehouseId` and `Sku`.
   - **Matched**: Updates `OnHand` (additive) and `UpdatedAt`.
   - **Not Matched**: Inserts new `WarehouseId`, `Sku`, and `OnHand`.
5. **Capture**: Extracts `INSERT` vs `UPDATE` counts via `OUTPUT $action`.
6. **Write**: Commits changes to `StockLevel` and inserts summary into `SyncLog`.
7. **Error Handling**: On failure, rolls back transaction and logs `ErrorMessage` to `SyncLog`.

#### Dependencies
- **Internal Tables**: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- **System Functions**: `SYSUTCDATETIME()` (default for `UpdatedAt`), `ERROR_MESSAGE()` (error capture).

#### Cross-cutting
- **Transaction Management**: Uses explicit transaction rollback on error to ensure data consistency.
- **Error Handling**: Catches exceptions to populate `ErrorMessage` in `SyncLog` before re-throwing.
- **Concurrency**: Relies on `MERGE` statement atomicity for safe concurrent updates.
