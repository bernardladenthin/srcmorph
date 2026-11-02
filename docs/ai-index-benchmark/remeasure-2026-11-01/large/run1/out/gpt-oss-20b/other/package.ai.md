### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:37:58Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A static XML product catalog that supplies e‑commerce services with product metadata, pricing in EUR, and per‑warehouse inventory levels.

#### Purpose
- Deliver a read‑only product catalog for downstream services.
- Provide consistent product, price, and stock data for display and order processing.

#### Responsibilities
- **Catalog structure** – defines categories and products with unique identifiers.
- **Pricing** – stores product prices in a single currency (EUR).
- **Inventory** – tracks stock quantities per warehouse.

#### Key units
- **`<catalog>`** – root element, versioned, namespace `urn:example:catalog`.
- **`<category>`** – container for products, identified by `id` and `name`.
- **`<product>`** – individual item, identified by `sku`; contains `<title>`, `<price>`, and `<inv:stock>`.
- **`<price>`** – monetary value, attribute `currency="EUR"`.
- **`<inv:stock>`** – inventory element, attribute `warehouse`, text quantity.

#### Data flow
- Consumers parse the XML file, traverse `<category>` → `<product>` → `<price>` and `<inv:stock>` to extract product details and inventory levels for UI rendering or order validation.

#### Dependencies
- XML namespaces: `urn:example:catalog` (default) and `urn:example:inventory` (prefix `inv`).
- No external schemas or XSLT templates referenced.

#### Cross-cutting
- Uniform use of attributes (`id`, `sku`, `currency`, `warehouse`) for identification.
- All monetary values are expressed in EUR, ensuring consistent currency handling.
