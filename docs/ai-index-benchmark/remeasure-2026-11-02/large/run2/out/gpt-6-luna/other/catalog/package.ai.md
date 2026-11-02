### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:16Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Maintains a categorized EUR-priced product catalog with warehouse stock levels and supplier recall notices for selected products.

#### Purpose
- Defines 40 product categories containing 5,000 SKU-identified products.
- Records product pricing, inventory quantities, and recall details for products in Categories 20 and 21.

#### Responsibilities
- Catalog organization: groups products by category IDs `cat-001`–`cat-040`.
- Product records: associates each SKU with a title, EUR price, and warehouse stock quantity.
- Recall tracking: stores supplier recall text and batch identifiers for 250 products in Categories 20 and 21.

#### Key units
- `catalog` — version `3` root element containing category records.
- `category` — category ID and name grouping 125 products.
- `product` — SKU-keyed record with title, price, and inventory data.
- `inv:stock` — warehouse-coded stock quantity; warehouse codes shown are `W1`–`W9`.
- `inv:recall` — batch-identified supplier recall notice.

#### Dependencies
- `urn:example:inventory` namespace qualifies `inv:stock` and `inv:recall`.
- `urn:example:catalog` is the default namespace.

#### Cross-cutting
- Product SKUs follow `SKU-NNN-NNNN`; stock quantities can be zero.
- Prices use currency `EUR`; category IDs span `cat-001`–`cat-040`.
