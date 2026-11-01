### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:51:55Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Maintains a versioned product catalog with EUR pricing, warehouse stock levels, and supplier-recall records.

#### Purpose
- Organizes products into numbered categories with SKU, title, price, and inventory data.
- Records recall batches for products in categories `cat-020` and `cat-021`.

#### Responsibilities
- Groups products under categories `cat-001` through `cat-040`.
- Associates product prices and stock quantities with warehouses `W1` through `W9`; stock may be zero.
- Adds category-specific recall batch identifiers where present.

#### Key units
- `catalog`: XML root with version `3` and repeated `category` entries.
- `category`: Product group identified by `id` and `name`.
- `product`: Record identified by `sku`, with `title`, `price`, and `inv:stock`; some products also include `inv:recall`.

#### Data flow
- Categories contain product records, which carry pricing and stock data; recall details appear for products in `cat-020` and `cat-021`.

#### Dependencies
- XML namespaces: `urn:example:catalog` (default) and `urn:example:inventory` (`inv` prefix).
- No referenced files, schemas, or external systems are shown.

#### Cross-cutting
- Prices use `EUR`.
- Recall batches include `R-020-0001`–`R-020-0125` and `R-021-0001`–`R-021-0125`.
