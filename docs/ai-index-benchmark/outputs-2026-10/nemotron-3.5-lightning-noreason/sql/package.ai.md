### sql
- H: 1.0
- C: 80560CFA
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:55:09Z
- G: 
- A: 0.0.0
- X: package
- F: [library/](library/package.ai.md)
- F: [shop/](shop/package.ai.md)
- F: [warehouse/](warehouse/package.ai.md)
---
> A data synchronization layer that merges supplier nightly feeds into warehouse stock records, enforces inventory integrity, and logs every change for audit and rollback.

#### Purpose
The package provides a T-SQL-based synchronization pipeline that reads a supplier feed, merges aggregated quantities into the warehouse stock ledger, and logs insert/update counts and errors into a sync log table.

#### Responsibilities
- **Feed ingestion**: Reads the `SupplierFeed` table filtered by a `@Since` timestamp, aggregating changes by `WarehouseId` and `Sku`.
- **Upsert logic**: Executes a `MERGE` statement to match `StockLevel` on `WarehouseId` and `Sku`, updating `OnHand` and `UpdatedAt` or inserting new rows as needed.
- **Change logging**: Captures merge outcomes (`INSERT`, `UPDATE`) into a `@Changes` table variable and writes aggregated counts (rows inserted, rows updated) into `SyncLog`.
- **Atomic error handling**: Wraps the merge in a transaction; on error, rolls back and logs the error details before re-throwing.

#### Key units
- **stock_sync_tsql.sql**: Stored procedure implementing the merge logic, aggregating supplier changes and writing to `StockLevel` and `SyncLog`.
- **dbo.StockLevel**: Target table storing current on-hand quantities keyed by `WarehouseId` and `Sku`, with an `UpdatedAt` timestamp.
- **dbo.SupplierFeed**: Source feed table containing nightly supplier stock data, filtered by `@Since` to track changes.
- **dbo.SyncLog**: Audit log table recording sync run metadata, including rows inserted, rows updated, and error descriptions.
- **SYSUTCDATETIME()**: System function used to generate the `@Since` cutoff timestamp for feed filtering.

#### Data flow
- Procedure reads `dbo.SupplierFeed` rows where the feed timestamp is at or after the `@Since` parameter.
- Results are aggregated by `WarehouseId` and `Sku` to consolidate multiple feed rows per key.
- A `MERGE` operation targets `dbo.StockLevel`, matching on `WarehouseId` and `Sku`:
  - When matched, `OnHand` and `UpdatedAt` are updated with the new aggregated quantity.
  - When not matched, a new row is inserted with the aggregated quantity and current timestamp.
- The `$action` from the `MERGE` is captured into a table variable `@Changes`.
- Aggregated counts from `@Changes` (inserts vs updates) are inserted into `dbo.SyncLog` to record the sync outcome.
- On any runtime error, the transaction is rolled back and an error row is inserted into `dbo.SyncLog` before the exception is re-thrown.

#### Dependencies
- **Tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **System function**: `SYSUTCDATETIME()` for timestamp generation.
- **Stored procedure**: `stock_sync_tsql.sql` (the merge procedure itself).

#### Cross-cutting
- Uses a table variable `@Changes` to capture individual merge actions (`INSERT`, `UPDATE`) for aggregation.
- Follows an atomic transaction pattern: successful merges commit; failures trigger rollback and error logging.
- Relies on a merge join between the aggregated `SupplierFeed` and `StockLevel` on the composite key `(WarehouseId, Sku)`.
- Error handling inserts a descriptive error row into `SyncLog` before re-throwing, ensuring every sync attempt is recorded.
