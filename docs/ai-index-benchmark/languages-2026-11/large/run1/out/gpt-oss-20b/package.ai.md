### ai
- H: 1.0
- C: 8B2A3ED5
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T01:09:19Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified infrastructure layer that centralizes error codes, stores an e‑commerce product catalog, and manages a PostgreSQL payment ledger with daily volume aggregation.

#### Purpose
- Centralize platform error codes, product catalog data, and payment ledger persistence.

#### Responsibilities
- **Error handling** – immutable enum of error codes with numeric lookup.  
- **Product catalog** – XML‑based storage of categories, products, pricing, and per‑warehouse inventory.  
- **Payments ledger** – relational tables for customers, accounts, transfers, and a materialised view for daily transfer volume.

#### Key units
- **Java**: `PlatformErrorCode` enum; static `BY_NUMBER` map.  
- **XML**: `<catalog>`, `<category>`, `<product>`, `<price>`, `<inv:stock>` (namespace `http://example.com/inventory`).  
- **SQL**: `ledger_customer`, `ledger_account`, `ledger_transfer`; view `ledger_daily_volume`; indexes `idx_transfer_from`, `idx_transfer_booked`.

#### Data flow
- **Error codes**: enum constants → `BY_NUMBER` map → `byNumber(id)` lookup.  
- **Catalog**: `<category>` → `<product>` → `<price>` + `<inv:stock>` per warehouse.  
- **Ledger**: insert into `ledger_customer` → `ledger_account` (FK) → `ledger_transfer` (FK) → `ledger_daily_volume` aggregates by day.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`.  
- XML: `inv` namespace `http://example.com/inventory`.  
- PostgreSQL: foreign keys (`ledger_transfer` → `ledger_account` → `ledger_customer`), standard constraints, indexes.

#### Cross-cutting
- Immutable, thread‑safe design for error codes.  
- Consistent attribute usage (`id`, `name`, `sku`, `warehouse`).  
- Referential integrity enforced via FK constraints; `amount_cents` positive check.  
- No external configuration or triggers; pure compile‑time constants and standard SQL.
