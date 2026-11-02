### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:27Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Defines platform error identifiers alongside product catalog, inventory, and customer banking-ledger data for operational lookup and reporting.

#### Purpose
- Provides searchable platform error codes and messages, versioned product listings with stock and recalls, and ledger records with daily transfer totals.
- Groups Java error definitions, catalog data, and SQL ledger structures in one package.

#### Responsibilities
- **Error lookup:** Assigns numeric codes to billing, storage, network, reporting, import, authentication, and ledger-lock errors.
- **Product catalog and inventory:** Organizes products by category, with EUR prices, warehouse stock, and optional supplier-recall details.
- **Banking ledger:** Stores customers, accounts, transfers, and fraud-review holds; aggregates transfer amounts by booking date.

#### Key units
- `PlatformErrorCode` — enum of platform error numbers and messages, indexed by `BY_NUMBER` and queried with `byNumber(int)`.
- `catalog`, `category`, `product` — versioned catalog hierarchy for categorized product listings.
- `price`, `inv:stock`, `inv:recall` — product pricing, warehouse inventory, and optional recall details.
- `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold` — customer and account records, transfers, and account-level hold reasons.
- `ledger_daily_volume` — view of transfer totals grouped by booking date.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer-source, booking-time, and held-account lookups.

#### Data flow
- Java enum initialization builds the numeric error-code index; `byNumber(int)` returns the matching value or `null`.
- Catalog data is nested as `catalog` → `category` → `product`, with pricing and inventory details and optional recall data on products.
- SQL seed data populates customers and accounts, then transfers and holds; `ledger_daily_volume` aggregates transfer amounts by booking date.
- A timestamped SQL value fragment is present without a specified target table or column mapping.

#### Dependencies
- Java error lookup uses `java.util.HashMap` and `java.util.Map`.
- Catalog uses default namespace `urn:example:catalog` and inventory namespace `inv` (`urn:example:inventory`).
- `ledger_account.customer_id` references `ledger_customer`; transfer account fields and `ledger_hold.account_id` reference `ledger_account`; `ledger_daily_volume` reads `ledger_transfer`.

#### Cross-cutting
- Catalog version is `3`; prices use `EUR`, and stock records use warehouse identifiers `W1`–`W9`; stock may be zero.
- Ledger relationships use primary and foreign keys; account IBANs are unique, and transfer amounts in cents must be positive.
- Error enum values and code/message fields are immutable; `BY_NUMBER` is populated during class initialization.
