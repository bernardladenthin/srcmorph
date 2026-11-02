### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:13Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A single XML catalog that stores product inventory, pricing, and recall information for an e‑commerce platform.

#### Purpose
- Central repository of product data for catalog, inventory, and recall tracking.

#### Responsibilities
- **Catalog definition** – defines categories and products with SKU, title, price, and stock.
- **Inventory tracking** – per‑warehouse stock levels via `<inv:stock>`.
- **Recall management** – optional recall batch data via `<inv:recall>`.

#### Key units
- **`catalog`** (root element, namespace `urn:example:catalog`) – holds all categories and products.
- **`category`** – attributes `id`, `name`; groups products.
- **`product`** – attributes `sku`; children `<title>`, `<price>`, `<inv:stock>`, optional `<inv:recall>`.
- **`price`** – attribute `currency="EUR"`; numeric value.
- **`inv:stock`** – namespace `http://example.com/inventory`; attributes `warehouse`; quantity text.
- **`inv:recall`** – namespace `http://example.com/inventory`; attributes `batch`; recall text.

#### Data flow
- Catalog XML is read by the e‑commerce system → categories and products are parsed → price and stock are extracted per warehouse → optional recall data is flagged for affected products.

#### Dependencies
- Namespace `urn:example:catalog` for catalog structure.
- Namespace `http://example.com/inventory` (or `urn:example:inventory`) for inventory elements.
- No external schemas, XSDs, or XSLT templates referenced.

#### Cross-cutting
- Consistent use of `SKU` as unique product identifier.
- Prices uniformly in EUR.
- Stock quantities are integers.
- Recall batch IDs follow pattern `R-<category>-<number>`.
