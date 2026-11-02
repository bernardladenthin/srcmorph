### ai
- H: 1.0
- C: 7299836A
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:34:05Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A unified system for managing platform‑wide error codes and an e‑commerce product catalog with pricing and inventory data.

#### Purpose
- Centralize error code definitions for consistent handling across the platform.
- Store and expose product, pricing, and inventory information for an e‑commerce system.

#### Responsibilities
- **Error Management**: Define immutable error codes, provide fast numeric lookup, and expose code/message accessors.
- **Catalog Management**: Organize products into categories, store SKU, title, price (EUR), optional recall data, and per‑warehouse stock levels.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants (`BILLING_00001`, `STORAGE_00002`, …, `LEDGER_LOCK_02899`, `BILLING_03620`) with `int code` and `String message`; static `BY_NUMBER` map and `byNumber(int)` lookup.
- **XML catalog structure** – root `<catalog>` (namespace `urn:example:catalog`), `<category>` elements (`id`, `name`), `<product>` elements (`sku`, `<title>`, `<price currency="EUR">`, optional `<inv:recall>`), and `<inv:stock>` elements (`warehouse`, quantity) in namespace `urn:example:inventory`.

#### Data flow
1. **Error Codes**: Enum constants declared → static block populates `BY_NUMBER` → `byNumber` retrieves instance by code.
2. **Catalog**: XML parsed → categories processed → products extracted with price and optional recall → inventory per warehouse recorded via `inv:stock`.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap` for error lookup.
- XML: namespaces `urn:example:catalog` (default) and `urn:example:inventory` (prefix `inv`); no external schemas or XSLT.

#### Cross-cutting
- Thread‑safe, immutable design for error codes.
- Consistent API (`getNumber()`, `getMessage()`) for error handling.
- Uniform use of `inv` namespace for all inventory elements.
- Prices consistently use `EUR` currency attribute across products.
