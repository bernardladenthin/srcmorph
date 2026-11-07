### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:18:36Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: F19D8599
- N: 712EFF34
- M: 2FA139DC
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A comprehensive product inventory catalog for a multi‑warehouse e‑commerce platform, organized by category and containing detailed pricing and stock information.

#### Purpose
- Stores product inventory data for a multi‑warehouse e‑commerce system.

#### Type
- XML catalog file  
  - Root element: `catalog` (namespace `urn:example:catalog`)  
  - Namespace prefix: `inv` → `urn:example:inventory` (or `http://example.com/inventory` in later fragments)  
  - Attribute: `version="3"`

#### Definitions
- **Categories**: 40 (`cat-001` … `cat-040`)  
  - Each `<category>` has attributes `id` and `name`.  
  - Contains a sequence of `<product>` elements.  
- **Products**: 1250 total (≈140 per category)  
  - `<product>` attributes: `sku` (e.g., `SKU-001-0001`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price currency="EUR">` – numeric value.  
    - `<inv:stock warehouse="W#">` – integer quantity per warehouse (`W1`–`W9`).  
    - Optional `<inv:recall batch="...">` for recalled items.  
- **Warehouses**: `W1`–`W9`, referenced in `inv:stock` elements.

#### Configuration
- No configurable values; all data is literal.

#### Dependencies
- Namespace declarations:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"` (or `http://example.com/inventory` in some fragments)  
- No external references or includes.
