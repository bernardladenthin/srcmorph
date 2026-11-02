### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:10:46Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A catalog XML file that defines product listings, prices, and warehouse inventory levels for an e‑commerce platform.

#### Purpose
- Stores product data for an e‑commerce catalog.  
- Provides inventory quantities per warehouse.

#### Responsibilities
- **Product definition** – SKU, title, price, and optional recall information.  
- **Inventory tracking** – stock quantities per warehouse.  
- **Catalog organization** – categories with id/name metadata.

#### Key units
- **`catalog`** (root) – holds version and namespace declarations.  
- **`category`** – id/name pairs (80 total).  
- **`product`** – sku attribute; child elements: `title`, `price` (currency EUR), `inv:stock` (warehouse, quantity), optional `inv:recall`.  
- **`inv:stock`** – inventory element with warehouse code (`W1`–`W9`) and quantity.

#### Data flow
Input: XML file loaded by an e‑commerce system.  
Processing: Parse `catalog` → iterate categories → iterate products → read price and stock per warehouse.  
Output: Structured product catalog with inventory levels for downstream services (pricing, fulfillment).

#### Dependencies
- Namespace `urn:example:catalog` (default).  
- Namespace `urn:example:inventory` (prefix `inv`).  
- No external schemas or imports.

#### Cross-cutting
- Consistent use of `sku`, `id`, and `name` attributes across elements.  
- Currency standardization (`EUR`) in all `price` elements.  
- Warehouse codes (`W1`–`W9`) uniformly referenced in `inv:stock`.
