### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:36:39Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Provides a categorized product catalog with EUR pricing, warehouse stock levels, and supplier-recall records for selected categories.

#### Purpose
- Stores 5,000 product records across 40 categories, including titles, prices, and inventory quantities.
- Identifies recalled products in Categories 20 and 21.

#### Responsibilities
- Organizes products under `category` records, with each category containing 125 products.
- Associates each product with a SKU, title, EUR price, and warehouse stock; adds recall details for products in `cat-020` and `cat-021`.

#### Key units
- `catalog` — version 3 root containing the category records.
- `category` — groups product records from `cat-001` through `cat-040`.
- `product` — identifies an item by SKU and contains its title, price, and inventory data.
- `inv:stock` — records warehouse and stock quantity.
- `inv:recall` — records a batch identifier and `supplier recall` text.

#### Dependencies
- Uses the `urn:example:catalog` default namespace and the `urn:example:inventory` namespace for inventory elements.

#### Cross-cutting
- All `price` records use `currency="EUR"`.
- Recall records occur only in Categories 20 and 21.
