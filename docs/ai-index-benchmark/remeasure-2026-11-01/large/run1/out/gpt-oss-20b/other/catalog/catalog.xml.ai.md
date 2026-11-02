### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:37:08Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> Catalog of products organized by category, each product with title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Defines product catalog for an e‑commerce system.
- Provides product metadata and inventory levels.

#### Type
- XML catalog file  
  - Root element: `<catalog>`  
  - Namespace: `urn:example:catalog` (default)  
  - Namespace prefix: `inv` → `urn:example:inventory`  
  - Attribute: `version="3"`

#### Definitions
- **Categories** (`<category>`):  
  - Attributes: `id`, `name`  
  - Contains multiple `<product>` elements.
- **Products** (`<product>`):  
  - Attribute: `sku`  
  - Child elements:  
    - `<title>` – product name.  
    - `<price>` – attributes: `currency="EUR"`, text value is price.  
    - `<inv:stock>` – attributes: `warehouse` (warehouse code), text value is quantity in stock.

#### Configuration
- No configurable properties or placeholders; all values are literal.

#### Dependencies
- Namespace `urn:example:catalog` (default).  
- Namespace `urn:example:inventory` (prefix `inv`).  
- No external files or schemas referenced.
