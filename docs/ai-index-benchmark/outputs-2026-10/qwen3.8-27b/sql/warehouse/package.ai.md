### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:47:15Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly warehouse stock synchronisation: merges supplier feed quantities into per-warehouse SKU stock levels and logs sync outcomes.

#### Purpose
- Defines the warehouse stock-level data model and the stored procedure that applies incremental supplier feed updates to it.
- Provides auditability via a sync log capturing row counts and error messages per run.

#### Responsibilities
- **Stock state** — `dbo.StockLevel` holds current on-hand quantity per `(WarehouseId, Sku)`.
- **Inbound feed** — `dbo.SupplierFeed` is the append-only supplier quantity feed consumed by the sync.
- **Sync engine** — `dbo.usp_SyncStock` performs the MERGE (increment or insert) and records a `dbo.SyncLog` entry.

#### Key units
- `dbo.StockLevel` — per-warehouse, per-SKU on-hand stock; PK `(WarehouseId, Sku)`.
- `dbo.SupplierFeed` — append-only supplier quantity rows; identity PK `FeedRowId`.
- `dbo.SyncLog` — per-run audit record (started-at, rows inserted/updated, error message).
- `dbo.usp_SyncStock` — parameterised procedure (`@Since DATETIME2(0)`) that MERGEs aggregated feed rows into `StockLevel` and logs the outcome.

#### Data flow
`SupplierFeed` (filtered by `ReceivedAt >= @Since`, grouped by `WarehouseId, Sku`, `SUM(Quantity)`) → MERGE into `StockLevel` (UPDATE `OnHand`/`UpdatedAt` or INSERT) → action counts captured into table variable → INSERT into `SyncLog`.

#### Dependencies
- Internal: all three tables are defined and consumed within the same script.
- No external schema or module references.

#### Cross-cutting
- **Transaction safety** — the procedure wraps the MERGE + log in a single transaction; on error it rolls back, logs `ErrorMessage`, and re-throws.
- **No FK constraints** — `StockLevel` ↔ `SupplierFeed` linkage is logical only (`WarehouseId`, `Sku`).
- **Idempotent by design** — re-running with the same `@Since` would double-count; callers are expected to advance the watermark.
