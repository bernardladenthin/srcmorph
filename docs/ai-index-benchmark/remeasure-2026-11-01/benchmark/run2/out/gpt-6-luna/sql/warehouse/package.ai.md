### sql/warehouse
- H: 1.0
- C: 1013CFD7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:28Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes supplier-received quantities into warehouse on-hand stock by warehouse and SKU, while recording each run’s outcome.

#### Purpose
- Apply supplier feed quantities received since a supplied timestamp to current warehouse inventory.
- Record inserted and updated stock counts, or the error message, for each synchronization run.

#### Responsibilities
- Aggregate `dbo.SupplierFeed` quantities by warehouse and SKU, filtered by `ReceivedAt >= @Since`.
- Merge aggregates into `dbo.StockLevel`, adding to existing `OnHand` quantities or inserting new stock rows.
- Log successful counts or failure details in `dbo.SyncLog`.

#### Key units
- `dbo.usp_SyncStock(@Since DATETIME2(0))` — performs the transactional stock synchronization and logs its outcome.
- `dbo.StockLevel` — stores current on-hand inventory keyed by `WarehouseId` and `Sku`.
- `dbo.SupplierFeed` — stores received supplier quantities and timestamps.
- `dbo.SyncLog` — stores per-run start time, inserted and updated counts, and optional error message.

#### Data flow
- Read eligible `dbo.SupplierFeed` rows → group and sum quantities by `WarehouseId` and `Sku` → `MERGE` totals into `dbo.StockLevel` → count merge actions and write one `dbo.SyncLog` row.
- On failure, roll back any active transaction, log the error with zero counts, and rethrow.

#### Dependencies
- `dbo.usp_SyncStock` reads `dbo.SupplierFeed` and updates `dbo.StockLevel`; it records results in `dbo.SyncLog`.
- `dbo.StockLevel` matches feed aggregates on `WarehouseId` and `Sku`.

#### Cross-cutting
- The procedure captures a UTC start time and wraps stock updates in a transaction.
- Success and error paths each write one synchronization log row; errors are rethrown after logging.
