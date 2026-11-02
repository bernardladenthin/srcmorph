### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:39:24Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A structured XML catalog that holds detailed inventory, pricing, and recall data for an e‑commerce product line.

#### Purpose
- Persist product inventory, pricing, and recall information in a single XML document.

#### Responsibilities
- **Catalog definition** – root element and namespace handling.  
- **Category organization** – grouping products into 40 categories with IDs and names.  
- **Product representation** – SKU, title, EUR price, per‑warehouse stock, optional recall batch.

#### Key units
- **`catalog` element** – top‑level container, namespace `urn:example:catalog`.  
- **`category` elements** – `id` and `name` attributes, 40 instances.  
- **`product` elements** – `sku` attribute, child `<title>`, `<price currency="EUR">`, `<inv:stock>` per warehouse, optional `<inv:recall>`.  
- **`inv:stock` element** – namespace `http://example.com/inventory`, `warehouse` attribute, quantity value.  
- **`inv:recall` element** – namespace `http://example.com/inventory`, `batch` attribute, text “supplier recall”.

#### Data flow
- XML is read by an e‑commerce system to populate product listings, inventory counts per warehouse, and to flag recalled items for compliance checks.

#### Dependencies
- XML namespace `urn:example:catalog` for catalog structure.  
- XML namespace `http://example.com/inventory` for inventory and recall sub‑elements.

#### Cross-cutting
- Consistent use of the `inv` namespace for all inventory‑related data.  
- Uniform attribute naming (`sku`, `warehouse`, `batch`) across product elements.  
- All prices expressed in EUR via `currency="EUR"` attribute.
