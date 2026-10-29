### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:54:11Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> This package manages the automated synchronization of current inventory levels from external supplier data into the master warehouse stock records.

#### Purpose
* Executes nightly stock synchronization from supplier feed data into the StockLevel table.
* Logs the results (inserts/updates) and any errors of the synchronization process in SyncLog.

#### Responsibilities
* Stock Level Management: Merging and updating `dbo.StockLevel` records based on incoming supplier data.
* Auditing and Logging: Recording the outcome of the synchronization process in `dbo.SyncLog`.

#### Key units
* `usp_SyncStock`: T-SQL procedure responsible for executing the stock synchronization logic.
* `dbo.StockLevel`: Master table holding current inventory levels per warehouse and SKU.
* `dbo.SupplierFeed`: Source table containing recent stock updates from suppliers.
* `dbo.SyncLog`: Audit table tracking the execution details and success/failure of sync operations.

#### Data flow
Supplier data from `dbo.SupplierFeed` is read, aggregated based on a time parameter (`@Since`), and then merged into `dbo.StockLevel` using a `MERGE` operation. The resulting counts of inserted and updated rows are then written to `dbo.SyncLog`.

#### Dependencies
* Relies on the existence and structure of `dbo.SupplierFeed`, `dbo.StockLevel`, and `dbo.SyncLog`.

#### Cross-cutting
* Transaction Management: The `usp_SyncStock` procedure encapsulates the entire operation within a transaction, ensuring atomicity, with rollback on failure.
* Data Integrity: Uses composite primary keys (`WarehouseId`, `Sku`) in `dbo.StockLevel` to ensure accurate merging logic.
