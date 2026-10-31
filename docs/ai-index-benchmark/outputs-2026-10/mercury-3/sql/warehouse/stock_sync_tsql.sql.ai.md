### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:32:58Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly warehouse stock synchronisation: merges supplier feed quantities into StockLevel and logs sync outcomes in T-SQL.

#### Purpose
- Models current warehouse stock levels and a supplier feed of incoming quantities.
- Stored procedure merges feed rows since a cutoff into StockLevel and records the result in SyncLog.

#### Type
- Tables + stored procedure.
- Dialect: T-SQL (SQL Server).

#### Schema
- `dbo.StockLevel`:
  - `WarehouseId INT NOT NULL` — part of PK `PK_StockLevel`.
  - `Sku NVARCHAR(40) NOT NULL` — part of PK `PK_StockLevel`.
  - `OnHand INT NOT NULL` — default `DF_StockLevel_OnHand` `(0)`.
  - `UpdatedAt DATETIME2(0) NOT NULL` — default `DF_StockLevel_UpdatedAt` `SYSUTCDATETIME()`.
  - PK index: `PK_StockLevel (WarehouseId, Sku)`.
- `dbo.SupplierFeed`:
  - `FeedRowId BIGINT IDENTITY(1,1) NOT NULL` — PK `PK_SupplierFeed`.
  - `WarehouseId INT NOT NULL`.
  - `Sku NVARCHAR(40) NOT NULL`.
  - `Quantity INT NOT NULL`.
  - `ReceivedAt DATETIME2(0) NOT NULL`.
  - PK index: `PK_SupplierFeed (FeedRowId)`.
- `dbo.SyncLog`:
  - `SyncLogId INT IDENTITY(1,1) NOT NULL` — PK `PK_SyncLog`.
  - `StartedAt DATETIME2(0) NOT NULL`.
  - `RowsInserted INT NOT NULL`.
  - `RowsUpdated INT NOT NULL`.
  - `ErrorMessage NVARCHAR(4000) NULL`.
  - PK index: `PK_SyncLog (SyncLogId)`.

#### Reads
- `dbo.usp_SyncStock` READS from `dbo.SupplierFeed` (`WarehouseId`, `Sku`, `SUM(Quantity)`, `ReceivedAt`).
- Reads `$action` OUTPUT from the MERGE into table variable `@Changes` (`MergeAction NVARCHAR(10)`).

#### Writes
- `dbo.usp_SyncStock` WRITES `dbo.StockLevel`: on match, `UPDATE` `OnHand`, `UpdatedAt`; when not matched, `INSERT` `WarehouseId`, `Sku`, `OnHand`.
- Writes `dbo.SyncLog` via `INSERT` (`StartedAt`, `RowsInserted`, `RowsUpdated`; plus `ErrorMessage` in catch).

#### Relationships
- Join/merge relationship: `dbo.StockLevel.WarehouseId = dbo.SupplierFeed.WarehouseId`.
- Join/merge relationship: `dbo.StockLevel.Sku = dbo.SupplierFeed.Sku`.
- No explicit foreign key constraints defined.

#### Routine logic
- `dbo.usp_SyncStock @Since DATETIME2(0)`:
  - Captures `@Started = SYSUTCDATETIME()`.
  - `MERGE dbo.StockLevel` using grouped `dbo.SupplierFeed` rows where `ReceivedAt >= @Since`.
  - On match: `OnHand += source.Quantity`, `UpdatedAt = SYSUTCDATETIME()`.
  - No matching target: inserts `WarehouseId`, `Sku`, `Quantity`.
  - OUTPUTs `$action` into `@Changes`.
  - Inserts one `dbo.SyncLog` row with counts of 'INSERT' and 'UPDATE' actions.
  - Commits transaction.
  - Catch: rollback on active transaction, log error row with 0/0 and `ERROR_MESSAGE()`, then `THROW`.

#### Dependencies
- `dbo.StockLevel`.
- `dbo.SupplierFeed`.
- `dbo.SyncLog`.
- `SYSUTCDATETIME()`.
- `ERROR_MESSAGE()`.
