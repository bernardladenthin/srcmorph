### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:40:18Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A single XML catalog that centralises product listings, pricing, and per‑warehouse inventory for an e‑commerce platform.

#### Purpose
- Store product metadata (SKU, title, price) and inventory levels in a single, static XML file.
- Provide a read‑only source for downstream services such as product display, pricing, and stock checks.

#### Responsibilities
- **Catalog definition** – declares categories and items.
- **Inventory tracking** – records stock quantities per warehouse.
- **Recall handling** – flags products subject to supplier recalls.

#### Key units
- **`catalog.xml`** – root `<catalog>` (namespace `urn:example:catalog`), containing:
  - `<category id="" name="">` (≈40).
  - `<product sku="">` (≈1250) with `<title>`, `<price currency="EUR">`, `<inv:stock warehouse="">`, optional `<inv:recall batch="">`.
- **Namespaces** – `urn:example:catalog` for catalog structure, `urn:example:inventory` for `<inv:stock>` and `<inv:recall>` elements.

#### Data flow
1. **Load** the XML file at application start.
2. **Read** `<product>` entries, extracting SKU, title, price, and inventory per warehouse.
3. **Expose** the data via APIs or internal services for product display, pricing, and stock checks.

#### Dependencies
- XML namespaces `urn:example:catalog` and `urn:example:inventory`.
- No external databases or code; data is embedded in the XML file.

#### Cross-cutting
- Uniform use of `<inv:stock>` for inventory and optional `<inv:recall>` for recall status.
- All prices use `currency="EUR"` to ensure consistent currency handling.
