### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:14:02Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes recent supplier-feed quantities into warehouse stock balances and records the outcome of each synchronization.

#### Purpose
- Aggregate supplier quantities by warehouse and SKU since a cutoff, then apply them to current stock.
- Track inserted and updated rows, or log and rethrow synchronization errors.

#### Responsibilities
- **Stock synchronization:** `dbo.usp_SyncStock` aggregates eligible feed rows and merges totals into `dbo.StockLevel`.
- **Outcome logging:** write synchronization counts or failure details to `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since)`: transactionally updates stock from recent supplier-feed quantities and records the result.
- `dbo.SupplierFeed`: source rows with warehouse, SKU, quantity, and receipt time.
- `dbo.StockLevel`: current on-hand quantities keyed by warehouse and SKU.
- `dbo.SyncLog`: per-run start time, inserted and updated counts, and optional error message.

#### Data flow
- Read `dbo.SupplierFeed` rows with `ReceivedAt >= @Since`; group by `WarehouseId` and `Sku`, summing `Quantity`.
- Merge the aggregates into `dbo.StockLevel`: update matched quantities and timestamps; insert unmatched warehouse/SKU pairs.
- Count merge actions, log successful counts, and commit; on failure, roll back, log the error, and rethrow.

#### Dependencies
- SQL Server T-SQL; `dbo.usp_SyncStock` operates on `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
- Stock changes and success logging run in a transaction.
- Errors trigger rollback of any active transaction, failure logging with zero counts, and rethrowing.
