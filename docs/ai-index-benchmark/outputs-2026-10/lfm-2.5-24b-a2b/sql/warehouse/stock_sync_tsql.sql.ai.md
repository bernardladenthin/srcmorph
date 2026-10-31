### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:22:43Z
- G: 
- A: 0.0.0
- X: file
---
> Synchronizes nightly supplier feed stock quantities into warehouse stock levels and records the merge outcome in a sync log.

#### Purpose
- Merges supplier feed data into the StockLevel table for nightly warehouse stock synchronization.
- Logs the merge outcome (rows inserted, rows updated, errors) in the SyncLog table.

#### Type
- SQL object kinds: table, procedure.
- Tables: dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog.
- Procedure: dbo.usp_SyncStock.

#### Schema
- dbo.StockLevel: WarehouseId INT NOT NULL, Sku NVARCHAR(40) NOT NULL, OnHand INT NOT NULL DEFAULT 0, UpdatedAt DATETIME2(0) NOT NULL DEFAULT SYSUTCDATETIME(), PK_StockLevel PRIMARY KEY (WarehouseId, Sku).
- dbo.SupplierFeed: FeedRowId BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY, WarehouseId INT NOT NULL, Sku NVARCHAR(40) NOT NULL, Quantity INT NOT NULL, ReceivedAt DATETIME2(0) NOT NULL.
- dbo.SyncLog: SyncLogId INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY, StartedAt DATETIME2(0) NOT NULL, RowsInserted INT NOT NULL, RowsUpdated INT NOT NULL, ErrorMessage NVARCHAR(4000) NULL.

#### Reads
- Procedure usp_SyncStock reads from dbo.SupplierFeed (WarehouseId, Sku, Quantity, ReceivedAt) and dbo.StockLevel (WarehouseId, Sku, OnHand, UpdatedAt).
- SyncLog is read for inserting log entries.

#### Writes
- Procedure usp_SyncStock writes to dbo.StockLevel (OnHand, UpdatedAt) via MERGE.
- Procedure writes to dbo.SyncLog (StartedAt, RowsInserted, RowsUpdated, ErrorMessage).

#### Relationships
- Logical join: StockLevel.WarehouseId and StockLevel.Sku match SupplierFeed.WarehouseId and SupplierFeed.Sku in the MERGE operation.
- No explicit foreign key constraints defined in the schema.

#### Routine logic
- Parameters: @Since DATETIME2(0).
- Declares @Started DATETIME2(0) and @Changes table with MergeAction.
- Begins transaction; MERGE uses aggregated supplier feed rows since @Since.
- WHEN MATCHED: UPDATE OnHand = target.OnHand + source.Quantity, UpdatedAt = SYSUTCDATETIME().
- WHEN NOT MATCHED BY TARGET: INSERT (WarehouseId, Sku, OnHand) VALUES (source.WarehouseId, source.Sku, source.Quantity).
- OUTPUT $action INTO @Changes.
- INSERT INTO SyncLog counts of INSERT and UPDATE actions.
- COMMIT transaction; on error, ROLLBACK and log error message.

#### Dependencies
- References dbo.StockLevel, dbo.SupplierFeed, dbo.SyncLog.
- Uses system functions SYSUTCDATETIME, IDENTITY, ERROR_MESSAGE, THROW.
