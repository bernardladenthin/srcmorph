### ai
- H: 1.0
- C: 8B2A3ED5
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:26:17Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides reference data for platform error identification, categorized product and inventory lookup, and customer-linked banking transfers with daily volume reporting.

#### Purpose
- Groups an error-code catalog, product catalog summaries, and a SQL ledger schema with seed-data fragments.
- Supports identifying platform failures, reviewing EUR-priced products and warehouse stock, and reporting booked transfer volume.

#### Responsibilities
- **Error identification:** Maps stable numeric codes to messages across billing, storage, network, reporting, import, and authentication.
- **Product and inventory catalog:** Organizes products by category and presents titles, EUR prices, and stock quantities for warehouses `W1`–`W9`.
- **Banking ledger and reporting:** Defines customers, accounts, positive-value transfers, and daily transfer totals; includes seed-data fragments.

#### Key units
- `PlatformErrorCode` — enum of error numbers and messages, with numeric lookup via `byNumber(int)`.
- `catalog` — product-catalog container described with `version="3"`.
- `category` — product grouping spanning identifiers `cat-001`–`cat-040`.
- `product`, `price`, `inv:stock` — product details, EUR pricing, and warehouse-specific quantities.
- `ledger_customer` — customer identifiers, display names, and country codes.
- `ledger_account` — customer-linked accounts with unique IBANs and opening dates.
- `ledger_transfer` — account-to-account transfers with positive amounts in cents and booking timestamps.
- `ledger_daily_volume` — daily aggregate of booked transfer amounts.
- `idx_transfer_from`, `idx_transfer_booked` — indexes on transfer source account and booking timestamp.

#### Data flow
- `PlatformErrorCode` initializes a numeric lookup map; `byNumber(int)` returns a matching enum constant or `null`.
- Product summaries group records by category and report each product’s title, EUR price, and warehouse stock.
- Ledger accounts link to customers; transfers link source and destination accounts; `ledger_daily_volume` groups transfer amounts by booking date.

#### Dependencies
- `PlatformErrorCode` uses `java.util.HashMap` and `java.util.Map`.
- `ledger_account.customer_id` references `ledger_customer.customer_id`; transfer account fields reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.
- The product summary mentions inventory namespace URI `urn:example:inventory`; its binding is not visible in all parts.

#### Cross-cutting
- Error constants and fields are immutable; the lookup map is populated during static initialization.
- Ledger records use primary keys; account IBANs are unique, required fields are `NOT NULL`, and transfer amounts must be positive.
- Product summaries note partial records around categories 19, 37, and 40; SQL seed batches may be incomplete or split across parts.
