### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:53:41Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A product catalog that groups items by category, lists each product’s SKU, title, price in EUR, and inventory stock per warehouse, with optional recall data.

#### Purpose
- Stores product listings for an e‑commerce catalog.  
- Provides inventory levels per warehouse.

#### Type
- XML data file.  
- Root element: `catalog` (namespace `urn:example:catalog`).  
- Declares namespace prefix `inv` → `urn:example:inventory`.

#### Definitions
- **Categories**: `category` elements with attributes `id` and `name`.  
- **Products**: `product` elements with attribute `sku`.  
  - Child elements:  
    - `title` – product name.  
    - `price` – element with attribute `currency="EUR"` and numeric value.  
    - `inv:stock` – element in the `inv` namespace with attribute `warehouse` (`W1`–`W9`) and quantity value.  
    - Optional `inv:recall` – element with attribute `batch` and text “supplier recall”.  

#### Configuration
- `catalog` attribute `version="3"`.  
- All `price` elements use currency `"EUR"`.  
- `inv:stock` elements specify warehouse codes (`W1`–`W9`) and quantity values.

#### Dependencies
- Namespace `urn:example:catalog` (default).  
- Namespace `urn:example:inventory` (prefix `inv`).  
- No external schemas, references, or placeholders.
