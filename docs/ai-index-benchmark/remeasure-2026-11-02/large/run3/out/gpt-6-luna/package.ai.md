### ai
- H: 1.0
- C: 121477E9
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:45Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides shared platform reference data for error handling, product inventory, and customer banking and transfer records.

#### Purpose
- Combines a numeric platform error catalog, a categorized product and inventory catalog, and a seeded banking ledger with daily transfer reporting.
- Supports lookup, catalog and stock reference, and account-transfer data needs.

#### Responsibilities
- **Platform errors:** Groups billing, storage, network, reporting, import, authentication, and ledger-lock failures under stable numeric codes.
- **Product catalog and inventory:** Organizes products by category with EUR prices, warehouse stock, and selected batch-specific supplier recalls.
- **Banking ledger and reporting:** Defines customer, account, transfer, and hold records; reports booked transfer totals by day.

#### Key units
- `PlatformErrorCode` — enum mapping numeric error identifiers to names and messages, with lookup through `byNumber(int)`.
- `catalog` — Version 3 root containing categories and their product records.
- `category`, `product` — Product group and SKU-keyed item with title, price, and inventory data.
- `inv:stock`, `inv:recall` — Warehouse quantity and batch-specific supplier-recall records.
- `ledger_customer`, `ledger_account` — Customer identity and country, and customer-linked bank accounts.
- `ledger_transfer`, `ledger_hold` — Account-to-account transfers and account hold reasons.
- `ledger_daily_volume` — Daily aggregation of booked transfer amounts.

#### Data flow
- Error constants are indexed by numeric code for lookup; catalog categories group products, whose records carry pricing, stock, and selected recall details.
- Ledger customers link to accounts; transfers and holds reference accounts, while daily reporting groups transfers by booking date and sums amounts in cents.

#### Dependencies
- Product inventory annotations use the `urn:example:inventory` namespace.
- Ledger foreign keys connect `ledger_account` to `ledger_customer`, and transfers and holds to `ledger_account`; `ledger_daily_volume` reads `ledger_transfer`.
- `PlatformErrorCode` uses `java.util.HashMap` and `java.util.Map` for numeric lookup.

#### Cross-cutting
- Ledger primary and foreign keys enforce identity and relationships; account IBANs are unique, transfer amounts must be positive, and indexes cover transfer source account, booking timestamp, and hold account.
- Error-code lookup is populated during enum initialization and read without synchronization.
