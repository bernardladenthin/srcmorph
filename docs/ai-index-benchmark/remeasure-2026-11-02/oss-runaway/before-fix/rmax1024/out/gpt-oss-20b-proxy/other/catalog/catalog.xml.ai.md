### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:17:37Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A single XML catalog file that defines product inventory data for an e‑commerce system, organized by category and warehouse.

#### Purpose
- Stores product inventory data for an e‑commerce system.

#### Type
- XML catalog file  
- Root element: `catalog`  
- Namespace declarations:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Categories**  
  - 6 categories (`cat-001` to `cat-006`) in Part 1;  
  - 10 categories (`cat-009` to `cat-018`) in Part 2;  
  - 9 categories (`cat-018` to `cat-026`) in Part 3;  
  - 6 categories (`cat-035` to `cat-040`) in Part 5.  
  - Each `<category>` has attributes `id` and `name`.
- **Products**  
  - Each `<product>` has attribute `sku`.  
  - Child `<title>` (product name).  
  - Child `<price>` with attribute `currency="EUR"` and numeric value.  
  - Child `<inv:stock>` with attribute `warehouse` (`W1`‑`W9`) and quantity.  
  - In category 20, optional `<inv:recall>` with attribute `batch` and text “supplier recall”.
- **Inventory namespace**  
  - Prefix `inv` used for `<inv:stock>` and `<inv:recall>` elements.

#### Configuration
- No configurable attributes or placeholders; all values are literal data.

#### Dependencies
- Namespace prefix `inv` bound to an inventory schema (URI not shown). No external schemas or includes referenced.
