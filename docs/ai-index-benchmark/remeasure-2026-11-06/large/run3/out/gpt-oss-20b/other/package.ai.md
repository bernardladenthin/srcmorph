### other
- H: 1.0
- C: A9F55380
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:20:55Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: A6638783
- F: [catalog/](catalog/package.ai.md)
---
> A multi‑warehouse e‑commerce product inventory catalog expressed in XML, detailing categories, products, pricing, and per‑warehouse stock levels.

#### Purpose
- Central repository of product data for an e‑commerce platform.  
- Supports multi‑warehouse stock tracking and product categorization.

#### Responsibilities
- **Catalog organization** – groups products into 40 categories (`cat-001` … `cat-040`).  
- **Product definition** – records SKU, title, price (EUR), and stock per warehouse.  
- **Stock management** – tracks inventory levels across 9 warehouses (`W1`–`W9`).  
- **Recall handling** – optional recall information per product batch.

#### Key units
- **`catalog` element** – root, namespace `urn:example:catalog`, version `3`.  
- **`category` elements** – attributes `id`, `name`; contain multiple `product` children.  
- **`product` elements** – attribute `sku`; children: `title`, `price`, `inv:stock`, optional `inv:recall`.  
- **`inv:stock` elements** – attribute `warehouse`; integer quantity.  
- **`inv:recall` elements** – attribute `batch`; marks recalled items.

#### Data flow
- Static XML file is read by the e‑commerce system to populate product catalogs, inventory dashboards, and order‑processing services.  
- Each `product` is linked to its category and warehouse stock, enabling real‑time availability checks.

#### Dependencies
- XML namespaces:  
  - `xmlns="urn:example:catalog"` (root)  
  - `xmlns:inv="urn:example:inventory"` (or `http://example.com/inventory` in some fragments).  
- No external includes or XSDs; the file is self‑contained.

#### Cross-cutting
- Consistent use of `inv:` prefix for inventory‑specific elements.  
- Uniform attribute naming (`id`, `sku`, `warehouse`, `batch`).  
- All prices use `currency="EUR"`; stock quantities are integers.
