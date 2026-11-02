### ai
- H: 1.0
- C: 7299836A
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:54:18Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A unified catalogue system that centralises platform error codes and product inventory data for an e‑commerce platform.

#### Purpose
- Provide a single source of truth for error codes and product catalog information.
- Enable fast lookup of error messages and structured access to product, price, and stock data.

#### Responsibilities
- **Error handling** – define immutable error codes with numeric IDs and human‑readable messages.
- **Product cataloging** – expose a hierarchical XML structure of categories, products, prices, inventory per warehouse, and optional recall data.
- **Lookup services** – offer efficient retrieval mechanisms for both error codes and catalog elements.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants (`int number`, `String message`), static `BY_NUMBER` map, methods `getNumber()`, `getMessage()`, `byNumber(int)`.
- **XML catalog schema** – root `catalog` (namespace `urn:example:catalog`, version="3") containing `category` (id, name) → `product` (sku, title, price, inventory) elements.
- **Inventory elements** – `inv:stock` (warehouse, quantity) and optional `inv:recall` (batch) within the `urn:example:inventory` namespace.

#### Data flow
1. **Error codes**: On class load, `PlatformErrorCode` populates a read‑only `BY_NUMBER` map; clients call `byNumber(code)` to obtain the enum and then `getMessage()` for the description.
2. **Catalog**: An application loads `catalog.xml`; XML parser traverses `catalog` → `category` → `product`, extracting `title`, `price` (currency="EUR"), and all `inv:stock` entries per warehouse (`W1`–`W9`). Optional `inv:recall` data is captured if present. The extracted data is then mapped to domain objects or persisted.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.
- XML: namespaces `urn:example:catalog` (default) and `urn:example:inventory` (prefix `inv`); no external schemas or XSLT.

#### Cross-cutting
- **Thread safety** – static initialization of `BY_NUMBER` is thread‑safe; catalog parsing is read‑only.
- **Standardization** – all prices use `currency="EUR"`; warehouse codes are consistently `W1`–`W9`.
- **Optionality** – `inv:recall` elements may be absent; consumers must handle null or missing values gracefully.
