### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:24:27Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes supplier inventory feeds into warehouse stock balances and records each synchronization’s results or errors.

#### Purpose
- Apply quantities received since a supplied timestamp to warehouse stock, grouped by warehouse and SKU.
- Track inserted and updated stock rows, or record synchronization errors.

#### Responsibilities
- Aggregate recent `dbo.SupplierFeed` quantities by `(WarehouseId, Sku)` and add them to existing `dbo.StockLevel` balances or create new stock rows.
- Record synchronization start time and row counts in `dbo.SyncLog`; log errors and rethrow them on failure.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — runs the feed-to-stock synchronization and logs its outcome.
- `dbo.StockLevel` — stores on-hand quantities keyed by `(WarehouseId, Sku)`.
- `dbo.SupplierFeed` — stores supplier quantities and receipt timestamps.
- `dbo.SyncLog` — stores synchronization timestamps, inserted/updated counts, and optional error messages.

#### Data flow
- Filter `dbo.SupplierFeed` by `ReceivedAt >= @Since`; sum `Quantity` per warehouse and SKU.
- Merge aggregates into `dbo.StockLevel`, adding quantities to matches or inserting new rows; capture merge actions to count inserts and updates.
- Write counts to `dbo.SyncLog` on success; on failure, roll back, log the error with zero counts, and rethrow.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and merges into `dbo.StockLevel`; it writes outcomes to `dbo.SyncLog`.

#### Cross-cutting
- Synchronization work is transactional, with rollback when an error occurs during an active transaction.
- Errors are recorded using `ERROR_MESSAGE()` and rethrown.
