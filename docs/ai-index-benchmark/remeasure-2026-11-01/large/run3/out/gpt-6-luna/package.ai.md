### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:13:06Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides shared platform reference data for error handling, product inventory, and customer banking activity.

#### Purpose
- Groups platform error-code definitions, a categorized EUR product catalog with stock and recalls, and a customer-account ledger with transfer reporting.
- Supplies lookup, inventory, and ledger data structures for platform operations.

#### Responsibilities
- **Platform errors:** Catalogue errors across billing, storage, networking, reporting, imports, authentication, and ledger locks; resolve numeric codes to identifiers.
- **Product catalog and inventory:** Organize SKU products by category with EUR prices, warehouse stock, and optional supplier recall details.
- **Banking ledger:** Define customer-owned accounts, account transfers, and holds; provide daily transfer-volume aggregates.

#### Key units
- `PlatformErrorCode` — enum of 6,000 error constants, each with a numeric code and message; `byNumber(int)` resolves codes.
- `catalog.xml` — catalog version 3 metadata, categories, products, prices, stock, and recall records.
- `inv:stock`, `inv:recall` — namespaced warehouse inventory and batch-recall elements.
- `ledger_customer` — customer identifiers, names, and country codes.
- `ledger_account` — customer-linked bank accounts with unique IBANs and opening dates.
- `ledger_transfer` — positive-cent transfers between accounts with booking timestamps.
- `ledger_hold` — account-linked hold reasons.
- `ledger_daily_volume` — transfer-amount totals grouped by booking date.

#### Data flow
- `PlatformErrorCode` constants populate a numeric-code lookup during class initialization.
- Catalog products are grouped by category, priced in EUR, associated with warehouses `W1`–`W9`, and optionally annotated with supplier recall batches.
- Seeded customers and accounts support transfer and hold records; `ledger_daily_volume` groups transfers by the date of `booked_at` and sums `amount_cents`.

#### Dependencies
- Java `HashMap` and `Map` back the `PlatformErrorCode` number index.
- Catalog XML uses namespaces `urn:example:catalog` and `urn:example:inventory`.
- `ledger_account.customer_id` references `ledger_customer`; transfer source/destination and hold account IDs reference `ledger_account`; `ledger_daily_volume` reads `ledger_transfer`.
- SQL indexes support transfer source-account and booking-time lookups, and hold-account lookups.

#### Cross-cutting
- Error constants are immutable; the numeric lookup is populated at class initialization and not subsequently mutated.
- Catalog category IDs follow `cat-###`; product identifiers use SKU-style names. The described `SKU-036-0116` record is incomplete.
- Ledger records use integer primary keys and required `NOT NULL` fields; transfer amounts must be positive and account IBANs unique.
