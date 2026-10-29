### sql/warehouse
- H: 1.0
- C: 87AC43C5
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:23:07Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Automates nightly synchronization of warehouse inventory from supplier feeds, maintaining accurate stock levels and audit logs.

#### Purpose
- Updates warehouse stock inventory from supplier feed data
- Maintains audit trail of synchronization operations and errors

#### Responsibilities
- Inventory synchronization from external supplier feeds
- Data consistency management through merge operations
- Operational logging and error tracking

#### Key units
- `dbo.StockLevel` tracks current warehouse inventory by SKU and location
- `dbo.SupplierFeed` holds incoming supplier data with timestamps
- `dbo.SyncLog` records synchronization metadata and outcomes
- `stock_sync_tsql.sql` procedure orchestrates the merge and logging process

#### Data flow
- Supplier feed data enters via `SupplierFeed` table
- Procedure merges new and updated stock data into `StockLevel`
- Synchronization results logged to `SyncLog` with row counts and errors
- Process triggered nightly with time-based filtering

#### Dependencies
- `dbo.StockLevel` as primary target for inventory updates
- `dbo.SupplierFeed` as source of incoming stock data
- `dbo.SyncLog` for operational audit and error reporting

#### Cross-cutting
- Transactional integrity enforced during merge operations
- Error handling captures and logs failure details to `SyncLog`
- Time-based filtering parameter (`@Since`) enables incremental syncs
- Default timestamp values ensure data freshness in `StockLevel` and `SyncLog`
