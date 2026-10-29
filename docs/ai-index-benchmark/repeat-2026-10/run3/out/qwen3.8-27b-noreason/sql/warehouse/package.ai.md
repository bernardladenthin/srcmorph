### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:51:12Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronisation that merges supplier feed quantities into on-hand stock levels and logs the sync outcome.

#### Purpose
- Models per-warehouse, per-SKU on-hand stock (`StockLevel`) and the raw supplier quantity feed (`SupplierFeed`).
- Provides `usp_SyncStock` to merge feed rows received since a cutoff into stock levels and record a `SyncLog` entry.

#### Responsibilities
- **Stock Management**: Maintains `dbo.StockLevel` with on-hand quantities per `WarehouseId` and `Sku`.
- **Data Ingestion**: Processes `dbo.SupplierFeed` rows to update or insert stock levels.
- **Audit & Logging**: Records sync execution details and error states in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock`: T-SQL procedure that merges aggregated supplier feed data into stock levels within a transaction.
- `dbo.StockLevel`: Table storing current on-hand inventory levels keyed by `WarehouseId` and `Sku`.
- `dbo.SupplierFeed`: Table storing raw incoming quantity data from suppliers.
- `dbo.SyncLog`: Table auditing sync operations with row counts and error messages.

#### Data flow
1. `dbo.usp_SyncStock` accepts a `@Since` datetime parameter.
2. Aggregates `dbo.SupplierFeed` rows where `ReceivedAt >= @Since` by `WarehouseId` and `Sku` (summing `Quantity`).
3. Executes a `MERGE` statement against `dbo.StockLevel`:
   - Updates existing rows by adding the aggregated quantity to `OnHand` and refreshing `UpdatedAt`.
   - Inserts new rows for SKUs not previously present.
4. Captures MERGE action counts (`$action`) into a table variable.
5. Inserts a summary row into `dbo.SyncLog` including `StartedAt`, `RowsInserted`, `RowsUpdated`, and any `ErrorMessage`.
6. Commits the transaction on success; rolls back and logs the error on failure.

#### Dependencies
- **Internal Tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **Built-in Functions**: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`.
- **No Foreign Keys**: Logical relationship between `StockLevel` and `SupplierFeed` via `WarehouseId` + `Sku` without enforced constraints.

#### Cross-cutting
- **Transaction Safety**: The sync procedure wraps all writes in a transaction with explicit error handling (`THROW` on failure).
- **Audit Trail**: Every execution, whether successful or failed, generates a `SyncLog` entry.
- **Timezone Handling**: Uses `SYSUTCDATETIME()` for consistent timestamping.
