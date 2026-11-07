### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:39:23Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Reconciles recent supplier inventory receipts into warehouse stock balances and records each synchronization outcome.

#### Purpose
- Aggregate supplier feed quantities by warehouse and SKU, then apply them to on-hand stock.
- Record successful row counts or failed synchronization details in an audit log.

#### Responsibilities
- **Inventory updates:** Group recent `dbo.SupplierFeed` receipts and merge totals into `dbo.StockLevel`.
- **Outcome logging:** Record inserted and updated row counts on success, or the error message on failure, in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since)`: Synchronizes feed quantities received since the cutoff and logs the result.
- `dbo.StockLevel`: Stores on-hand quantity and update time by warehouse and SKU.
- `dbo.SupplierFeed`: Holds supplier receipt quantities and timestamps.
- `dbo.SyncLog`: Stores synchronization start time, row counts, and optional error message.

#### Data flow
- `dbo.usp_SyncStock` filters `dbo.SupplierFeed` by `ReceivedAt >= @Since` and sums `Quantity` by `WarehouseId` and `Sku`.
- A `MERGE` adds totals to matching `dbo.StockLevel` rows or inserts rows for unmatched keys; merge actions determine outcome counts.
- The procedure writes counts to `dbo.SyncLog` and commits, or rolls back and logs the error before rethrowing it.

#### Dependencies
- SQL Server tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- Built-ins: `SYSUTCDATETIME()` for the start timestamp and `ERROR_MESSAGE()` for failure logging.

#### Cross-cutting
- Stock changes and success logging run in a transaction.
- Errors trigger rollback of an active transaction, zero-count error logging, and rethrowing.
