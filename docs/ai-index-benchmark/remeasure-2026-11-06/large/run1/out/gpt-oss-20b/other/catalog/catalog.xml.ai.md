### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:08:05Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: F19D8599
- N: 712EFF34
- M: 2FA139DC
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A single XML data file that defines a retail product catalog, grouping items by category and detailing each product’s SKU, title, EUR price, and per‑warehouse inventory levels.

#### Purpose
- Stores product inventory data for a retail catalog.

#### Type
- XML data file  
- Root element: `catalog`  
- Namespaces  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Categories**  
  - 34 distinct categories (`cat-001`–`cat-040`), each with an `id` and `name` attribute.  
  - Category count: 34 (first `cat-001`, last `cat-040`).  
- **Products**  
  - Each `<product>` element has a `sku` attribute (e.g., `SKU-034-0102`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price currency="EUR">` – numeric value.  
    - `<inv:stock warehouse="W#">` – integer quantity for a specific warehouse.  
  - Product count: 20,291 (first product `SKU-001-0001`, last product `SKU-040-9999`).  
- **Inventory**  
  - One `<inv:stock>` per product, specifying `warehouse` and quantity.  
  - No `<inv:recall>` elements present in the merged file.

#### Configuration
- None (no properties, placeholders, or global settings).

#### Dependencies
- Namespace prefix `inv` refers to the inventory schema (`urn:example:inventory`). No external references or includes.
