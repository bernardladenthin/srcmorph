### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:34:02Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A hierarchical XML catalog that stores product, pricing, and per‑warehouse inventory data for an e‑commerce system.

#### Purpose
- Persist product information, prices in EUR, and stock levels per warehouse.
- Support optional recall batch data for products.

#### Responsibilities
- **Catalog structure**: Organize products into categories with unique IDs and names.
- **Product details**: Provide SKU, title, price, and optional recall information.
- **Inventory tracking**: Record stock quantities per warehouse using the `inv` namespace.

#### Key units
- **`catalog`** (root element, namespace `urn:example:catalog`): holds the entire product hierarchy.
- **`category`** elements: top‑level grouping of products, each with `id` and `name`.
- **`product`** elements: contain `sku`, `<title>`, `<price>` (currency="EUR"), optional `<inv:recall>`.
- **`inv:stock`** elements: namespace `urn:example:inventory`, specify `warehouse` and quantity.

#### Data flow
- XML file is parsed: categories first, then each product within a category, extracting price and inventory per warehouse, optionally capturing recall batch details.

#### Dependencies
- Namespace `urn:example:catalog` (default).
- Namespace `urn:example:inventory` (prefix `inv`).
- No external schemas or XSLT templates referenced.

#### Cross-cutting
- Consistent use of the `inv` namespace for all inventory elements.
- Prices uniformly use the `EUR` currency attribute.
- Optional recall data follows the same structure across products.
