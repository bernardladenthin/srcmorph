### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:36:55Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides reference data for platform errors and products alongside customer-account, transfer, fraud-hold, and daily-volume records.

#### Purpose
- Combines a numeric platform-error catalogue, a categorized EUR product catalog, and a banking ledger with daily transfer aggregation.
- Supplies structured records for error lookup, product and inventory reference, and account-transfer reporting.

#### Responsibilities
- **Platform errors:** Names billing, storage, network, reporting, import, authentication, and ledger-lock failures and associates each with a numeric code and message.
- **Product catalog:** Organizes 5,000 products into 40 categories, recording identifiers, titles, EUR prices, and warehouse stock; selected products in Categories 20 and 21 include supplier-recall details.
- **Banking ledger:** Defines customer, account, transfer, and fraud-review hold records; includes bulk data inserts and aggregates transfer amounts by booking date.

#### Key units
- `PlatformErrorCode` — enumerates named platform errors and supports lookup by numeric identifier.
- `catalog` — version 3 root containing category records.
- `category` — groups product records; categories range from `cat-001` through `cat-040`.
- `product` — stores an item’s SKU, title, price, and inventory data.
- `inv:stock` — records warehouse and stock quantity.
- `inv:recall` — records a batch identifier and supplier-recall text.
- `ledger_customer` — stores customer identifiers, display names, and country codes.
- `ledger_account` — links accounts to customers and stores IBANs and opening dates.
- `ledger_transfer` — records source and destination accounts, positive amounts in cents, and booking timestamps.
- `ledger_hold` — records account-linked fraud-review holds and reasons.
- `ledger_daily_volume` — sums transfer amounts by booking date.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — index transfer sources, booking times, and held accounts.

#### Data flow
- `PlatformErrorCode` entries populate a number-to-error lookup map; unknown numbers return `null`.
- Product categories contain product records, with stock data and, for Categories 20 and 21, recall details.
- Customers link to accounts; transfers and holds reference accounts. `ledger_daily_volume` groups transfers by the date of `booked_at` and sums `amount_cents`.

#### Dependencies
- `PlatformErrorCode` uses `java.util.HashMap` and `java.util.Map`.
- Product records use the `urn:example:catalog` default namespace and `urn:example:inventory` namespace.
- `ledger_account.customer_id` references `ledger_customer.customer_id`; transfer and hold account fields reference `ledger_account.account_id`.
- `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Product prices use `currency="EUR"`; recall records occur only in Categories 20 and 21.
- Ledger primary and foreign keys enforce row identity and customer/account relationships; account IBANs are unique and transfer amounts must be positive.
