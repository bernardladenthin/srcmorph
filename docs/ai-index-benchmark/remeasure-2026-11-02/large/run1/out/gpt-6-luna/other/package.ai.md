### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:36:43Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Organizes a 5,000-item product catalog with category-based EUR pricing, warehouse inventory, and supplier-recall details for selected products.

#### Purpose
- Groups products into 40 categories, with 125 products per category.
- Records item identifiers, titles, prices, stock quantities, and selected recall information.

#### Responsibilities
- Maintains product and category records from `cat-001` through `cat-040`.
- Associates products in `cat-020` and `cat-021` with supplier-recall details.

#### Key units
- `catalog` — version 3 root containing category records.
- `category` — groups product records.
- `product` — stores an item’s SKU, title, price, and inventory data.
- `inv:stock` — records warehouse and stock quantity.
- `inv:recall` — records a batch identifier and supplier-recall text.

#### Dependencies
- Uses the `urn:example:catalog` default namespace and `urn:example:inventory` namespace.

#### Cross-cutting
- All `price` records use `currency="EUR"`.
- Recall records appear only in Categories 20 and 21.
