### ai
- H: 1.0
- C: 8B2A3ED5
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:33Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> Defines platform error lookup, versioned product inventory, and a customer-owned bank ledger with daily transfer reporting.

#### Purpose
- Bundles three domain resources: a numeric platform-error catalogue, a categorized product catalog, and a ledger schema with transfer-volume reporting.
- Supplies lookup data, catalog and inventory records, and SQL tables, constraints, indexes, seed data, and an aggregate view.

#### Responsibilities
- **Error catalogue (`java/`)** — maps platform error numbers to immutable error codes and human-readable messages.
- **Product catalog (`other/`)** — organizes products by category with SKU, title, EUR price, and warehouse stock; data for categories 19 and 37 is incomplete or fragmentary.
- **Bank ledger (`sql/`)** — models customers, accounts, and positive-amount transfers; seeds the tables and reports daily transfer volume.

#### Key units
- `PlatformErrorCode` — enum mapping error constants to numeric codes and messages, with lookup by number.
- `catalog`, `category`, `product`, `price`, `inv:stock` — catalog structure for versioned categories, product details, EUR prices, and warehouse quantities.
- `ledger_customer` — customer ID, display name, and country code.
- `ledger_account` — account owner, unique IBAN, and opening date.
- `ledger_transfer` — source and destination accounts, positive amount in cents, and booking timestamp.
- `ledger_daily_volume` — view returning transfer volume grouped by booking date.
- `idx_transfer_from`, `idx_transfer_booked` — indexes on transfer source account and booking timestamp.

#### Data flow
- `PlatformErrorCode` constants populate a code-to-enum index; `byNumber(int)` returns a matching entry or `null`.
- Catalog data nests categories and products, with prices and inventory attached to each product.
- Ledger customers own accounts; transfers reference source and destination accounts; `ledger_daily_volume` aggregates transfer amounts by the date of `booked_at`.

#### Dependencies
- Error lookup uses Java `HashMap` and `Map`.
- Catalog data uses `urn:example:catalog` and `urn:example:inventory` namespaces.
- `ledger_account.customer_id` references `ledger_customer`; transfer account references point to `ledger_account`; the daily-volume view reads `ledger_transfer`.

#### Cross-cutting
- Catalog version is `3`; visible prices use `EUR`, and warehouse codes range from `W1` to `W9`.
- Ledger primary keys identify customers, accounts, and transfers; required fields are `NOT NULL`, IBANs are unique, and transfer amounts must be positive.
- Ledger seed data comprises 60 `INSERT` statements and approximately 30,000 value rows; segment attribution and transfer ID ranges are uncertain or overlapping.
