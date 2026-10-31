### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:34:40Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse SKU inventory by applying supplier quantities received since a cutoff and recording each run’s outcome.

#### Purpose
- Aggregate recent supplier feed quantities by warehouse and SKU, then add them to warehouse stock.
- Record inserted and updated row counts, or failure details, for each synchronization run.

#### Responsibilities
- **Inventory data:** `dbo.StockLevel` stores on-hand quantities keyed by warehouse and SKU; `dbo.SupplierFeed` stores incoming quantities and receipt times.
- **Synchronization audit:** `dbo.SyncLog` records run start times, row counts, and optional error messages.
- **Stock synchronization:** `dbo.usp_SyncStock` aggregates eligible feed rows and merges the results into stock.

#### Key units
- `dbo.StockLevel` — warehouse SKU balances with default quantity and UTC update timestamp.
- `dbo.SupplierFeed` — supplier quantities keyed by an identity feed-row ID.
- `dbo.SyncLog` — synchronization outcomes keyed by an identity log ID.
- `dbo.usp_SyncStock(@Since)` — applies feed quantities received on or after the cutoff and logs the result.
- `@Changes` — table variable capturing `MERGE` actions for inserted and updated row counts.

#### Data flow
- `dbo.usp_SyncStock` filters `dbo.SupplierFeed` by `ReceivedAt >= @Since`, groups by `WarehouseId` and `Sku`, and sums `Quantity`.
- The aggregates are merged into `dbo.StockLevel`: matching rows receive added quantities and an updated timestamp; unmatched rows are inserted.
- Merge actions determine success counts written to `dbo.SyncLog`; failures are logged with zero counts and the error message, then rethrown.

#### Dependencies
- Uses SQL Server T-SQL features including `MERGE`, transactions, `SYSUTCDATETIME()`, and `ERROR_MESSAGE()`.
- Matches `dbo.SupplierFeed` to `dbo.StockLevel` by `WarehouseId` and `Sku`; no foreign key is declared.

#### Cross-cutting
- Captures run and update timestamps in UTC.
- Wraps stock changes in a transaction; on error, rolls back any active transaction, writes a failure log entry, and rethrows.
