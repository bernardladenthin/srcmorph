### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:38:56Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier-feed quantity changes to warehouse stock by SKU and records synchronization results.

#### Purpose
- Synchronize `dbo.StockLevel` from supplier quantities received since a specified timestamp.
- Log successful insert/update counts or record and rethrow failures.

#### Responsibilities
- Aggregate `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku` for records received since `@Since`.
- Update matching stock levels or insert new warehouse/SKU rows.
- Record synchronization start time, affected-row counts, and errors in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — transactionally applies aggregated supplier quantities and logs the outcome.
- `dbo.StockLevel` — stores on-hand quantities keyed by `WarehouseId` and `Sku`.
- `dbo.SupplierFeed` — supplies timestamped warehouse/SKU quantity records.
- `dbo.SyncLog` — stores per-execution start time, insert/update counts, and optional error message.

#### Data flow
- Filter feed rows on `ReceivedAt >= @Since`, then aggregate `Quantity` by warehouse and SKU.
- `MERGE` aggregates into `dbo.StockLevel`: add quantities to matched rows and insert unmatched rows.
- Capture `INSERT` and `UPDATE` actions in `@Changes`, count them, and write a success row to `dbo.SyncLog`.
- On failure, roll back an active transaction, log the error with zero counts, and rethrow it.

#### Dependencies
- SQL Server tables: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- SQL Server built-ins: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.

#### Cross-cutting
- Uses a transaction around stock updates and success logging; error handling rolls back when needed.
- Captures `MERGE` actions in a table variable to derive insert and update counts.
