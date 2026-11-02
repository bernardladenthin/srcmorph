### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:17:25Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Recent supplier-feed quantities are applied to warehouse stock by SKU, with each synchronization outcome recorded for operational tracking.

#### Purpose
- Maintain `dbo.StockLevel` from supplier quantities received since a specified timestamp.
- Record inserted and updated counts on success, or the error and failure outcome on exception.

#### Responsibilities
- Aggregate recent `dbo.SupplierFeed` rows by `WarehouseId` and `Sku`, summing `Quantity`.
- Merge aggregates into `dbo.StockLevel`: add quantities to matching rows or insert missing stock rows.
- Write synchronization counts or error details to `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — runs the transactional inventory update and logs its result.
- `dbo.SupplierFeed` — stores supplier quantities with warehouse, SKU, and receipt time.
- `dbo.StockLevel` — stores on-hand quantity per warehouse and SKU.
- `dbo.SyncLog` — stores synchronization start time, inserted/updated counts, and optional error message.
- `@Changes` — table variable collecting MERGE actions for counting inserts and updates.

#### Data flow
- `@Since` filters `dbo.SupplierFeed` by `ReceivedAt`; matching rows are grouped by warehouse and SKU and their quantities summed.
- The aggregated source is merged into `dbo.StockLevel`; MERGE actions are captured in `@Changes`.
- Insert and update counts are written to `dbo.SyncLog` before commit.
- On failure, the transaction is rolled back if active, a zero-count error record is logged, and the error is rethrown.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and merges into `dbo.StockLevel`; it records outcomes in `dbo.SyncLog`.
- `dbo.StockLevel` is keyed by `(WarehouseId, Sku)`; no foreign keys are declared.

#### Cross-cutting
- Stock changes and success logging run in a transaction.
- Error handling rolls back an active transaction, logs `ERROR_MESSAGE()`, and rethrows.
