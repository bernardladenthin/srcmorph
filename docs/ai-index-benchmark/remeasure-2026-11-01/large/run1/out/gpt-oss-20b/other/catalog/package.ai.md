### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:37:46Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A structured XML catalog that lists products by category, including pricing and per‑warehouse inventory.

#### Purpose
- Provides a static product catalog for an e‑commerce system.
- Supplies product metadata and inventory levels for downstream services.

#### Responsibilities
- **Catalog definition** – declares categories and products with identifiers.
- **Pricing** – stores product prices in EUR.
- **Inventory tracking** – records stock quantities per warehouse.

#### Key units
- **`<catalog>`** – root element, versioned, namespace `urn:example:catalog`.
- **`<category>`** – group of products, identified by `id` and `name`.
- **`<product>`** – individual item, identified by `sku`; contains `<title>`, `<price>`, and `<inv:stock>`.
- **`<price>`** – holds monetary value, attribute `currency="EUR"`.
- **`<inv:stock>`** – inventory element, attribute `warehouse`, text quantity.

#### Data flow
- Consumers read the XML file, parse `<category>` → `<product>` → `<price>` and `<inv:stock>` to obtain product details and stock levels for display or order processing.

#### Dependencies
- XML namespaces: `urn:example:catalog` (default) and `urn:example:inventory` (prefix `inv`).
- No external schemas or XSLT templates referenced.

#### Cross-cutting
- Consistent use of attributes (`id`, `sku`, `currency`, `warehouse`) for identification and metadata.
- All monetary values are in EUR, ensuring uniform currency handling.
