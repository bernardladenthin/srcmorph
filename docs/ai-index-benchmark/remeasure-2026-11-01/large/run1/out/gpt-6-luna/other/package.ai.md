### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:15Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> Maintains a versioned product catalog with category-based listings, EUR pricing, warehouse stock, and supplier-recall details.

#### Purpose
- Organizes catalog products and inventory by category.
- Records recall information for applicable products.

#### Responsibilities
- Catalog organization: groups products under categories with identifiers and names.
- Product and inventory records: associates SKUs with titles, prices, stock quantities, warehouse identifiers, and optional recall details.

#### Key units
- `catalog` — versioned root containing category records.
- `category` — groups products and carries `id` and `name` attributes.
- `product` — identifies a listing by SKU and contains product details.
- `price` — stores an amount and currency.
- `inv:stock` — records stock quantity and warehouse.
- `inv:recall` — carries a batch identifier and recall text.

#### Data flow
- Catalog data is structured as `catalog` → `category` → `product`, with pricing and inventory details on each product and optional recall information.

#### Dependencies
- Default namespace: `urn:example:catalog`.
- Inventory namespace `inv` (`urn:example:inventory`) supplies `inv:stock` and `inv:recall`.

#### Cross-cutting
- Catalog version is `3`; prices use `EUR`, and stock records identify warehouses `W1` through `W9`.
- Stock quantities may be zero.
