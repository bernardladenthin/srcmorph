### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:40:15Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A single XML catalog that stores product listings, pricing, and per‑warehouse inventory for an e‑commerce system.

#### Purpose
- Central repository of product data for an online store.
- Supplies SKU, title, price, and stock levels to downstream services.

#### Responsibilities
- **Catalog definition** – declares product categories and items.
- **Inventory tracking** – records stock quantities per warehouse.
- **Recall handling** – flags products subject to supplier recalls.

#### Key units
- **`catalog.xml`** – root element `<catalog>` (namespace `urn:example:catalog`), containing:
  - `<category id="" name="">` elements (40 total).
  - `<product sku="">` elements (1250 total) with child `<title>`, `<price currency="EUR">`, `<inv:stock warehouse="">`, optional `<inv:recall batch="">`.
- **Namespaces** – `urn:example:catalog` for catalog structure, `urn:example:inventory` for `<inv:stock>` and `<inv:recall>` elements.

#### Data flow
1. **Input** – static XML file loaded at application start.
2. **Processing** – services read `<product>` entries, extract SKU, title, price, and inventory per warehouse.
3. **Output** – data exposed via APIs or internal services for product display, pricing, and stock checks.

#### Dependencies
- Relies on XML namespaces `urn:example:catalog` and `urn:example:inventory`.
- No external code or database; all data is embedded in the XML file.

#### Cross-cutting
- Consistent use of `<inv:stock>` for inventory and optional `<inv:recall>` for recall status.
- All prices use `currency="EUR"` attribute, ensuring uniform currency handling.
