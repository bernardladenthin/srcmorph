### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:33:12Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse inventory levels by aggregating supplier feed data into a consolidated stock ledger.

#### Purpose
* Maintain accurate, up-to-date stock quantities per warehouse and SKU.
* Provide auditability of synchronization operations and error tracking.

#### Responsibilities
* **Stock Synchronization**: Aggregating raw supplier feed quantities and merging them into the master stock table.
* **Audit Logging**: Recording the outcome, row counts, and potential errors of each sync execution.

#### Key units
* `dbo.usp_SyncStock`: Stored procedure that orchestrates the aggregation of `dbo.SupplierFeed` and the update of `dbo.StockLevel`.
* `dbo.StockLevel`: Table storing the current `OnHand` quantity and `UpdatedAt` timestamp per `WarehouseId` and `Sku`.
* `dbo.SupplierFeed`: Table containing raw incoming stock data (`Quantity`, `ReceivedAt`).
* `dbo.SyncLog`: Table tracking synchronization history, including `RowsInserted`, `RowsUpdated`, and `ErrorMessage`.

#### Data flow
1. **Input**: `dbo.usp_SyncStock` receives a `@Since` timestamp.
2. **Aggregation**: Records from `dbo.SupplierFeed` filtered by `ReceivedAt` are grouped by `WarehouseId` and `Sku`.
3. **Merge**: Aggregated totals are merged into `dbo.StockLevel` (updating existing records or inserting new ones).
4. **Output**: Execution metrics (counts and errors) are written to `dbo.SyncLog`.

#### Dependencies
* Internal tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.

#### Cross-cutting
* **Transaction Management**: Uses T-SQL transactions with TRY/CATCH blocks for atomicity and error logging.
* **Concurrency/Consistency**: Employs the `MERGE` statement to handle simultaneous insert/update logic.
