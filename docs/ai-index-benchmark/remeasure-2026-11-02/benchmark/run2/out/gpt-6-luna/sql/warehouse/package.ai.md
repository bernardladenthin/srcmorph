### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:46:37Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies newly received supplier quantities to warehouse stock and records the synchronization result.

#### Purpose
- Synchronize `dbo.StockLevel` from supplier feed quantities received since a specified time.
- Record inserted and updated row counts, or the synchronization error, in `dbo.SyncLog`.

#### Responsibilities
- Aggregate `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`, then update matching stock or insert new stock records.
- Track synchronization outcomes and roll back and log failures.

#### Key units
- `dbo.usp_SyncStock(@Since)` — synchronizes eligible supplier quantities and logs the outcome.
- `dbo.StockLevel` — stores on-hand inventory keyed by warehouse and SKU.
- `dbo.SupplierFeed` — stores received supplier quantities and timestamps.
- `dbo.SyncLog` — stores sync start time, row counts, and optional error message.

#### Data flow
- Filter `dbo.SupplierFeed` by `ReceivedAt >= @Since`; group by warehouse and SKU and sum quantities.
- `MERGE` aggregates into `dbo.StockLevel`: add quantities and refresh `UpdatedAt` for matches, or insert new rows.
- Aggregate `MERGE` actions into inserted and updated counts; write a success log and commit.
- On failure, roll back an active transaction, write an error log with zero counts, and rethrow.

#### Dependencies
- `dbo.usp_SyncStock` reads from `dbo.SupplierFeed` and matches against `dbo.StockLevel`; it writes to `dbo.StockLevel` and `dbo.SyncLog`.

#### Cross-cutting
- Uses a transaction for stock synchronization, with rollback and error logging on failure.
- Captures UTC start time and uses `SYSUTCDATETIME()` as the `StockLevel.UpdatedAt` default.
