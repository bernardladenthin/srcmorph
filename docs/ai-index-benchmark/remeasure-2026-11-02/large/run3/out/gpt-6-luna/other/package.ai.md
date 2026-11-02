### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:33Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Organizes a 40-category product catalog with EUR pricing, warehouse inventory, and supplier-recall details for selected products.

#### Purpose
- Stores product, pricing, and stock data across 40 categories.
- Records batch-specific supplier recalls for products in categories 20 and 21.

#### Responsibilities
- **Catalog and pricing:** Groups 125 products per category, each identified by SKU and carrying a title and EUR price.
- **Inventory and recalls:** Associates products with stock quantities at warehouses W1–W9 and, in categories `cat-020` and `cat-021`, supplier-recall records by batch.

#### Key units
- `catalog` — Version 3 root containing categories.
- `category` — `id`- and `name`-identified group of products.
- `product` — SKU-keyed product record with title, price, and inventory data.
- `inv:stock` — Warehouse-specific inventory quantity record.
- `inv:recall` — Batch-specific supplier-recall record with recall text.

#### Data flow
- Categories group products; each product carries price and warehouse stock data, with recall details on products in `cat-020` and `cat-021`.

#### Dependencies
- Uses the `urn:example:inventory` namespace for `inv:stock` and `inv:recall`.
