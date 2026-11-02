### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:15:34Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> Catalog of products organized by category, each product with title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Stores product catalog data for an e‑commerce system.
- Provides product metadata (SKU, title, price, currency) and inventory levels per warehouse.

#### Type
- XML data file  
  - Root element: `<catalog>`  
  - Namespace declarations:  
    - `xmlns="urn:example:catalog"` (default)  
    - `xmlns:inv="urn:example:inventory"`  
  - Attribute: `version="3"`

#### Definitions
- **Categories** (`<category>`):  
  - Attributes: `id`, `name`  
  - Contains multiple `<product>` elements.
- **Products** (`<product>`):  
  - Attribute: `sku` (e.g., `SKU-001-0001`)  
  - Child elements:  
    - `<title>` – product name  
    - `<price>` – contains `currency="EUR"` and numeric value  
    - `<inv:stock>` – inventory element in `inv` namespace, attributes: `warehouse` (e.g., `W8`) and text value (stock quantity).
- **Inventory** (`inv:stock`):  
  - Namespace prefix `inv` bound to `urn:example:inventory`.

#### Configuration
- No configurable properties or placeholders; all values are literal.

#### Dependencies
- Relies on two XML namespaces:  
  - `urn:example:catalog` (default)  
  - `urn:example:inventory` (prefix `inv`)  
- No external schemas or DTDs referenced.
