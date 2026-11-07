### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:35Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Maintains warehouse stock levels by applying recent supplier-feed quantities and recording each synchronization’s outcome.

#### Purpose
- Aggregate eligible supplier receipts by warehouse and SKU, then add quantities to current on-hand stock.
- Log synchronization counts on success or the error on failure.

#### Responsibilities
- **Stock synchronization:** Match aggregated supplier quantities to `dbo.StockLevel`, inserting new warehouse/SKU rows or increasing existing balances.
- **Execution logging:** Record inserted and updated row counts, or capture the failure message.

#### Key units
- `dbo.usp_SyncStock(@Since)`: Aggregates feed rows received since the cutoff, merges them into stock, and logs the outcome.
- `dbo.StockLevel`: Stores on-hand quantities keyed by warehouse and SKU, with update timestamps.
- `dbo.SupplierFeed`: Supplies dated warehouse/SKU quantity records.
- `dbo.SyncLog`: Stores execution start time, affected-row counts, and optional error message.
- `@Changes`: Captures `$action` values for affected stock rows to calculate insert and update counts.

#### Data flow
- `dbo.SupplierFeed` rows with `ReceivedAt >= @Since` are grouped by `WarehouseId` and `Sku`, and their quantities are summed.
- `dbo.usp_SyncStock` merges the aggregates into `dbo.StockLevel`: inserts unmatched keys and adds quantities to matched rows.
- Merge actions determine success log counts; failures roll back when needed, log the error with zero counts, and rethrow.

#### Dependencies
- Internal tables: `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- Warehouse and SKU values link feed aggregates to stock rows through merge matching; these are not declared foreign keys.

#### Cross-cutting
- Synchronization uses a transaction; errors trigger rollback when needed and are rethrown after logging.
- Start times and stock update timestamps use UTC time.
