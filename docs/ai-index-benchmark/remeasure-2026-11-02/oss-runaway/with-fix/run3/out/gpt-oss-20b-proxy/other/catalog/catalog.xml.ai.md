### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:37:45Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A structured XML catalog that lists products by category, each with SKU, title, EUR price, and per‑warehouse inventory stock, optionally including recall batches.

#### Purpose
- Stores product listings and inventory data for an e‑commerce catalog.

#### Type
- XML data file  
  Root element: `catalog` (namespace `urn:example:catalog`)  
  Namespace prefixes: `inv` → `urn:example:inventory`

#### Definitions
- **Categories**: `<category>` elements with `id` and `name` attributes.  
  - `id` values: `cat-001`–`cat-009`, `cat-009`–`cat-018`, `cat-020`, `cat-026`–`cat-027`, `cat-035`–`cat-040`.  
  - `name` values: `Category 1`–`Category 40` (where present).  
- **Products**: `<product>` elements with `sku` attribute.  
  - Child `<title>`: product name.  
  - Child `<price>`: `currency="EUR"` and numeric value.  
  - Child `<inv:stock>`: `warehouse` attribute (`W1`, `W2`, …) and quantity value.  
  - Optional child `<inv:recall>`: `batch` attribute (e.g., `R-020-0001`), present only in category 20.  

#### Configuration
- Prices are in EUR.  
- Stock quantities are integers.  
- Recall batch identifiers are strings like `R-020-0001`.  

#### Dependencies
- Namespace `inv` refers to the inventory schema (URL not shown).
