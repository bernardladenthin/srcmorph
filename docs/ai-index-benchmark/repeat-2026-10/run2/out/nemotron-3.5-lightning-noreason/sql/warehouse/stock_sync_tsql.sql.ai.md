### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:18Z
- G: 
- A: 0.0.0
- X: file
---
> Warehouse stock synchronisation from a supplier feed: merges new/updated feed rows into the stock level table and logs the operation.

#### Purpose
- Merges supplier feed rows received since a given timestamp into the StockLevel table.
- Logs the number of rows inserted and updated, and captures any errors into SyncLog.

#### Type
Procedure (T-SQL)

#### Schema
- **StockLevel**: `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `OnHand INT NOT NULL DF_StockLevel_OnHand DEFAULT (0)`, `UpdatedAt DATETIME2(0) NOT NULL DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())`, PK `PK_StockLevel` on `(WarehouseId, Sku)`, index on `(WarehouseId, Sku)` (implied by PK).
- **SupplierFeed**: `FeedRowId BIGINT NOT NULL PK_SupplierFeed`, `WarehouseId INT NOT NULL`, `Sku NVARCHAR(40) NOT NULL`, `Quantity INT NOT NULL`, `ReceivedAt DATETIME2(0) NOT NULL`, PK `PK_SupplierFeed` on `FeedRowId`.
- **SyncLog**: `SyncLogId INT NOT NULL PK_SyncLog`, `StartedAt DATETIME2(0) NOT NULL`, `RowsInserted INT NOT NULL`, `RowsUpdated INT NOT NULL`, `ErrorMessage NVARCHAR(4000) NULL`, PK `PK_SyncLog` on `SyncLogId`.

#### Reads
- `dbo.SupplierFeed.WarehouseId`, `dbo.SupplierFeed.Sku`, `dbo.SupplierFeed.Quantity`, `dbo.SupplierFeed.ReceivedAt` (filtered by `@Since`, grouped).
- `dbo.StockLevel.OnHand`, `dbo.StockLevel.WarehouseId`, `dbo.StockLevel.Sku` (merge target columns).

#### Writes
- `dbo.StockLevel`: UPDATE `OnHand` + `UpdatedAt` when matched; INSERT `WarehouseId`, `Sku`, `OnHand` when not matched.
- `dbo.SyncLog`: INSERT `StartedAt`, `RowsInserted`, `RowsUpdated`, `ErrorMessage` (on success or catch).

#### Relationships
- `StockLevel.WarehouseId` → `SupplierFeed.WarehouseId` (join key in MERGE).
- `StockLevel.Sku` → `SupplierFeed.Sku` (join key in MERGE).

#### Routine logic
- Declare local variable `@Started` with current UTC timestamp.
- Declare table variable `@Changes` to capture merge actions.
- Begin try block, start transaction.
- MERGE `StockLevel` with aggregated `SupplierFeed` (WHERE `ReceivedAt` >= `@Since`, GROUP BY `WarehouseId`, `Sku`).
- WHEN MATCHED: UPDATE `OnHand` = `OnHand` + `Quantity`, `UpdatedAt` = current UTC.
- WHEN NOT MATCHED BY TARGET: INSERT `WarehouseId`, `Sku`, `OnHand` from source quantity.
- OUTPUT `$action` into `@Changes`.
- INSERT into `SyncLog` the count of `INSERT` and `UPDATE` actions from `@Changes`.
- COMMIT transaction.
- On error: ROLLBACK transaction, INSERT into `SyncLog` with error message, THROW.

#### Dependencies
- `dbo.StockLevel`
- `dbo.SupplierFeed`
- `dbo.SyncLog`
- `SYSUTCDATETIME()` (system function)
