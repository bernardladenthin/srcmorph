### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:31Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides platform error identification, product and inventory reference data, and customer-account ledger records with transfer reporting.

#### Purpose
- Groups application error codes, categorized EUR-priced products and warehouse inventory, and seeded banking-ledger data.
- Supports numeric error lookup, product and recall records, and daily transfer-volume aggregation.

#### Responsibilities
- Maintain `PlatformErrorCode` values and descriptions, including the `LEDGER_LOCK` range.
- Organize 5,000 SKU-keyed products across 40 categories, with prices, stock quantities, and selected supplier recalls.
- Define and seed customer, account, transfer, and hold tables, plus daily transfer-volume reporting.

#### Key units
- `PlatformErrorCode` — numeric error-code enum with message retrieval and `byNumber(int)` lookup.
- `catalog`, `category`, `product` — versioned product catalog, category groupings, and SKU-keyed product records.
- `inv:stock`, `inv:recall` — warehouse quantities and batch-identified supplier recall notices.
- `ledger_customer`, `ledger_account` — customer identities and their uniquely IBAN-identified accounts.
- `ledger_transfer`, `ledger_hold` — account-to-account transfers and account-linked hold reasons.
- `ledger_daily_volume` — transfer amounts aggregated by booking date.
- `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account` — indexes for transfer-source, booking-time, and hold-account lookups.

#### Data flow
- `PlatformErrorCode` enum values populate a number-to-enum index used by `byNumber(int)`.
- Catalog categories group products; product records carry EUR prices and inventory-namespace stock or recall data.
- Ledger customers link to accounts, which are referenced by transfers and holds; `ledger_daily_volume` sums transfer amounts by `booked_at` date.

#### Dependencies
- Error-code lookup uses `java.util.HashMap` and `java.util.Map`.
- Product records use `urn:example:catalog` and `urn:example:inventory` namespaces.
- Ledger tables use foreign keys between customers, accounts, transfers, and holds; daily volume depends on `ledger_transfer`.

#### Cross-cutting
- Error codes are indexed once during static initialization; unmatched numeric lookups return `null`.
- Product SKUs use the `SKU-NNN-NNNN` pattern; stock may be zero, and warehouse codes range from `W1` to `W9`.
- Ledger primary and foreign keys enforce record relationships; account IBANs are unique and transfer amounts must be positive.
