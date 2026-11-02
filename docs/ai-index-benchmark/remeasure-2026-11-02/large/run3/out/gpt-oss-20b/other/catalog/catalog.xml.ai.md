### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:09:55Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A catalog XML that defines product listings, prices, and warehouse inventory levels for an e‑commerce platform.

#### Purpose
- Stores product data for an e‑commerce catalog.  
- Provides inventory quantities per warehouse.

#### Type
- XML data file.  
- Root element: `catalog` (default namespace `urn:example:catalog`).  
- Declares namespace prefix `inv` → `urn:example:inventory`.

#### Definitions
- **Categories**: `category` elements with attributes `id` and `name`.  
  - Family: 80 `id`/`name` pairs across the file.  
- **Products**: `product` elements with attribute `sku`.  
  - Child elements:  
    - `title` – product name.  
    - `price` – contains attribute `currency="EUR"` and a numeric value.  
    - `inv:stock` – contains attribute `warehouse` (values `W1`–`W9`) and a quantity value.  
    - Optional `inv:recall` – contains attribute `batch` and a text description.  
  - Family: 20291 total elements, including all nested `price`, `title`, `inv:stock`, and optional `inv:recall` elements.

#### Configuration
- `catalog` attribute `version="3"`.  
- `price` elements use currency `"EUR"`.  
- `inv:stock` elements specify warehouse codes (`W1`–`W9`) and quantity values.

#### Dependencies
- Namespace `urn:example:catalog` (default).  
- Namespace `urn:example:inventory` (prefix `inv`).  
- No external schemas, imports, or references.
