### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:10:41Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock reconciliation that merges supplier feed quantities into per-warehouse SKU stock levels and logs the outcome.

#### Purpose
- Provides a T-SQL data model and stored procedure for synchronizing warehouse inventory levels from supplier feeds.
- Ensures auditability of each sync run through a dedicated log table.

#### Responsibilities
- **Stock Level Management**: Maintains current on-hand quantities per `WarehouseId` and `Sku` in `dbo.StockLevel`.
- **Feed Ingestion**: Consumes raw quantity updates from `dbo.SupplierFeed` filtered by a time window.
- **Reconciliation Logic**: Executes a `MERGE` operation to increment existing stock or insert new SKU records based on aggregated feed data.
- **Audit Logging**: Records the start time, row counts (inserted/updated), and any error messages for each execution in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock`: Stored procedure that orchestrates the transactional merge of `SupplierFeed` into `StockLevel` and writes to `SyncLog`.
- `dbo.StockLevel`: Table storing current inventory state with composite primary key `(WarehouseId, Sku)` and `OnHand` quantity.
- `dbo.SupplierFeed`: Table holding raw incoming quantity records with `FeedRowId`, `WarehouseId`, `Sku`, `Quantity`, and `ReceivedAt`.
- `dbo.SyncLog`: Audit table tracking sync execution metadata including `RowsInserted`, `RowsUpdated`, and `ErrorMessage`.

#### Data flow
1. `dbo.usp_SyncStock` accepts a `@Since` parameter to filter `dbo.SupplierFeed` rows.
2. Feed rows are aggregated by `SUM(Quantity)` grouped by `WarehouseId` and `Sku`.
3. A `MERGE` statement updates `dbo.StockLevel` (`OnHand` increment) or inserts new rows for unmatched SKUs.
4. The `$action` output from the `MERGE` is captured to count inserted vs. updated rows.
5. A summary row is inserted into `dbo.SyncLog` within the same transaction; on failure, an error row is logged before re-throwing.

#### Dependencies
- **Internal**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **External**: SQL Server built-ins `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`.

#### Cross-cutting
- **Transactional Integrity**: The entire sync process (merge + log) is wrapped in a single transaction with explicit `COMMIT`/`ROLLBACK` handling.
- **Error Handling**: Uses `TRY...CATCH` to ensure `dbo.SyncLog` records failures with `ErrorMessage` before propagating the exception via `THROW`.
- **Time Handling**: Uses `DATETIME2(0)` and `SYSUTCDATETIME()` for consistent UTC timestamping across `StockLevel.UpdatedAt`, `SupplierFeed.ReceivedAt`, and `SyncLog.StartedAt`.
