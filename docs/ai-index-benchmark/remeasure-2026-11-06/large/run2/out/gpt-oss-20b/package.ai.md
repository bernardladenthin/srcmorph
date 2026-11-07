### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:15:16Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 1BEE9CA6
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified platform that centralises error handling, product cataloging, and payment ledger data for downstream services.

#### Purpose
- Provide a single source of truth for error codes, product inventory, and financial transactions.
- Enable fast lookup, structured data consumption, and analytics across domains.

#### Responsibilities
- **Error Management** – Define and expose immutable platform‑wide error codes.
- **Product Catalog** – Offer a structured XML catalog with pricing, stock, and recall information.
- **Payments Ledger** – Supply a populated PostgreSQL schema and analytics view for transfer volumes.

#### Key units
- **`PlatformErrorCode` enum** – numeric ID, message, and `byNumber` lookup.
- **XML catalog elements** – `catalog`, `category`, `product`, `inv:stock`, `inv:recall`.
- **Ledger tables** – `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.
- **Analytics view** – `ledger_daily_volume` summarising daily transfer totals.

#### Data flow
1. **Error codes**: compiled into enum constants; clients retrieve via `byNumber`.
2. **Catalog**: `catalog.xml` parsed into DTOs; products expose SKU, title, price, warehouse stock map, and optional recall batch.
3. **Ledger**: bulk inserts populate customer, account, transfer, and hold tables; `ledger_daily_volume` aggregates transfers by date for reporting.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`.
- XML: namespaces `urn:example:catalog` and `urn:example:inventory`.
- PostgreSQL: foreign keys linking accounts to customers, transfers to accounts, holds to accounts; view depends on `ledger_transfer`.

#### Cross-cutting
- Thread‑safe, immutable error constants.
- Consistent currency (`EUR`) and warehouse identifiers (`W1`–`W9`) across catalog.
- Standard PostgreSQL constraints and indexes for performance; no triggers or extensions.
