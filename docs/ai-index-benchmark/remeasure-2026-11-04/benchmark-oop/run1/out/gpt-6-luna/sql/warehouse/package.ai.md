### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:23:52Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies timestamp-filtered supplier stock receipts to warehouse/SKU inventory and records synchronization results or failures.

#### Purpose
- Synchronize aggregated supplier-feed quantities into on-hand stock levels.
- Record inserted and updated row counts, or the error message, for each synchronization attempt.

#### Responsibilities
- **Stock aggregation and application:** Group `dbo.SupplierFeed` quantities by warehouse and SKU, then insert or update corresponding `dbo.StockLevel` rows.
- **Outcome logging:** Write synchronization start time and row counts to `dbo.SyncLog`; record errors on failure.

#### Key units
- `dbo.usp_SyncStock(@Since)`: Transactionally applies feed quantities received since `@Since` and logs the outcome.
- `dbo.SupplierFeed`: Source rows containing warehouse, SKU, quantity, and receipt timestamp.
- `dbo.StockLevel`: On-hand inventory keyed by `WarehouseId` and `Sku`.
- `dbo.SyncLog`: Synchronization timestamps, inserted/updated counts, and optional error message.

#### Data flow
- Filter `dbo.SupplierFeed` by `ReceivedAt >= @Since`, then sum quantities per `WarehouseId` and `Sku`.
- `MERGE` aggregated totals into `dbo.StockLevel`: update matching rows or insert new ones.
- Capture merge actions to calculate insert/update counts and write a success row to `dbo.SyncLog`.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and matches/writes `dbo.StockLevel`; it writes outcomes to `dbo.SyncLog`.
- Uses SQL Server `SYSUTCDATETIME()` for the start time and `ERROR_MESSAGE()` for failure details.

#### Cross-cutting
- Uses a transaction; on error, rolls back any active transaction, logs the error with zero counts, and rethrows.
- `StockLevel` uses a composite primary key on `WarehouseId` and `Sku`; no foreign keys are declared.
