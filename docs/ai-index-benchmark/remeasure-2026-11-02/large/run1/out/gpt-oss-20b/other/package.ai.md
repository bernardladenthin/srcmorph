### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:39:29Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A single XML document that defines product inventory, pricing, and recall status for an e‑commerce catalog.

#### Purpose
- Store product, category, and inventory data in a structured XML format.

#### Responsibilities
- **Catalog structure** – root element, namespaces, and overall layout.  
- **Category hierarchy** – 40 named categories with unique IDs.  
- **Product details** – SKU, title, price, per‑warehouse stock, and optional recall batch.

#### Key units
- **`catalog` element** – root, namespace `urn:example:catalog`.  
- **`category` elements** – attributes `id` and `name`.  
- **`product` elements** – attribute `sku`; children `<title>`, `<price currency="EUR">`, `<inv:stock>`, optional `<inv:recall>`.  
- **`inv:stock` element** – namespace `http://example.com/inventory`, attributes `warehouse`, quantity value.  
- **`inv:recall` element** – namespace `http://example.com/inventory`, attribute `batch`, text “supplier recall”.

#### Data flow
- E‑commerce systems parse the XML to build product listings, calculate inventory per warehouse, and flag recalled items for compliance.

#### Dependencies
- XML namespaces: `urn:example:catalog` for catalog layout; `http://example.com/inventory` for inventory and recall sub‑elements.

#### Cross-cutting
- Uniform use of the `inv` namespace for all inventory‑related data.  
- Consistent attribute names (`sku`, `warehouse`, `batch`).  
- Prices always expressed in EUR via `currency="EUR"`.
