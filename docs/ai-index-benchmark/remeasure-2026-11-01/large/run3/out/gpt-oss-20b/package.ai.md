### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:16:28Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified platform for error handling, product cataloging, and financial ledger management across a multi‑service e‑commerce ecosystem.

#### Purpose
- Centralize error codes for consistent platform‑wide error handling.  
- Store and expose product metadata and inventory for an online store.  
- Persist and aggregate financial ledger data for customers, accounts, transfers, and holds.

#### Responsibilities
- **Error Management** – define and look up platform error codes.  
- **Catalog Service** – organize products into categories, provide pricing and inventory per warehouse.  
- **Ledger Service** – model customers, accounts, transfers, holds, and expose daily transfer volume.

#### Key units
- **`PlatformErrorCode` enum** – numeric ID + message, with `byNumber(int)` lookup.  
- **XML `<catalog>`** – root element with `<category>` → `<product>` hierarchy; `<price>` (EUR) and `<inv:stock>` (warehouse, quantity).  
- **PostgreSQL tables**:  
  - `ledger_customer` (id, name, country)  
  - `ledger_account` (id, customer_id, iban, open_date)  
  - `ledger_transfer` (id, from_account, to_account, amount, booked_at)  
  - `ledger_hold` (id, account_id, reason)  
- **View `ledger_daily_volume`** – aggregates `ledger_transfer.amount` by day.  
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error handling**: code calls `PlatformErrorCode.byNumber(code)` → retrieves enum → `getNumber()` / `getMessage()` for logging or API response.  
2. **Catalog consumption**: XML parser reads `<catalog>` → iterates categories → products → extracts `<price>` and `<inv:stock>` for display or inventory checks.  
3. **Ledger operations**:  
   - Insert into `ledger_customer` → `ledger_account` → `ledger_transfer` / `ledger_hold`.  
   - Query `ledger_daily_volume` for reporting or analytics.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`.  
- XML: two namespaces (`urn:example:catalog`, `urn:example:inventory`).  
- PostgreSQL: foreign keys between `ledger_account`, `ledger_customer`, `ledger_transfer`, `ledger_hold`; view depends on `ledger_transfer`.

#### Cross-cutting
- Thread‑safe, read‑only error code map.  
- Consistent namespace usage in XML to separate catalog data from inventory.  
- Integer primary keys and foreign key constraints enforce referential integrity across ledger tables.  
- Indexes target common query patterns (transfer source, booking date, hold account).
