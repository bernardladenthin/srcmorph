### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T02:55:05Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Warehouse: nightly stock synchronisation from a supplier feed (SQL Server, T-SQL).

## Purpose
- Synchronize stock levels from a supplier feed into the warehouse database.
- Log the outcome of the synchronization process.

## Responsibilities
- SQL procedure for synchronizing stock levels.
- Logging synchronization results.

## Key units
- **dbo.StockLevel**
  - Stores stock levels for each warehouse and SKU.
- **dbo.SupplierFeed**
  - Contains supplier feed data for stock synchronization.
- **dbo.SyncLog**
  - Logs the synchronization process outcomes.

## Data flow
- Reads data from `dbo.SupplierFeed` to update `dbo.StockLevel`.
- Writes synchronization results to `dbo.SyncLog`.

## Dependencies
- References `dbo.SupplierFeed` for data.
- Uses `dbo.SyncLog` for logging.

## Cross-cutting
- Uses transactions for data consistency.
- Logs errors and exceptions during synchronization.
