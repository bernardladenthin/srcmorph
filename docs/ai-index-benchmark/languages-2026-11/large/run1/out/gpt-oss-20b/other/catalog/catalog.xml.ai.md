### catalog.xml
- H: 1.0
- C: 754E8810
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T01:06:24Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20041; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

> Catalog of products organized by category, each product with SKU, title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Stores product listings with inventory per warehouse for an e‑commerce catalog.

#### Type
- XML data file  
  - Root element: `<catalog>`  
  - Namespace: `inv` → `http://example.com/inventory` (declared on `<inv:stock>` elements)

#### Definitions
- **Categories**: `<category id="…" name="…">`  
  - Contains multiple `<product>` elements.
- **Products**: `<product sku="…">`  
  - `<title>`: product name.  
  - `<price currency="EUR">`: numeric value.  
  - `<inv:stock warehouse="Wn">`: integer quantity per warehouse.
- **Warehouses**: referenced by `warehouse` attribute values `W1`–`W9`.

#### Configuration
- No configurable properties or placeholders; all values are literal data.

#### Dependencies
- Namespace prefix `inv` bound to `http://example.com/inventory` used for `<inv:stock>` elements.
