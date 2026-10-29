### stock_sync_tsql.sql
- H: 1.0
- C: 197358EF
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:09:36Z
- G: 
- A: 0.0.0
- X: file
---
**Purpose**  
- Merges supplier feed data into the `StockLevel` inventory table and records sync outcomes.

**Type**  
- **SQL Server (T‑SQL)**  
  - Procedure: `dbo.usp_SyncStock`  
  - Tables: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`  
  - Views / other objects: none

**Schema**  

| Table | Columns (type & constraints) |
|-------|------------------------------|
| **StockLevel** | `WarehouseId` INT NOT NULL, `Sku` NVARCHAR(40) NOT NULL, `OnHand` INT NOT NULL CONSTRAINT DF_StockLevel_OnHand DEFAULT (0), `UpdatedAt` DATETIME2(0) NOT NULL CONSTRAINT DF_StockLevel_UpdatedAt DEFAULT (SYSUTCDATETIME()), **PK**: (`WarehouseId`, `Sku`) |
| **SupplierFeed** | `FeedRowId` BIGINT IDENTITY(1,1) PK, `WarehouseId` INT NOT NULL, `Sku` NVARCHAR(40) NOT NULL, `Quantity` INT NOT NULL, `ReceivedAt` DATETIME2(0) NOT NULL, **PK**: `FeedRowId` |
| **SyncLog** | `SyncLogId` INT IDENTITY(1,1) PK, `StartedAt` DATETIME2(0) NOT NULL, `RowsInserted` INT NOT NULL, `RowsUpdated` INT NOT NULL, `ErrorMessage` NVARCHAR(4000) NULL |

**Reads**  
- `StockLevel` (via `MERGE` source rows)  
- `SupplierFeed` (via `MERGE` `source` SELECT)

**Writes**  
- `StockLevel` (`OnHand` updates, new rows via `INSERT`)  
- `SyncLog` (inserts a row with counts and optional error message)

**Relationships**  
- `StockLevel.WarehouseId, StockLevel.Sku` → `SupplierFeed.WarehouseId, SupplierFeed.Sku` (join key)  
- `SyncLog` records rows inserted/updated from `StockLevel` and `SupplierFeed` during the merge.

**Routine logic**  
- **Parameters**: `@Since` – timestamp to filter `SupplierFeed` rows.  
- **Steps**:  
  1. Capture start time.  
  2. Use a `MERGE` to update `OnHand` and insert missing SKUs from `SupplierFeed` rows received ≥ `@Since`.  
  3. Capture affected rows in `@Changes` table.  
  4. Insert counts into `SyncLog` (rows inserted = `INSERT`s, rows updated = `UPDATE`s).  
  5. On error, rollback transaction, log error message, and re‑throw.  

**Dependencies**  
- `dbo.StockLevel`  
- `dbo.SupplierFeed`  
- `dbo.SyncLog`  

*(No other tables, views, or functions are referenced.)*
