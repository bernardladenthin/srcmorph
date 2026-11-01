### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:12:54Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Defines a categorized product catalog with EUR pricing, warehouse stock, and optional supplier recall details.

#### Purpose
- Describes catalog version 3 and its product, category, pricing, and inventory data.
- Records optional recall notices for affected product batches.

#### Responsibilities
- **Product catalog:** Organizes SKU-identified products with titles and prices under categories `cat-001`–`cat-040`.
- **Inventory and recalls:** Associates warehouse stock with products and describes optional batch recall information.

#### Key units
- `catalog.xml` — describes catalog metadata, categories, product ranges, prices, stock, and recalls.
- `inv:stock` — namespaced warehouse inventory element.
- `inv:recall` — namespaced batch recall element.

#### Data flow
- Product records are grouped by category, priced in EUR, and associated with warehouse stock at `W1`–`W9`; some records include supplier recall batches.

#### Dependencies
- XML namespaces `urn:example:catalog` and `urn:example:inventory`.
- Catalog metadata uses EUR pricing and warehouses `W1`–`W9`; recall batch identifiers include `R-020-*` and `R-021-*`.

#### Cross-cutting
- Category IDs follow `cat-###`; product identifiers use SKU-style names.
- The final described product record, `SKU-036-0116`, is incomplete.
