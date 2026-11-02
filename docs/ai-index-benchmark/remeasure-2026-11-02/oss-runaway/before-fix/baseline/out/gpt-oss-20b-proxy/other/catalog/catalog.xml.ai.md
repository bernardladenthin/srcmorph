### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:02:09Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized (1 part summary cut at the output budget)

> A catalog of products grouped by category, each product listing its SKU, title, price in EUR, and inventory stock per warehouse.

#### Purpose
- Stores product inventory data for multiple categories.

#### Type
- XML catalog file  
  - Root element: `catalog` (namespace `urn:example:catalog`)  
  - Namespace prefixes: `inv` → `urn:example:inventory`

#### Definitions
- **Categories**: `cat-001` to `cat-040` (each `<category>` has attributes `id` and `name`)  
- **Products**: each `<product>` has attribute `sku`  
- **Child elements**:  
  - `<title>`: product name  
  - `<price>`: contains `currency="EUR"` and a numeric value  
  - `<inv:stock>`: contains `warehouse` attribute (`W1`–`W9`) and an integer quantity

#### Configuration
- `price` elements always use `currency="EUR"`  
- `inv:stock` elements specify a `warehouse` attribute and a quantity

#### Dependencies
- Uses the `inv` namespace prefix for stock elements; the namespace URI is not shown in this chunk.
