### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:38:51Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A catalog XML that stores product inventory data for dozens of categories, each product identified by SKU, title, EUR price, and per‑warehouse stock, with optional recall information.

#### Purpose
- Stores product inventory data for an e‑commerce catalog.

#### Type
- XML data file  
- Root element: `catalog`  
- Namespaces  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="http://example.com/inventory"`

#### Definitions
- **Categories**  
  - `cat-001` to `cat-040` (each with `id` and `name` attributes)  
  - Category families:  
    - `cat-001`–`cat-009`: 9 categories, 1–9 products each  
    - `cat-010`–`cat-018`: 9 categories, 1–25 products each  
    - `cat-019`–`cat-025`: 7 categories, 1–25 products each  
    - `cat-026`–`cat-032`: 7 categories, 1–25 products each  
    - `cat-033`–`cat-040`: 8 categories, 1–25 products each  
- **Products** (`<product>`)  
  - Attribute `sku` (e.g., `SKU-018-0091`)  
  - Child `<title>` (product name)  
  - Child `<price currency="EUR">` (numeric value)  
  - Child `<inv:stock warehouse="W#">` (numeric quantity)  
  - Optional child `<inv:recall batch="R-020-0001">` with text “supplier recall”

#### Configuration
- No configurable properties or placeholders.

#### Dependencies
- Namespace prefix `inv` bound to `http://example.com/inventory`.
