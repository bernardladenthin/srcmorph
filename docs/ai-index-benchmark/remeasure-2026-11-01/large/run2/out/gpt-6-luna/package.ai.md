### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:52:05Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides platform error lookup, a versioned product and inventory catalog, and customer-account transfer and fraud-review records.

#### Purpose
- Combines categorized platform error identifiers, product pricing and stock data, and a sample banking ledger with daily transfer reporting.
- Supports numeric error lookup, catalog organization, and ledger relationships and aggregation.

#### Responsibilities
- **Error catalogue:** Defines 6,000 categorized error codes and indexes them by number.
- **Product catalog:** Groups products into categories `cat-001`–`cat-040` with EUR prices, warehouse stock (`W1`–`W9`), and recall batches for `cat-020` and `cat-021`.
- **Banking ledger:** Stores customers, accounts, transfers, and fraud-review holds; summarizes transfer amounts by booking date.
- **Seed data:** Populates ledger tables with sample customer, account, transfer, and hold records.

#### Key units
- `PlatformErrorCode` — enum of numbered error identifiers and messages, with `byNumber(int)` lookup.
- `catalog`, `category`, `product` — XML structures for catalog version `3`, product grouping, and SKU, price, stock, and optional recall data.
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with unique IBANs and opening dates.
- `ledger_transfer` — positive-amount transfers between accounts with booking timestamps.
- `ledger_hold` — account-linked fraud-review hold reasons.
- `ledger_daily_volume` — transfer amount totals grouped by booking date.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes supporting transfer-source, booking-time, and held-account lookups.

#### Data flow
- `PlatformErrorCode` initialization builds the numeric lookup map; `byNumber` returns the matching enum entry or `null`.
- Catalog categories contain product records with pricing, inventory, and, where present, recall details.
- Ledger customers support account records; accounts link transfers and holds; `ledger_daily_volume` aggregates `ledger_transfer` by booking date.

#### Dependencies
- Error lookup uses `java.util.HashMap` and `java.util.Map`.
- Catalog XML uses `urn:example:catalog` and `urn:example:inventory`.
- Ledger foreign keys link accounts to customers and transfers and holds to accounts; the daily-volume view reads from transfers.

#### Cross-cutting
- Error enum values are immutable; its lookup map is populated during static initialization.
- Ledger constraints enforce unique account IBANs, positive transfer amounts, and foreign-key relationships.
- Catalog prices use EUR; recall batch identifiers span `R-020-0001`–`R-020-0125` and `R-021-0001`–`R-021-0125`.
