### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:12:50Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Defines a versioned product catalog with categorized product details, EUR prices, warehouse inventory, and supplier recall information.

#### Purpose
- Describes catalog version 3, organizing products by category with identifiers, titles, prices, and inventory details.
- Captures optional supplier recall information for affected product batches.

#### Responsibilities
- **Catalog and products:** Describes categories `cat-001`–`cat-040` and product fields including `sku`, `title`, and `price`.
- **Inventory and recalls:** Describes warehouse stock (`inv:stock`) and optional batch recall notices (`inv:recall`).

#### Key units
- `catalog.xml` — catalog data description; reports catalog version, categories, product ranges, pricing, stock, and recall details.
- `inv:stock` — namespaced inventory element with warehouse information.
- `inv:recall` — namespaced recall element with batch information.

#### Data flow
- Product records are described within categories, with prices in EUR and stock associated with warehouses `W1`–`W9`; some records may include supplier recall batches.

#### Dependencies
- XML namespaces `urn:example:catalog` and `urn:example:inventory`.
- Catalog metadata specifies EUR pricing and warehouses `W1`–`W9`; recall batches include `R-020-*` and `R-021-*`.

#### Cross-cutting
- Product identifiers use SKU-style names; category identifiers use `cat-###`.
- The summary notes an incomplete final described product record, `SKU-036-0116`.
