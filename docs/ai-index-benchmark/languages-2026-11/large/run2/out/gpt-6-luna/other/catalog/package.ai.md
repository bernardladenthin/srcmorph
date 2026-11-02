### other/catalog
- H: 1.0
- C: 16329263
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:12Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Organizes versioned product and inventory data by category, with EUR pricing and warehouse-specific stock quantities.

#### Purpose
- Supplies catalog product titles, SKU identifiers, prices, and stock records.
- Describes categories `cat-001`–`cat-040`; records around categories 19 and 37 are reported as incomplete or fragmentary.

#### Responsibilities
- Groups products into named categories and records each product’s identifying and commercial data.
- Associates product stock quantities with warehouse codes.

#### Key units
- `catalog` — root element containing the catalog version and repeated `category` elements.
- `category` — groups repeated `product` elements and carries `id` and `name` attributes.
- `product` — carries a `sku` attribute and contains `title`, `price`, and `inv:stock`.
- `price` — stores a numeric amount and `currency` attribute.
- `inv:stock` — stores a quantity and `warehouse` attribute.

#### Data flow
- `catalog` → `category` → `product`; each product provides title and price data alongside inventory records.

#### Dependencies
- `urn:example:catalog` — default namespace for catalog data.
- `urn:example:inventory` — namespace for `inv:stock`.

#### Cross-cutting
- Catalog version is `3`; visible prices use `EUR`.
- Warehouse codes range from `W1` to `W9`; categories 1–18 are described as having 125 products each.
