### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:06:55Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronizes warehouse inventory levels by merging supplier feed quantities into the StockLevel table and logging operation metrics.

#### Purpose
*   Merges incoming supplier feed quantities into `dbo.StockLevel` based on `WarehouseId` and `Sku` since a specified timestamp.
*   Records insertion and update counts in `dbo.SyncLog` upon success or failure.

#### Responsibilities
*   Aggregates supplier feed data to calculate total quantities per warehouse and SKU.
*   Executes `MERGE` operations to update `OnHand` and `UpdatedAt` in `dbo.StockLevel`.
*   Inserts new stock records for SKUs not previously present in the warehouse.
*   Logs transaction metrics (rows inserted, updated, errors) into `dbo.SyncLog`.

#### Key units
*   `stock_sync_tsql.sql`: T-SQL procedure handling the synchronization logic.
*   `dbo.StockLevel`: Target table for inventory levels (`WarehouseId`, `Sku`, `OnHand`, `UpdatedAt`).
*   `dbo.SupplierFeed`: Source table for incoming quantities (`WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`).
*   `dbo.SyncLog`: Logging table for operation metrics (`RowsInserted`, `RowsUpdated`, `ErrorMessage`).

#### Data flow
*   Input: `@Since` timestamp parameter filters records from `dbo.SupplierFeed`.
*   Processing: Aggregates `dbo.SupplierFeed` quantities by `WarehouseId` and `Sku`.
*   Transformation: `MERGE` statement applies aggregated data to `dbo.StockLevel`, updating `OnHand` and `UpdatedAt`.
*   Output: Success logs counts to `dbo.SyncLog`; failures trigger rollback and error logging.

#### Dependencies
*   Internal: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
*   External: T-SQL `MERGE` syntax, transaction management.

#### Cross-cutting
*   Transactional integrity: All operations wrapped in a single transaction with rollback on error.
*   Error handling: Exceptions are caught, rolled back, and detailed error messages logged to `dbo.SyncLog`.
*   Schema: Relies on `dbo.SupplierFeed.WarehouseId` and `dbo.SupplierFeed.Sku` matching `dbo.StockLevel` keys.
