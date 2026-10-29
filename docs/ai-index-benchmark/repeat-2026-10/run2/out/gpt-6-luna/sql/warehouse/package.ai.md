### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:52:20Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Applies recent supplier inventory feeds to warehouse SKU stock levels and records synchronization results or failures.

#### Purpose
- Maintain on-hand quantities per warehouse and SKU from supplier feed data.
- Track synchronization start times, row counts, and errors.

#### Responsibilities
- Aggregate supplier quantities received since a specified timestamp by warehouse and SKU.
- Insert missing stock rows or add quantities to existing rows, updating timestamps.
- Log merge counts on success; roll back and log the error on failure.

#### Key units
- `dbo.usp_SyncStock(@Since)` — applies recent feed quantities and records the synchronization outcome.
- `dbo.StockLevel` — stores on-hand quantities keyed by `(WarehouseId, Sku)`.
- `dbo.SupplierFeed` — stores incoming warehouse, SKU, quantity, and receipt-time records.
- `dbo.SyncLog` — stores synchronization start time, insert/update counts, and optional error message.

#### Data flow
- `dbo.SupplierFeed` rows with `ReceivedAt >= @Since` are grouped by `WarehouseId, Sku` and summed.
- The aggregate is merged into `dbo.StockLevel`: matching quantities are added; unmatched pairs are inserted.
- Merge action counts are written to `dbo.SyncLog`; errors trigger rollback, error logging with zero counts, and rethrow.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and `dbo.StockLevel`, and writes `dbo.StockLevel` and `dbo.SyncLog`.
- Warehouse and SKU match keys connect aggregated feed rows to stock rows; no foreign keys are declared.

#### Cross-cutting
- Uses a transaction for stock updates and success logging, with rollback and error logging on failure.
- Captures UTC timestamps for synchronization start and stock updates.
