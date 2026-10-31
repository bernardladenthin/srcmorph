### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:48:03Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Recent supplier feed quantities are applied to warehouse stock levels, with each synchronization’s outcome recorded for operational tracking.

#### Purpose
- Aggregate supplier quantities received since a supplied timestamp and update or create stock levels by warehouse and SKU.
- Record successful row counts or failure details in the synchronization log.

#### Responsibilities
- **Stock synchronization:** `dbo.usp_SyncStock` aggregates `dbo.SupplierFeed` rows and applies the totals to `dbo.StockLevel`.
- **Outcome tracking:** Record insert/update counts or an error message in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since)`: transactionally applies recent feed quantities and logs the result.
- `dbo.SupplierFeed`: stores incoming quantities by warehouse, SKU, and receipt timestamp.
- `dbo.StockLevel`: stores on-hand quantities keyed by warehouse and SKU.
- `dbo.SyncLog`: stores synchronization start time, row counts, and optional error details.
- `@Changes`: table variable capturing each `MERGE` action for count calculation.

#### Data flow
- Read `dbo.SupplierFeed` rows where `ReceivedAt >= @Since`; aggregate `Quantity` by `WarehouseId` and `Sku`.
- `MERGE` aggregates into `dbo.StockLevel`: add quantities to matching rows or insert unmatched pairs.
- Count insert and update actions from `@Changes`; write a success row to `dbo.SyncLog`.
- On error, roll back any active transaction, log `ERROR_MESSAGE()` with zero counts, and rethrow.

#### Dependencies
- SQL Server T-SQL; internal data dependencies are `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.
- Stock matching uses the `(WarehouseId, Sku)` pair; no foreign keys are declared.

#### Cross-cutting
- Stock changes and success logging run within a transaction.
- Error handling rolls back active work, records the error, and rethrows it.
