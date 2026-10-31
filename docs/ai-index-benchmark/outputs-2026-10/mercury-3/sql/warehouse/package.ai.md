### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:42Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronisation: merges supplier feed quantities into StockLevel and logs sync outcomes in T-SQL.

#### Purpose
- Models current warehouse stock levels (StockLevel) against a supplier feed of incoming quantities (SupplierFeed).
- Stored procedure merges feed rows since a cutoff into StockLevel and records the result in SyncLog.

#### Responsibilities
- Table schema for current stock (StockLevel), incoming supplier data (SupplierFeed), and sync audit trail (SyncLog).
- `dbo.usp_SyncStock` merge-and-log routine with transaction handling and error capture.

#### Key units
- `dbo.StockLevel` — per-warehouse/SKU on-hand quantity with `UpdatedAt`; composite PK `(WarehouseId, Sku)`.
- `dbo.SupplierFeed` — identity-keyed incoming supplier rows (`FeedRowId`) with `Quantity` and `ReceivedAt`.
- `dbo.SyncLog` — audit record per run: `StartedAt`, `RowsInserted`, `RowsUpdated`, nullable `ErrorMessage`.
- `dbo.usp_SyncStock @Since` — merges grouped feed rows where `ReceivedAt >= @Since` into StockLevel, then writes a SyncLog row.

#### Data flow
- `SupplierFeed` rows are grouped by `WarehouseId`/`Sku` (SUM of `Quantity`) and merged into `StockLevel`.
- MERGE match: `OnHand += source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`; no match: insert new `WarehouseId`/`Sku`/`Quantity`.
- `$action` OUTPUT from MERGE is captured into `@Changes`; INSERT/UPDATE counts feed one `SyncLog` row.
- Catch path: rollback active transaction, log a 0/0 `SyncLog` row with `ERROR_MESSAGE()`, then rethrow.

#### Dependencies
- Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- Built-in functions: `SYSUTCDATETIME()`, `ERROR_MESSAGE()`.
- No explicit foreign key constraints between tables.

#### Cross-cutting
- Transaction discipline: commit on success, rollback + logged failure on error.
- UTC timestamps throughout via `SYSUTCDATETIME()` (`UpdatedAt`, feed timing, `StartedAt`).
- Identity columns for PKs on `SupplierFeed` and `SyncLog`; composite PK on `StockLevel`.
- Constraint naming pattern: `PK_*` and `DF_*`.
