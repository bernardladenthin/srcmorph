### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:01:19Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> This package manages the automated synchronization of inventory levels from external supplier feeds into the master stock records.

#### Purpose
* Executes nightly stock synchronization from a supplier feed into the StockLevel table.
* Logs the outcome (inserts/updates) of the stock synchronization process in SyncLog.

#### Responsibilities
* Stock Level Management: Merging and updating `dbo.StockLevel` based on incoming supplier data.
* Auditing/Logging: Recording the success or failure metrics of the synchronization process in `dbo.SyncLog`.

#### Key units
* `usp_SyncStock`: The T-SQL procedure responsible for orchestrating the stock synchronization logic.
* `dbo.StockLevel`: Master table tracking inventory levels per warehouse and SKU.
* `dbo.SupplierFeed`: Source table containing raw, recent stock data from suppliers.
* `dbo.SyncLog`: Audit table recording synchronization run metrics.

#### Data flow
Supplier data from `dbo.SupplierFeed` (filtered by `ReceivedAt`) is aggregated and fed into a `MERGE` operation targeting `dbo.StockLevel`. The resulting changes (inserts/updates) are then summarized and written to `dbo.SyncLog`.

#### Dependencies
* Relies on `dbo.SupplierFeed` for input data.
* Modifies `dbo.StockLevel` as the primary target for inventory updates.
* Writes operational metadata to `dbo.SyncLog`.

#### Cross-cutting
* Transaction management is used to ensure atomicity for the entire synchronization process.
* The procedure accepts a `@Since DATETIME2(0)` parameter to scope the synchronization window.
