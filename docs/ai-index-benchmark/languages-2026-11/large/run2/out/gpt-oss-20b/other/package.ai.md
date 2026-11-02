### other
- H: 1.0
- C: AC984AE0
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:23:15Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Provides a structured XML catalog of products, their prices, and warehouse inventory for an e‑commerce platform.

#### Purpose
- Store product data organized by category.
- Track inventory levels per warehouse.

#### Responsibilities
- **Catalog definition** – root `<catalog>` with versioning.
- **Category grouping** – `<category>` elements with `id` and `name`.
- **Product details** – `<product>` elements containing title, price, and SKU.
- **Inventory tracking** – `<inv:stock>` elements per warehouse.

#### Key units
- **`catalog`** – root element holding all categories, versioned.
- **`category`** – groups products, identified by `id` and `name`.
- **`product`** – represents an item, identified by `sku`; contains `<title>`, `<price>` (EUR), and `<inv:stock>`.
- **`inv:stock`** – inventory quantity per `warehouse` attribute.

#### Data flow
1. XML file is parsed into a catalog tree.
2. Each `<category>` is processed to collect its `<product>` children.
3. For each product, price and stock per warehouse are extracted.
4. The resulting data structure can be queried for product listings, pricing, or stock availability.

#### Dependencies
- Uses two XML namespaces:  
  - `urn:example:catalog` (default) for catalog elements.  
  - `urn:example:inventory` (`inv`) for stock elements.

#### Cross-cutting
- Consistent use of attributes (`id`, `name`, `sku`, `currency`, `warehouse`) across elements.  
- All monetary values are in EUR, enforced by the `currency="EUR"` attribute.  
- Inventory quantities are plain text numbers within `<inv:stock>`.
