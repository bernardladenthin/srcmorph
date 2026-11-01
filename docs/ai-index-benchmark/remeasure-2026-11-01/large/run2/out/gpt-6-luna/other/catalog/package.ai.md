### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:51:51Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Provides a versioned product catalog with EUR pricing, warehouse inventory, and supplier-recall information.

#### Purpose
- Stores products organized by numbered categories, with SKU, title, price, and stock data.
- Records supplier recalls for products in categories `cat-020` and `cat-021`.

#### Responsibilities
- Catalog data: groups products under categories `cat-001` through `cat-040`.
- Product and inventory details: associates product identifiers and prices with warehouse stock quantities and, where present, recall batches.

#### Key units
- `catalog`: XML root element with version `3` and repeated `category` entries.
- `category`: identified by `id` and `name`, containing products.
- `product`: identified by `sku`, with `title`, `price`, and `inv:stock`; products in `cat-020` and `cat-021` also have `inv:recall`.

#### Data flow
- Categories group product records; each product carries pricing and stock data, with recall details added for the specified categories.

#### Dependencies
- XML namespaces: `urn:example:catalog` (default) and `urn:example:inventory` (`inv` prefix).
- No referenced files, schemas, or external systems are shown.

#### Cross-cutting
- Visible prices use currency `EUR`; stock records identify warehouses `W1` through `W9` and may have quantity `0`.
- Recall records use category-specific batch IDs, including `R-020-0001`–`R-020-0125` and `R-021-0001`–`R-021-0125`.
