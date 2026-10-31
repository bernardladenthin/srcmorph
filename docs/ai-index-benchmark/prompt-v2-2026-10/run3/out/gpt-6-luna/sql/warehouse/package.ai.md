### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:01:24Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier-received quantities to warehouse stock levels and records synchronization counts or errors.

#### Purpose
- Aggregate supplier feed quantities by warehouse and SKU since a cutoff, then add them to on-hand inventory.
- Log synchronization results and rethrow failures after error logging.

#### Responsibilities
- **Inventory synchronization:** Merge grouped `dbo.SupplierFeed` quantities into `dbo.StockLevel`, inserting new warehouse/SKU rows or updating existing quantities and timestamps.
- **Outcome logging:** Record inserted and updated row counts in `dbo.SyncLog`; log error messages when synchronization fails.

#### Key units
- `dbo.usp_SyncStock(@Since)`: runs the transactional feed aggregation, stock merge, outcome logging, and failure handling.
- `dbo.SupplierFeed`: stores supplier quantities and receipt timestamps used as the synchronization input.
- `dbo.StockLevel`: stores on-hand quantities keyed by `(WarehouseId, Sku)`.
- `dbo.SyncLog`: stores synchronization start time, action counts, and optional error message.

#### Data flow
- Filter `dbo.SupplierFeed` to `ReceivedAt >= @Since`; group by `WarehouseId` and `Sku`, summing `Quantity`.
- Merge aggregates into `dbo.StockLevel`: add quantities to matched rows and insert unmatched rows; capture merge actions to count inserts and updates.
- Commit and log counts in `dbo.SyncLog`; on failure, roll back if needed, log the error with zero counts, and rethrow.

#### Dependencies
- SQL Server built-in `SYSUTCDATETIME()` for UTC timestamps.
- `dbo.usp_SyncStock` coordinates `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Uses a transaction for the stock merge and success logging, with rollback-on-failure handling.
- `dbo.StockLevel` defaults `OnHand` to `0` and `UpdatedAt` to the current UTC time.
