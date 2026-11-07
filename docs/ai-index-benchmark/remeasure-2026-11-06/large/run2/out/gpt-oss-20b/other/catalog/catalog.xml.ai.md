### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:14:20Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: file
- P: F19D8599
- N: 712EFF34
- M: 2FA139DC
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A comprehensive product catalog that groups items by category, lists each product’s SKU, title, EUR price, and per‑warehouse inventory, and occasionally records recall batch information.

#### Purpose
- Supplies product, pricing, and inventory data for an e‑commerce or warehouse management system.

#### Type
- XML catalog data file  
- Root element: `catalog`  
- Namespaces:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Categories**: 26 (`cat-001` – `cat-026`) each with `id` and `name`.  
- **Products**: 1 250 total (≈140 per category).  
  - Each `<product>` has:  
    - `sku` attribute (e.g., `SKU-018-0039`)  
    - `<title>` element (product name)  
    - `<price currency="EUR">` element (numeric value)  
    - One or more `<inv:stock warehouse="W#">` elements (quantity per warehouse, `W1`–`W9`)  
    - (Category 20 only) optional `<inv:recall batch="R-020-xxxx">` element.  
- **Warehouses**: `W1` – `W9` referenced in stock elements.

#### Configuration
- All `<price>` elements use `currency="EUR"`.  
- Inventory quantities are specified in `<inv:stock>` with a `warehouse` attribute.  
- Recall batches are identified by the `batch` attribute on `<inv:recall>`.

#### Dependencies
- Namespace `urn:example:catalog` defines the catalog structure.  
- Namespace `urn:example:inventory` defines `<inv:stock>` and `<inv:recall>` elements.
