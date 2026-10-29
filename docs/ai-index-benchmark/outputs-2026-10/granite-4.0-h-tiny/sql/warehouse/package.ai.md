### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:10:35Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> A SQL Server T‑SQL package that merges supplier feed data into inventory tables and logs sync outcomes.

#### Purpose
- Merges supplier feed data into the `StockLevel` inventory table and records sync outcomes.

#### Responsibilities
- **SQL Server (T‑SQL)**
  - Procedure: `dbo.usp_SyncStock`  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`  
  - Views / other objects: none

#### Key units
- **Procedure**: `dbo.usp_SyncStock`  
  - Parameters: `@Since` (timestamp filter)  
  - Steps: `MERGE` updates `StockLevel` and inserts missing SKUs from `SupplierFeed`; records affected rows in `SyncLog`; handles errors by rollback and re‑throw.

- **Tables**
  - `StockLevel`  
    - Columns: `WarehouseId` INT NOT NULL, `Sku` NVARCHAR(40) NOT NULL, `OnHand` INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0), `UpdatedAt` DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME__)  
    - PK: (`WarehouseId`, `Sku`)  
  - `SupplierFeed`  
    - Columns: `FeedRowId` BIGINT IDENTITY(1,1) PK, `WarehouseId` INT NOT NULL, `Sku` NVARCHAR(40) NOT NULL, `Quantity` INT NOT NULL, `ReceivedAt` DATETIME2(0) NOT NULL  
    - PK: `FeedRowId`  
  - `SyncLog`  
    - Columns: `SyncLogId` INT IDENTITY(1,1) PK, `StartedAt` DATETIME2(0) NOT NULL, `RowsInserted` INT NOT NULL, `RowsUpdated` INT NOT NULL, `ErrorMessage` NVARCHAR(4000) NULL  

- **Relationships**
  - `StockLevel.WarehouseId, StockLevel.Sku` → `SupplierFeed.WarehouseId, SupplierFeed.Sku` (join key)  
  - `SyncLog` records rows inserted/updated from `StockLevel` and `SupplierFeed` during the merge.

#### Data flow
Inputs: `SupplierFeed` rows ≥ `@Since`.  
Pipeline: `SupplierFeed` → `MERGE` into `StockLevel` → update `OnHand` and insert new SKUs → capture affected rows → insert counts into `SyncLog`.  

#### Dependencies
- `dbo.StockLevel`  
- `dbo.SupplierFeed`  
- `dbo.SyncLog`  

#### Cross-cutting
- Uses T‑SQL `MERGE` for atomic inventory updates.  
- Transactional logic with rollback on error.  
- No shared base types or common exception handling beyond standard T‑SQL error handling.
