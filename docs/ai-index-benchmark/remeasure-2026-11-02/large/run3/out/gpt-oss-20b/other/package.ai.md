### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:10:49Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A structured XML catalog that defines products, prices, and per‑warehouse inventory for an e‑commerce platform.

#### Purpose
- Store product listings, pricing, and stock levels.  
- Enable downstream services (pricing, fulfillment) to query catalog data.

#### Responsibilities
- **Product definition** – SKU, title, price, optional recall flag.  
- **Inventory tracking** – stock quantities per warehouse.  
- **Catalog organization** – hierarchical categories with id/name metadata.

#### Key units
- **`catalog`** – root element, declares version and namespaces.  
- **`category`** – id/name pairs (≈80).  
- **`product`** – sku attribute; contains `title`, `price` (EUR), optional `inv:recall`.  
- **`inv:stock`** – inventory element with `warehouse` code (`W1`–`W9`) and `quantity`.

#### Data flow
1. XML file is loaded by the e‑commerce system.  
2. Parser processes `catalog` → iterates `category` → iterates `product`.  
3. For each product, reads `price` and all `inv:stock` entries.  
4. Structured catalog data is exposed to pricing and fulfillment services.

#### Dependencies
- Default namespace `urn:example:catalog`.  
- Inventory namespace `urn:example:inventory` (prefix `inv`).  
- No external schemas or imports.

#### Cross-cutting
- Uniform use of `sku`, `id`, `name` attributes.  
- Currency fixed to `EUR` in all `price` elements.  
- Warehouse codes (`W1`–`W9`) consistently referenced in `inv:stock`.
