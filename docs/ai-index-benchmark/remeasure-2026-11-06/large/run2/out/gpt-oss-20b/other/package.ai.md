### other
- H: 1.0
- C: A9F55380
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:15:04Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 507CDE24
- F: [catalog/](catalog/package.ai.md)
---
> A structured XML product catalog that supplies SKU, pricing, inventory, and recall data to downstream e‑commerce and warehouse systems.

#### Purpose
- Provide a single source of truth for product, price, stock, and recall information.

#### Responsibilities
- **Catalog definition** – root `catalog` element grouping categories and products.  
- **Category management** – 26 categories identified by `id` and `name`.  
- **Product listing** – 1 250 products with SKU, title, price, and stock details.  
- **Inventory tracking** – per‑warehouse stock quantities (`W1`–`W9`).  
- **Recall handling** – optional recall batch data for category 20 products.

#### Key units
- **`catalog`** – root element containing all categories and products.  
- **`category`** – element with `id` and `name` attributes.  
- **`product`** – element with `sku` attribute, child `<title>`, `<price>`, and `<inv:stock>` elements.  
- **`inv:stock`** – inventory element with `warehouse` attribute and quantity value.  
- **`inv:recall`** – optional recall element with `batch` attribute.

#### Data flow
1. **Input** – XML file `catalog.xml` loaded by an application or service.  
2. **Parsing** – XML parser reads `catalog`, iterates categories → products → stock.  
3. **Output** – Structured data objects (e.g., product DTOs) containing SKU, title, price, warehouse stock map, and optional recall batch.  
4. **Consumption** – Data supplied to pricing, inventory, or order‑processing modules.

#### Dependencies
- **Namespaces**  
  - `urn:example:catalog` – defines catalog, category, product, and price elements.  
  - `urn:example:inventory` – defines `inv:stock` and `inv:recall` elements.  
- **External systems** – Any consumer of the catalog (e‑commerce front‑end, warehouse management, recall notification services).

#### Cross-cutting
- **Currency consistency** – All `<price>` elements use `currency="EUR"`.  
- **Warehouse identifiers** – Standard set `W1`–`W9` used across all stock entries.  
- **Recall batch convention** – `batch` attribute format `R-020-xxxx` for category 20 products.
