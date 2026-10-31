### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T11:20:43Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> The package implements a nightly SQL Server stored procedure that merges supplier feed quantities into a stock level table and logs synchronization metrics and errors.

#### Purpose
- Nightly synchronization of supplier feed data into warehouse stock levels.
- Logging of row counts (inserted/updated) and error messages for auditability.

#### Responsibilities
- Data ingestion from `SupplierFeed` table filtered by timestamp.
- Stock level updates/inserts via MERGE operation on `StockLevel`.
- Transaction management with rollback on failure.
- Logging of sync results and errors in `SyncLog`.

#### Key units
- `dbo.usp_SyncStock`: stored procedure taking `@Since DATETIME2(0)` parameter; orchestrates merge, logging, and transaction handling.
- `dbo.StockLevel`: target table storing `WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`; primary key `(WarehouseId, Sku)`.
- `dbo.SupplierFeed`: source table with `FeedRowId`, `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`.
- `dbo.SyncLog`: audit table storing `SyncLogId`, `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`.
- `@Changes` (local table variable): captures `$action` output from MERGE to count inserts/updates.

#### Data flow
- Input: `@Since DATETIME2(0)` parameter defines the cutoff timestamp for supplier feed rows.
- Reads `SupplierFeed` rows where `ReceivedAt >= @Since`, groups by `WarehouseId` and `Sku`, sums `Quantity`.
- MERGE targets `StockLevel`; matches on `(WarehouseId, Sku)` to update `OnHand` and `UpdatedAt`, inserts new rows for unmatched SKUs.
- Outputs row counts (inserts/updates) via `@Changes` and inserts a log record in `SyncLog` with `StartedAt`, counts, and empty `ErrorMessage` on success.
- On error (e.g., constraint violation, runtime exception), rolls back transaction, inserts `SyncLog` row with `ErrorMessage`, re-throws error.

#### Dependencies
- Internal: `StockLevel`, `SupplierFeed`, `SyncLog` tables; local table `@Changes`.
- System: `SYSUTCDATETIME()` for default `UpdatedAt`; `ERROR_MESSAGE()` for error capture.
- No external packages or modules referenced.

#### Cross-cutting
- Uses a single transaction for data modification and logging to ensure consistency.
- MERGE pattern with `$action` output to track inserts vs updates.
- Timestamp-based filtering via `@Since` and `ReceivedAt` for incremental sync.
- Error handling rolls back transaction, logs error message, and re-throws exception.
- All tables use explicit primary keys (`IDENTITY` for log IDs, composite key for `StockLevel`).

> *(Note: Only the provided file summary for `stock_sync_tsql.sql` is used; no other files are present in the input.)*
