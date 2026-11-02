### ai
- H: 1.0
- C: 8B2A3ED5
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:33:04Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A unified platform for managing error codes, product inventory, and financial ledger data across multiple subsystems.

#### Purpose
- Centralize error code definitions for consistent messaging.
- Provide a structured product catalog for inventory lookup.
- Persist and analyze customer, account, and transfer data in a PostgreSQL ledger.

#### Responsibilities
- **Error Management** – Define numeric error codes and messages; expose fast read‑only lookup.
- **Inventory Catalog** – Store hierarchical product data with SKU, price, and warehouse stock.
- **Financial Ledger** – Maintain customers, accounts, transfers, and daily transfer volume analytics.

#### Key units
- **`PlatformErrorCode` enum** – constants with `number`, `message`; static `BY_NUMBER` map; `byNumber(int)` lookup.
- **XML catalog** – root `catalog` (versioned), `category` (id, name), `product` (sku, title, price, `inv:stock`), `inv:stock` (warehouse, quantity).
- **PostgreSQL tables** – `ledger_customer` (customer_id PK, display_name, country_code); `ledger_account` (account_id PK, customer_id FK, iban, opened_on); `ledger_transfer` (transfer_id PK, from_account FK, to_account FK, amount_cents, booked_at); `ledger_daily_volume` view (sum of amount_cents per day).

#### Data flow
1. **Error codes**: Enum constants → static map → `byNumber` lookup.
2. **Inventory**: `catalog.xml` parsed → DOM or objects → categories → products → stock per warehouse.
3. **Ledger**: Insert customer → `ledger_customer`; insert account → `ledger_account`; insert transfer → `ledger_transfer`; query `ledger_daily_volume` for daily totals.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`.
- XML: `urn:example:catalog` and `urn:example:inventory` namespaces.
- PostgreSQL: foreign keys (`ledger_account.customer_id → ledger_customer.customer_id`, `ledger_transfer.from_account → ledger_account.account_id`, `ledger_transfer.to_account → ledger_account.account_id`); indexes on `from_account` and `booked_at`; view depends on `ledger_transfer`.

#### Cross-cutting
- Thread‑safe, read‑only error map after static init.
- Consistent attribute usage (`id`, `name`, `sku`, `warehouse`) across XML elements.
- Currency standardization (`price` always EUR).
- Referential integrity enforced by foreign‑key constraints and non‑negative `amount_cents` check.
