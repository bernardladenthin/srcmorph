### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T10:01:21Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronisation that merges supplier feed quantities into per-warehouse SKU stock levels and logs the outcome.

#### Purpose
- Implements a T-SQL data warehouse routine for updating inventory levels based on supplier data feeds.
- Provides schema definitions for stock levels, raw feed data, and synchronization audit logs.

#### Responsibilities
- **Stock Level Management**: Maintains current on-hand quantities per `WarehouseId` and `Sku` in `dbo.StockLevel`.
- **Feed Ingestion**: Stores raw supplier quantity updates in `dbo.SupplierFeed` with timestamps.
- **Synchronization Logic**: Executes `dbo.usp_SyncStock` to merge aggregated feed data into stock levels.
- **Audit Logging**: Records success metrics (inserts/updates) or error details in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock`: Stored procedure that performs a `MERGE` operation to update or insert stock levels based on feed data since a specified timestamp.
- `dbo.StockLevel`: Table storing current inventory counts keyed by `WarehouseId` and `Sku`.
- `dbo.SupplierFeed`: Table storing incoming supplier quantity records with `ReceivedAt` timestamps.
- `dbo.SyncLog`: Table tracking synchronization execution results, including row counts and error messages.

#### Data flow
1. `dbo.usp_SyncStock` accepts a `@Since` parameter to filter `dbo.SupplierFeed` rows.
2. Feed rows are aggregated by `WarehouseId` and `Sku` using `SUM(Quantity)`.
3. A `MERGE` statement updates existing `dbo.StockLevel` rows (incrementing `OnHand`) or inserts new ones.
4. Transaction outcome (row counts or error message) is written to `dbo.SyncLog`.
5. Transaction is committed or rolled back based on success/failure.

#### Dependencies
- **Internal Tables**: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- **SQL Server Built-ins**: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`, `@@TRANCOUNT`.
- **Dialect**: T-SQL (SQL Server).

#### Cross-cutting
- **Transaction Safety**: All writes are wrapped in an explicit transaction with rollback on error.
- **Error Handling**: Catches exceptions, logs `ERROR_MESSAGE()` to `dbo.SyncLog`, and re-throws via `THROW`.
- **Timestamping**: Uses `SYSUTCDATETIME()` for consistent UTC-based `UpdatedAt` and `StartedAt` values.
- **Logical Relationships**: No explicit foreign keys; relies on logical join keys (`WarehouseId`, `Sku`) between `StockLevel` and `SupplierFeed`.
