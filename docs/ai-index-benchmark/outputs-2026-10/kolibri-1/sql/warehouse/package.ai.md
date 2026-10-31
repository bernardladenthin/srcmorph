### sql/warehouse
- H: 1.0
- C: 2C8761B6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:42:56Z
- G: 
- A: 0.0.0
- X: package
- F: [stock_sync_tsql.sql](stock_sync_tsql.sql.ai.md)
---
> Nightly stock synchronisation merges supplier feed quantities into warehouse stock levels and logs the outcome.

#### Purpose
- Nightly stock synchronisation from a supplier feed into warehouse stock levels.
- Merges received quantities into existing stock and logs insert/update counts and errors.

#### Responsibilities
- Merges supplier feed rows received since a given timestamp into warehouse stock levels.
- Updates existing stock levels by adding source quantity and sets updated timestamp.
- Inserts new stock levels for unmatched supplier feed rows.
- Logs sync start time, inserted and updated row counts, and any error message.

#### Key units
- `dbo.usp_SyncStock`: T-SQL stored procedure performing the stock merge.
- `dbo.StockLevel`: Warehouse stock table with warehouse, SKU, on-hand quantity, and updated timestamp.
- `dbo.SupplierFeed`: Supplier feed table with warehouse, SKU, quantity, and received timestamp.
- `dbo.SyncLog`: Sync outcome log table with start time, inserted and updated counts, and error message.

#### Data flow
- Supplier feed rows are read and filtered by received timestamp.
- Rows are matched to warehouse stock levels by warehouse and SKU.
- Matched rows update on-hand quantity and updated timestamp.
- Unmatched rows insert new stock levels.
- Merge actions are logged into the sync log with counts and any error.

#### Dependencies
- Internal: `dbo.StockLevel`, `dbo.SupplierFeed`, `dbo.SyncLog`.
- External: none specified.

#### Cross-cutting
- Transactional merge with rollback on error.
- Rethrows error after logging.
- Uses table variable for merge actions.
