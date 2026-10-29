### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:17:54Z
- G: 
- A: 0.0.0
- X: file
---
> Nightly stock synchronization from a supplier feed.

#### Purpose
- Synchronizes stock levels in the warehouse with data from a supplier feed.
- Logs the synchronization process, including rows inserted and updated.

#### Type
Procedure

#### Schema
- **StockLevel**
  - WarehouseId INT NOT NULL
  - Sku NVARCHAR(40) NOT NULL
  - OnHand INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0)
  - UpdatedAt DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME())
  - CONSTRAINT PK_StockLevel PRIMARY KEY (WarehouseId, Sku)
- **SupplierFeed**
  - FeedRowId BIGINT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SupplierFeed PRIMARY KEY
  - WarehouseId INT NOT NULL
  - Sku NVARCHAR(40) NOT NULL
  - Quantity INT NOT NULL
  - ReceivedAt DATETIME2(0) NOT NULL
- **SyncLog**
  - SyncLogId INT IDENTITY(1, 1) NOT NULL CONSTRAINT PK_SyncLog PRIMARY KEY
  - StartedAt DATETIME2(0) NOT NULL
  - RowsInserted INT NOT NULL
  - RowsUpdated INT NOT NULL
  - ErrorMessage NVARCHAR(4000) NULL

#### Reads
- **SupplierFeed**.WarehouseId, Sku, Quantity, ReceivedAt

#### Writes
- **StockLevel**.OnHand, UpdatedAt
- **SyncLog**.StartedAt, RowsInserted, RowsUpdated, ErrorMessage

#### Relationships
- **StockLevel**.WarehouseId -> **SupplierFeed**.WarehouseId
- **StockLevel**.Sku -> **SupplierFeed**.Sku

#### Routine logic
- Parameters: @Since DATETIME2(0)
- Steps:
  - Starts a transaction.
  - Merges data from **SupplierFeed** into **StockLevel**.
  - Logs the number of rows inserted and updated.
  - Rolls back the transaction if an error occurs.
  - Logs any error message.

#### Dependencies
- **StockLevel**
- **SupplierFeed**
- **SyncLog**
