### ai
- H: 1.0
- C: 7299836A
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:15:56Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A unified catalog of platform error codes and an e‑commerce product inventory stored in a single XML document.

#### Purpose
- Centralize platform‑wide error codes for consistent lookup.
- Persist product listings, prices, and per‑warehouse inventory in a single XML source.

#### Responsibilities
- **Error catalog**: Define numeric error IDs, messages, and provide fast reverse lookup.
- **Product catalog**: Structure product data with categories, SKUs, prices, and inventory per warehouse.

#### Key units
- **`PlatformErrorCode` enum** – constants with numeric ID, message, and a static `Map<Integer, PlatformErrorCode>` for lookup.
- **`byNumber(int)`** – static method returning the enum instance for a given code or `null`.
- **`catalog` XML element** – root with `version="3"`, namespaces `urn:example:catalog` and `urn:example:inventory`.
- **`category` elements** – 25 named categories with `id` and `name`.
- **`product` elements** – `sku`, `<title>`, `<price currency="EUR">`, and `<inv:stock warehouse="W#">`.
- **`inv:stock` element** – namespace‑qualified stock quantity per warehouse.

#### Data flow
1. **Error catalog**: Enum constants instantiated → static block populates `BY_NUMBER` → clients call `byNumber(code)` → retrieve `getNumber()` / `getMessage()`.
2. **Product catalog**: XML file `catalog.xml` loaded → parser reads root, namespaces, categories, products, and inventory → domain objects (SKU, title, price, stock per warehouse) created → data exposed to downstream services.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap` for error lookup.
- XML: self‑contained namespaces `urn:example:catalog` and `urn:example:inventory`; no external schemas.

#### Cross-cutting
- Immutable enum instances; thread‑safe static map after initialization.
- SKU convention `SKU-<cat>-<sequence>` links products to categories.
- Prices uniformly in EUR (`currency="EUR"`).
- Inventory granularity per warehouse (`W1`‑`W9`) supports multi‑location stock management.
