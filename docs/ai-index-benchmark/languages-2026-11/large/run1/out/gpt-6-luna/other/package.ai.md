### other
- H: 1.0
- C: AC984AE0
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:38:11Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Represents categorized products with EUR prices and stock quantities tracked by warehouse.

#### Purpose
- Organizes product identities, titles, prices, and inventory by category.
- Combines catalog data with warehouse-specific stock records.

#### Responsibilities
- Groups products under categories identified by `id` and `name`.
- Records product SKU, title, EUR price, and stock quantities by warehouse.

#### Key units
- `catalog` — version `3` root element containing categories.
- `category` — grouping element with category identifiers and names.
- `product` — product record containing SKU, title, price, and stock.
- `price` — product price marked with `currency="EUR"`.
- `inv:stock` — inventory quantity associated with a warehouse.

#### Data flow
- `catalog` contains `category` elements, which contain `product` records with descriptive, pricing, and warehouse stock data.

#### Dependencies
- `urn:example:catalog` — default catalog namespace.
- `urn:example:inventory` — namespace for `inv:stock`.
- Warehouse identifiers include `W1` through `W9`; quantities may be zero.
