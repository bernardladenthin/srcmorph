### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T05:22:30Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Synchronises nightly supplier stock feeds into the `StockLevel` table, logging the import in `SyncLog`.

#### Purpose
- Performs a nightly bulk import of supplier stock quantities.
- Updates existing inventory levels and creates new SKUs as needed.
- Records the outcome of each run for monitoring and auditing.

#### Responsibilities
- **Data Ingestion**: Reads rows from `SupplierFeed` that have a `ReceivedAt` timestamp within the current sync window.
- **Inventory Update**: Merges those rows into `StockLevel`, applying additive updates to `OnHand` and refreshing `UpdatedAt`.
- **Audit Trail**: Inserts a record into `SyncLog` capturing the number of rows inserted and updated, and any error that occurred.

#### Key units
- **`dbo.SupplierFeed`**: Staging table that receives raw stock data from the supplier feed.
- **`dbo.StockLevel`**: Canonical source of truth for warehouse‑level inventory quantities.
- **`dbo.SyncLog`**: Log of each sync execution, storing start time, counts of changes, and error details.
- **`dbo.usp_SyncStock`**: Stored procedure that orchestrates the entire sync process (transaction, merge, logging).

#### Data flow
1. **Input** – `SupplierFeed` rows filtered by `ReceivedAt >= @Since`.
2. **Aggregation** – Grouped by `WarehouseId` + `Sku` to sum `Quantity`.
3. **Merge** – `MERGE` into `StockLevel`:
   - **Matched** rows: `OnHand += Quantity`, `UpdatedAt = SYSDATETIME()`.
   - **Not Matched** rows: insert new inventory record.
4. **Logging** – Count of `INSERT` and `UPDATE` actions from the merge is written to `SyncLog`.
5. **Output** – `SyncLog` entry with start time, row counts, and any error message.

#### Dependencies
- **Tables**: `dbo.SupplierFeed`, `dbo.StockLevel`, `dbo.SyncLog`.
- **Procedure**: `dbo.usp_SyncStock` (the entry point for the nightly job).
