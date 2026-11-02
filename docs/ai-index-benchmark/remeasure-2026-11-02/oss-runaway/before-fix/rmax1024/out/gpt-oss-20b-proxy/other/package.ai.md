### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:18:13Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A single XML catalog file that stores product inventory data for an e‑commerce system, organized by category and warehouse.

#### Purpose
- Central repository of product inventory for an e‑commerce platform.

#### Responsibilities
- **Catalog definition** – declares categories, products, prices, and stock levels.  
- **Inventory tracking** – records warehouse‑specific stock quantities.  
- **Recall handling** – optional supplier recall information for specific products.

#### Key units
- **`catalog`** (root element) – contains all categories and products.  
- **`category`** – identified by `id` and `name`; groups related products.  
- **`product`** – identified by `sku`; includes `<title>`, `<price>`, and inventory elements.  
- **`inv:stock`** – inventory element with `warehouse` attribute and quantity value.  
- **`inv:recall`** – optional recall element with `batch` attribute and text “supplier recall”.

#### Data flow
- Static XML data is read by the e‑commerce system to populate product listings, pricing, and stock availability per warehouse.

#### Dependencies
- Namespace `xmlns="urn:example:catalog"` for catalog elements.  
- Namespace `xmlns:inv="urn:example:inventory"` for inventory elements (`inv:stock`, `inv:recall`).

#### Cross-cutting
- Consistent use of the `inv` namespace for all inventory‑related data.  
- Uniform attribute naming (`id`, `name`, `sku`, `warehouse`, `batch`).  
- Currency fixed to EUR in `<price>` elements.
