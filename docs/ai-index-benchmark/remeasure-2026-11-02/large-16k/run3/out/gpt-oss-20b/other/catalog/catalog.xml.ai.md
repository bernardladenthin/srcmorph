### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:39:10Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 6 of 6 chunks summarized

> A single XML catalog that lists thousands of products, each with SKU, title, price, and per‑warehouse inventory, organized into numbered categories.

#### Purpose
- Stores product listings for an e‑commerce catalog.  
- Provides pricing and stock information for each SKU.

#### Type
- XML data file (`catalog.xml`).  
- Root element: `catalog` (namespace `urn:example:catalog`).  
- Declares namespace prefix `inv` for `urn:example:inventory` (used on `<inv:stock>` and `<inv:recall>`).

#### Definitions
- **Categories**: 40 (`cat-001` … `cat-040`), each with `id` and `name`.  
- **Products**: 1250 total (≈140 per category).  
- **Product attributes**:  
  - `sku` (unique identifier).  
  - `<title>` (product name).  
  - `<price>` with `currency="EUR"` and numeric value.  
  - `<inv:stock>` with `warehouse` attribute and quantity value.  
  - Optional `<inv:recall>` with `batch` attribute and text `supplier recall` (present only in Category 20 products).  

#### Configuration
- No configurable properties or placeholders; all values are literal data.

#### Dependencies
- Namespace `urn:example:catalog` (root).  
- Namespace `urn:example:inventory` (used for `<inv:stock>` and `<inv:recall>`).
