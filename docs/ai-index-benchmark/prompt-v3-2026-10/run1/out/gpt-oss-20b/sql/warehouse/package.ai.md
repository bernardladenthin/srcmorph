### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:56:33Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into a warehouse inventory system and records the sync outcome.

#### Purpose
- Merge supplier feed rows into `StockLevel`.
- Log sync statistics and errors in `SyncLog`.

#### Responsibilities
- **Data ingestion** – aggregate and merge supplier feed data into the inventory table.
- **Change tracking** – record counts of inserted and updated rows.
- **Error handling** – capture and log any failures during the sync.

#### Key units
- **dbo.StockLevel** – inventory table keyed by `WarehouseId`/`Sku`; holds `OnHand` and `UpdatedAt`.
- **dbo.SupplierFeed** – staging table for nightly supplier rows; primary key `FeedRowId`.
- **dbo.SyncLog** – audit table for each sync run; columns `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.
- **dbo.usp_SyncStock** – T‑SQL procedure that performs the merge, logs results, and handles transactions.

#### Data flow
1. `dbo.usp_SyncStock(@Since)` starts, records `@Started`.
2. Begins a transaction.
3. Aggregates `SupplierFeed` rows where `ReceivedAt >= @Since`.
4. `MERGE` into `StockLevel`: update `OnHand`/`UpdatedAt` on match, insert new rows on miss.
5. Counts `INSERT` and `UPDATE` actions into `@Changes`.
6. Inserts a row into `SyncLog` with counts and `@Started`.
7. Commits transaction; on error rolls back, logs error, rethrows.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- SQL Server system functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- Transaction control via `@@TRANCOUNT`.

#### Cross-cutting
- Uses a single transaction to ensure atomicity of merge and logging.
- Error handling pattern: `TRY…CATCH` with rollback and error log insertion.
- Default constraints on `StockLevel` (`OnHand`, `UpdatedAt`) and `SyncLog` (`SyncLogId` identity).
