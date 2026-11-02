### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:11Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> Provides a versioned product catalog with category-grouped listings, EUR prices, warehouse inventory, and supplier-recall information.

#### Purpose
- Stores product and inventory data across numbered catalog categories.
- Records supplier recalls for visible products in Categories 20 and 21.

#### Responsibilities
- Catalog organization: groups products by category, with category identifiers and names.
- Product details: associates each SKU with a title, price, and stock; some products also include recall information.

#### Key units
- `catalog` — versioned root containing category records.
- `category` — groups products and carries `id` and `name` attributes.
- `product` — identifies a listing by SKU and contains title, price, and inventory data.
- `price` — stores an amount with a currency attribute.
- `inv:stock` — records stock quantity and warehouse.
- `inv:recall` — carries a batch identifier and recall text.

#### Data flow
- Catalog data is structured as `catalog` → `category` → `product`, with each product containing pricing and inventory details and optionally recall information.

#### Dependencies
- Default namespace `urn:example:catalog`.
- Inventory namespace `inv` mapped to `urn:example:inventory`, used by `inv:stock` and `inv:recall`.

#### Cross-cutting
- Catalog version is `3`; prices use `EUR`, and stock records identify warehouses `W1` through `W9`.
- Stock quantities may be zero; summarized excerpts include partial product and category records.
