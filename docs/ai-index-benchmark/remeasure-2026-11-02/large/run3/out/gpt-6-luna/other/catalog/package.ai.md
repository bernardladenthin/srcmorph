### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:29Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Provides a category-organized product catalog with EUR prices, warehouse stock levels, and supplier-recall records.

#### Purpose
- Stores product, pricing, and inventory data across 40 categories.
- Identifies recalled products in categories 20 and 21.

#### Responsibilities
- **Catalog and pricing:** Groups 125 products per category, each with a SKU, title, and EUR price.
- **Inventory and recalls:** Records stock by warehouse and adds batch-specific supplier-recall data for products in categories 20 and 21.

#### Key units
- `catalog` — Version 3 root containing 40 categories.
- `category` — Category identified by `id` and `name`, containing product records.
- `product` — SKU-keyed record with title, price, and inventory data.
- `inv:stock` — Inventory quantity record associated with a warehouse (`W1`–`W9`).
- `inv:recall` — Recall record with a batch identifier and supplier-recall text.

#### Data flow
- Categories group products; each product carries pricing and warehouse stock data, with recall details present for products in `cat-020` and `cat-021`.

#### Dependencies
- Uses the `urn:example:inventory` namespace for `inv:stock` and `inv:recall`.
