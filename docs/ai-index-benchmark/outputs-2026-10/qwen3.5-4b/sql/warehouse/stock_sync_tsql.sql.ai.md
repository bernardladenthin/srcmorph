### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:04:46Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes warehouse inventory levels from a supplier feed by merging incoming quantities into the StockLevel table and logging operation metrics.

#### Purpose
*   Merges supplier feed quantities into `dbo.StockLevel` based on `WarehouseId` and `Sku` since a given timestamp.
*   Records insertion and update counts in `dbo.SyncLog` upon success or failure.

#### Type
Procedure (T-SQL)

#### Schema
N/A

#### Reads
*   `dbo.SupplierFeed` (columns: `WarehouseId`, `Sku`, `Quantity`, `ReceivedAt`)
*   `dbo.StockLevel` (columns: `WarehouseId`, `Sku`, `OnHand`)

#### Writes
*   `dbo.StockLevel` (columns: `OnHand`, `UpdatedAt` via MERGE)
*   `dbo.SyncLog` (columns: `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage`)

#### Relationships
*   `dbo.SupplierFeed.WarehouseId` -> `dbo.StockLevel.WarehouseId`
*   `dbo.SupplierFeed.Sku` -> `dbo.StockLevel.Sku`

#### Routine logic
*   Accepts `@Since` parameter to filter supplier feed records.
*   Executes `MERGE` on `dbo.StockLevel` using aggregated `dbo.SupplierFeed` data.
*   Updates `OnHand` and `UpdatedAt` for matching records; inserts new rows for unmatched records.
*   Logs `RowsInserted` and `RowsUpdated` counts into `dbo.SyncLog`.
*   Rolls back transaction and logs error details if an exception occurs.

#### Dependencies
*   `dbo.StockLevel`
*   `dbo.SupplierFeed`
*   `dbo.SyncLog`
