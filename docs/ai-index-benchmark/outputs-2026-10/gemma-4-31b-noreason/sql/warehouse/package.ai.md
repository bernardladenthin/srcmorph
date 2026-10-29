### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:56:05Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse inventory levels by aggregating supplier feed data into a consolidated stock state.

#### Purpose
* Maintain accurate, per-warehouse and per-SKU stock totals.
* Process incremental supplier updates into a persistent inventory state.

#### Responsibilities
* **Inventory Management**: Aggregating quantities from supplier feeds and updating current stock levels.
* **Audit & Logging**: Tracking synchronization execution metrics and capturing errors.

#### Key units
* `dbo.StockLevel`: Table storing the current `OnHand` quantity and last update timestamp per warehouse and SKU.
* `dbo.SupplierFeed`: Table acting as the landing zone for incoming stock quantity updates.
* `dbo.SyncLog`: Table recording the results (rows inserted/updated) and errors of each sync run.
* `dbo.usp_SyncStock`: Stored procedure that aggregates feed data since a specific date and merges it into the stock levels.

#### Data flow
`dbo.SupplierFeed` (filtered by date) $\rightarrow$ Aggregation by Warehouse/SKU $\rightarrow$ `MERGE` into `dbo.StockLevel` $\rightarrow$ Summary metrics written to `dbo.SyncLog`.

#### Dependencies
* Internal dependencies between `dbo.usp_SyncStock` and the tables `dbo.StockLevel`, `dbo.SupplierFeed`, and `dbo.SyncLog`.

#### Cross-cutting
* **Transaction Management**: Uses T-SQL transactions and TRY/CATCH blocks to ensure atomicity and log `ERROR_MESSAGE()` on failure.
* **State Tracking**: Uses `DATETIME2(0)` for precise synchronization windowing and update timestamps.
