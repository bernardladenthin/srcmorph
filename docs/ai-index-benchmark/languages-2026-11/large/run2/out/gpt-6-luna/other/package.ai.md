### other
- H: 1.0
- C: AC984AE0
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:16Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Maintains a versioned product catalog with category-based product details, EUR prices, and warehouse-specific inventory quantities.

#### Purpose
- Organizes catalog products by category and records their identifiers, titles, prices, and stock.
- Describes categories `cat-001`–`cat-040`, with incomplete or fragmentary data reported for categories 19 and 37.

#### Responsibilities
- Groups products into named categories and stores each product’s SKU, title, and price.
- Associates product stock quantities with warehouse codes.

#### Key units
- `catalog` — root element containing catalog version and categories.
- `category` — groups products and carries `id` and `name` attributes.
- `product` — carries a `sku` and contains title, price, and inventory data.
- `price` — stores a numeric amount with a `currency` attribute.
- `inv:stock` — stores a quantity with a `warehouse` attribute.

#### Data flow
- `catalog` → `category` → `product`; each product contains title and price data alongside inventory records.

#### Dependencies
- `urn:example:catalog` — default namespace for catalog data.
- `urn:example:inventory` — namespace for `inv:stock`.

#### Cross-cutting
- Catalog version is `3`; visible prices use `EUR`.
- Warehouse codes range from `W1` to `W9`; categories 1–18 are described as having 125 products each.
