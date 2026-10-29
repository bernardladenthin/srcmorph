### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T02:31:01Z
- G: 
- A: 0.0.0
- X: file
---
> Warehouse: nightly stock synchronisation from a supplier feed (SQL Server, T-SQL).

#### Purpose
- Synchronize stock levels from a supplier feed into the warehouse database.
- Log the outcome of the synchronization process.

#### Type
SQL procedure.

#### Schema
**dbo.StockLevel**
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `OnHand` INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)
- `UpdatedAt` DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())
- PRIMARY KEY (WarehouseId, Sku)

**dbo.SupplierFeed**
- `FeedRowId` BIGINT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY
- `WarehouseId` INT NOT NULL
- `Sku` NVARCHAR(40) NOT NULL
- `Quantity` INT NOT NULL
- `ReceivedAt` DATETIME2(0) NOT NULL

**dbo.SyncLog**
- `SyncLogId` INT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY
- `StartedAt` DATETIME2(0) NOT NULL
- `RowsInserted` INT NOT NULL
- `RowsUpdated` INT NOT NULL
- `ErrorMessage` NVARCHAR(4000) NULL

#### Reads
- Reads from `dbo.SupplierFeed` to get the supplier feed received since @Since.
- Groups by `WarehouseId` and `Sku` to summarize the quantities.

#### Writes
- Writes to `dbo.StockLevel` to update the stock levels.
- Writes to `dbo.SyncLog` to log the synchronization results.

#### Relationships
- `dbo.StockLevel.WarehouseId` and `dbo.StockLevel.Sku` are primary keys.
- `dbo.SupplierFeed.WarehouseId` and `dbo.SupplierFeed.Sku` are foreign keys.

#### Routine logic
- **Parameters**: `@Since` DATETIME2(0)
- **Steps**:
  1. Start a transaction.
  2. Merge the supplier feed into `dbo.StockLevel`.
  3. Insert the results into `dbo.SyncLog`.
  4. Commit the transaction if no errors occur.
  5. Handle exceptions by rolling back the transaction and logging the error.

#### Dependencies
- References `dbo.SupplierFeed` for data synchronization.
- Uses `dbo.SyncLog` to log synchronization outcomes.
