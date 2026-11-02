### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:35:19Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified data‑access layer that maps platform error codes, e‑commerce catalog inventory, and payment‑ledger records into immutable, queryable structures for consistent business logic and analytics.

#### Purpose
- Centralize error code definitions for uniform handling.
- Provide a structured inventory catalog for e‑commerce services.
- Persist and analyze payment‑ledger data, including fraud holds and daily transfer volumes.

#### Responsibilities
- **Error handling** – immutable enum of platform error codes with reverse lookup.
- **Catalog management** – XML schema for categories, products, and warehouse stock.
- **Ledger persistence** – PostgreSQL tables for customers, accounts, transfers, fraud holds, and a daily volume view.

#### Key units
- `PlatformErrorCode` enum – numeric ID, message, and `byNumber(int)` lookup.
- XML elements: `catalog`, `category`, `product`, `inv:stock` (namespaces `urn:example:catalog` / `urn:example:inventory`).
- PostgreSQL tables: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`; view `ledger_daily_volume`; indexes `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Error codes**: static map populated from enum values; callers retrieve via `byNumber`.
2. **Catalog**: XML file parsed, namespaces resolved → DTOs or DB rows for categories, products, and stock.
3. **Ledger**: inserts propagate through `ledger_customer → ledger_account → ledger_transfer / ledger_hold`; analytics read from `ledger_daily_volume`.

#### Dependencies
- Java: `java.util.Map`, `HashMap`.
- XML: namespaces `urn:example:catalog`, `urn:example:inventory` (or `http://example.com/inventory`).
- PostgreSQL: foreign keys linking accounts to customers, transfers to accounts, holds to accounts.

#### Cross-cutting
- Immutable, thread‑safe error code map.
- Consistent EUR pricing and warehouse codes (`W1`–`W9`) in catalog.
- Versioned catalog root (`version="3"`).
- Integer primary keys and positive `amount_cents` constraint in ledger tables.
- Indexes targeting common queries: source account lookups, date ranges, and hold lookups.
