### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:18:37Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly stock synchronization from a supplier feed.

#### Purpose
- Synchronizes stock levels in the warehouse with data from a supplier feed.
- Logs the synchronization process, including rows inserted and updated.

#### Key units
- **stock_sync_tsql.sql**: Procedure for nightly stock synchronization.

#### Data flow
- **SupplierFeed** data is read.
- Data is merged into **StockLevel**.
- The synchronization process is logged in **SyncLog**.

#### Dependencies
- **StockLevel**: Central table for stock levels.
- **SupplierFeed**: Source of stock data from the supplier.
- **SyncLog**: Table for logging synchronization results.
