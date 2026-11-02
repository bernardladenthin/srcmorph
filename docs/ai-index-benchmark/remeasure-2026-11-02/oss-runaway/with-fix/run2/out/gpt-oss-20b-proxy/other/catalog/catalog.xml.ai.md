### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:34:08Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A structured XML catalog that groups products by category, each product listing its SKU, title, EUR price, and warehouse‑specific stock levels.

#### Purpose
- Stores product inventory data for an e‑commerce system.

#### Type
- XML catalog file  
  - Root element: `catalog` (namespace `urn:example:catalog`)  
  - Namespace: `inv` → `urn:example:inventory` (or `http://example.com/inventory` in later fragments)  
  - Attribute: `version="3"`

#### Definitions
- **Categories** (`category` elements, 9 in the first chunk, 10 in the fourth chunk)  
  - `id` (e.g. `cat-001`, `cat-009` … `cat-018`, `cat-026` … `cat-035`)  
  - `name` (e.g. `Category 1`, `Category 9` … `Category 18`, `Category 26` … `Category 35`)  
  - Contains one or more `product` elements.
- **Products** (`product` elements)  
  - Attribute `sku` (e.g. `SKU-001-0001`, `SKU-009-0111`, `SKU-018-0091`)  
  - Child elements:  
    - `title` – product name  
    - `price` – numeric value with attribute `currency="EUR"`  
    - `inv:stock` – inventory quantity with attribute `warehouse` (`W1`–`W9`) and text content as integer.
- **Inventory namespace** `inv` defines `stock` elements.

#### Configuration
- All `price` elements use `currency="EUR"`.  
- `inv:stock` elements contain integer quantities; warehouses identified by codes `W1`–`W9`.

#### Dependencies
- Relies on the `urn:example:catalog` namespace for catalog structure.  
- Uses the `urn:example:inventory` (or `http://example.com/inventory`) namespace for stock information.
