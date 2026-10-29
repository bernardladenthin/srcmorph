### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:49:04Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly supplier feed changes are merged into warehouse stock balances and recorded in a transaction audit trail for stock synchronization.

#### Purpose
- Maintain warehouse stock levels by applying supplier feed delta changes since a cutoff time to the live `dbo.StockLevel` table.
- Record synchronization outcomes and failures in `dbo.SyncLog` to support operational auditing of nightly stock updates.

#### Responsibilities
- Warehouse stock state management: `dbo.StockLevel` stores current on-hand quantities per warehouse and SKU.
- Supplier feed intake: `dbo.SupplierFeed` accumulates incoming quantity updates with timestamps.
- Stock synchronization routine: `dbo.usp_SyncStock` aggregates feed rows, merges them into stock, and logs insert/update totals.
- Error and audit handling: `dbo.SyncLog` captures successful row counts and caught exceptions with rollback semantics.

#### Key units
- `dbo.usp_SyncStock` — nightly stock synchronization stored procedure that filters supplier feed rows by `@Since`, merges deltas into `dbo.StockLevel`, and logs outcomes.
- `dbo.StockLevel` — current warehouse stock table keyed by `(WarehouseId, Sku)` with `OnHand` and `UpdatedAt`.
- `dbo.SupplierFeed` — supplier event feed table storing `WarehouseId`, `Sku`, `Quantity`, and `ReceivedAt` for each feed record.
- `dbo.SyncLog` — sync audit table recording `StartedAt`, `RowsInserted`, `RowsUpdated`, and optional `ErrorMessage`.
- `@Changes` — table variable capturing `MERGE` action results (`INSERT` or `UPDATE`) for audit aggregation.
- `@Since` — cutoff timestamp parameter limiting supplier changes to the relevant interval.

#### Data flow
- Input: `dbo.SupplierFeed` rows with `ReceivedAt >= @Since` are aggregated by `WarehouseId` and `Sku` with `SUM(Quantity)`.
- Join: aggregated source rows are matched to `dbo.StockLevel` by `WarehouseId` and `Sku`.
- Merge: `MERGE` updates existing stock by adding source quantity to `OnHand` and refreshes `UpdatedAt`; inserts new warehouse/SKU rows when absent.
- Capture: `OUTPUT $action INTO @Changes` tracks whether each merged row was an insert or update.
- Audit: `dbo.SyncLog` records total inserted and updated row counts for the run using `@Started` and `@Changes`.
- Failure path: `BEGIN CATCH` rolls back the transaction, writes an error row to `dbo.SyncLog`, and rethrows.

#### Dependencies
- Internal:
  - `dbo.StockLevel`
  - `dbo.SupplierFeed`
  - `dbo.SyncLog`
  - `@Changes`
  - `@Since`
  - transaction control with `BEGIN TRANSACTION`, `COMMIT TRANSACTION`, `ROLLBACK`
- External / runtime:
  - SQL Server T-SQL engine functions `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, and `THROW`
  - `MERGE`/`OUTPUT $action` behavior in SQL Server

#### Cross-cutting
- Transactional batch pattern: `dbo.usp_SyncStock` wraps the stock merge in a transaction and ensures rollback on failure.
- Audit-first reconciliation: every sync run records start time and row totals, with error entries logged in `dbo.SyncLog`.
- Shared warehouse identity model: all stock and feed data use `WarehouseId` + `Sku` as the natural merge key.
- Defaulting pattern: `dbo.StockLevel.OnHand` defaults to `0` and `UpdatedAt` defaults to `SYSUTCDATETIME()`.
- Minimal indexes: each table uses a primary key only; there are no explicit foreign keys or advanced indexes in the provided schema.
