### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:15:00Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: FD86E9A6
- F: [catalog.xml](catalog.xml.ai.md)
---
> A structured XML product catalog providing SKU, title, EUR price, per‑warehouse inventory, and recall batch data for e‑commerce or warehouse systems.

#### Purpose
- Deliver comprehensive product, pricing, and inventory information to downstream services.

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
