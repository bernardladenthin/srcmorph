### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:31Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified catalog of platform error codes, e‑commerce product data, and a PostgreSQL payments ledger schema, all designed for consistent lookup, inventory management, and transaction reporting.

#### Purpose
- Centralize error codes, product catalog, and payment ledger definitions for a single platform.

#### Responsibilities
- **Error handling** – immutable error code constants with reverse lookup.  
- **Product inventory** – XML catalog of categories, products, pricing, stock, and recall data.  
- **Payments ledger** – PostgreSQL schema for customers, accounts, transfers, holds, and daily volume reporting.

#### Key units
- **`PlatformErrorCode` enum** – `int code`, `String message`; static `byNumber(int)` for lookup.  
- **XML catalog** – root `catalog` (`urn:example:catalog`), `category`, `product` (SKU, title, price, `<inv:stock>`, optional `<inv:recall>`).  
- **PostgreSQL tables** – `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`; **view** `ledger_daily_volume`.  
- **Indexes** – `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error codes**: enum constants loaded once; `byNumber` retrieves code by integer.  
2. **Product catalog**: system parses XML → extracts categories, products, prices, per‑warehouse stock, and recall flags for downstream processing.  
3. **Payments ledger**: insert customers → accounts (FK to customers) → transfers/holds (FK to accounts); view aggregates `ledger_transfer.amount_cents` per day.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`.  
- XML: namespaces `urn:example:catalog` and `http://example.com/inventory`.  
- PostgreSQL: foreign keys among `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`; view depends on `ledger_transfer`.

#### Cross-cutting
- Immutable design for thread safety in error codes.  
- SKU as unique product key; prices always in EUR; stock quantities are integers.  
- Ledger uses `INTEGER PRIMARY KEY`, `BIGINT` for amounts, `CHECK (amount_cents > 0)`, and unique IBAN constraint.  
- Consistent indexing on foreign keys and timestamps for performance.
