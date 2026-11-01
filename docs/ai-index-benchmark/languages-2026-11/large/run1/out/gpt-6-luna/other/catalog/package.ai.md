### other/catalog
- H: 1.0
- C: 16329263
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:38:07Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Organizes categorized product listings with EUR pricing and warehouse-specific stock quantities.

#### Purpose
- Supplies product identities, titles, prices, and inventory grouped by category.
- Combines catalog records with stock data from the inventory namespace.

#### Responsibilities
- Category grouping: `category` elements identify categories by `id` and `name`.
- Product details: `product` records carry SKU, title, and currency-tagged price.
- Inventory representation: `inv:stock` records associate quantities with warehouse identifiers.

#### Key units
- `catalog` — root element with version `3`, containing categories.
- `category` — groups products and provides category identifiers and names.
- `product` — identifies a product by SKU and contains its title, price, and stock.
- `price` — product price, visibly using `currency="EUR"`.
- `inv:stock` — inventory quantity associated with a warehouse.

#### Data flow
- `catalog` groups `category` records; each category contains products whose descriptive, pricing, and warehouse stock data are represented in nested elements.

#### Dependencies
- `urn:example:catalog` — default catalog namespace.
- `urn:example:inventory` — namespace for `inv:stock`.
- Visible warehouse identifiers range from `W1` to `W9`; quantities can be zero.
