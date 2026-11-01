### ai
- H: 1.0
- C: 8B2A3ED5
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:38:22Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Provides platform error-code reference data, categorized product and warehouse inventory data, and customer account-transfer ledger records with daily volume reporting.

#### Purpose
- Supplies searchable platform failure identifiers and messages alongside product, stock, banking, and transfer data models.
- Supports error-code lookup, catalog organization, and ledger reporting.

#### Responsibilities
- **Platform errors:** `PlatformErrorCode` groups numeric codes and messages across billing, storage, networking, reporting, importing, and authentication.
- **Product catalog and inventory:** XML `catalog` organizes categorized products with EUR prices and warehouse-specific stock.
- **Banking ledger and reporting:** SQL records customers, accounts, and transfers; daily totals summarize booked transfer amounts.

#### Key units
- `PlatformErrorCode` — enum of numeric error identifiers and messages, with lookup by number.
- `catalog`, `category`, `product` — XML elements for catalog version, category identity, and product details.
- `price`, `inv:stock` — XML elements for EUR pricing and warehouse-associated quantities.
- `ledger_customer` — customer identity, display name, and country code.
- `ledger_account` — customer-owned account with IBAN and opening date.
- `ledger_transfer` — account-to-account transfer with positive amount in cents and booking timestamp.
- `ledger_daily_volume` — daily aggregate of booked transfer amounts.
- `idx_transfer_from`, `idx_transfer_booked` — indexes for transfer source-account and booking-time queries.

#### Data flow
- `PlatformErrorCode` initializes a number-to-enum lookup map; `byNumber(int)` returns the matching constant or `null`.
- XML `catalog` contains `category` elements, which contain products with titles, prices, and warehouse stock records.
- SQL customer records link to accounts; transfers link source and destination accounts; `ledger_daily_volume` groups transfers by booking date and sums amounts.

#### Dependencies
- `PlatformErrorCode` uses `java.util.HashMap` and `java.util.Map`.
- Catalog XML uses `urn:example:catalog` and `urn:example:inventory`; stock entries identify warehouses `W1`–`W9`.
- `ledger_account.customer_id` references `ledger_customer`; transfer account fields reference `ledger_account`; daily volume reads `ledger_transfer`.

#### Cross-cutting
- Error-code fields are immutable; numeric lookup is initialized from enum constants.
- Ledger records use primary keys and required fields; account IBANs are unique and transfer amounts must be positive.
