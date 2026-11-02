### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:19Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Organizes EUR-priced products by category and records warehouse inventory and selected supplier recall notices.

#### Purpose
- Provides a categorized catalog of 5,000 SKU-identified products across 40 categories.
- Stores product prices, stock quantities, and recall details for selected products.

#### Responsibilities
- Groups products under category IDs `cat-001`–`cat-040`.
- Associates each product with a title, EUR price, and warehouse stock quantity.
- Tracks supplier recall text and batch identifiers for 250 products in Categories 20 and 21.

#### Key units
- `catalog` — version `3` root element containing category records.
- `category` — category ID and name grouping 125 products.
- `product` — SKU-keyed record with title, price, and inventory data.
- `inv:stock` — warehouse-coded stock quantity.
- `inv:recall` — batch-identified supplier recall notice.

#### Dependencies
- `urn:example:catalog` — default namespace for catalog records.
- `urn:example:inventory` — namespace for `inv:stock` and `inv:recall`.

#### Cross-cutting
- Product SKUs follow `SKU-NNN-NNNN`; stock quantities can be zero.
- Prices use `EUR`; warehouse codes shown are `W1`–`W9`.
