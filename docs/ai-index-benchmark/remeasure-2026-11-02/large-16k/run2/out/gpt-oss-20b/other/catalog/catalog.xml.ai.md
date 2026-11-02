### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:33:00Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 6 of 6 chunks summarized

> A hierarchical product catalog that lists categories, products, prices in EUR, and per‑warehouse inventory stock, optionally including recall batches.

#### Purpose
- Stores product information for an e‑commerce system.
- Provides price and stock levels per warehouse.

#### Type
- XML data file.
- Root element: `catalog` (namespace `urn:example:catalog`).
- Declares namespace prefix `inv` for `urn:example:inventory`.
- Attribute `version="3"` on `catalog`.

#### Definitions
- **Categories**: 9 top‑level `<category>` elements, each with `id` and `name` attributes.  
  - Example IDs: `cat-009`, `cat-016`, `cat-024`, …, `cat-040`.  
  - Example names: `Category 9`, `Category 16`, `Category 24`, …, `Category 40`.
- **Products**: each `<category>` contains a sequence of `<product>` elements.  
  - `sku` attribute uniquely identifies a product (e.g., `SKU-024-0012`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price>` – contains numeric value and attribute `currency="EUR"`.  
    - `<inv:stock>` – namespace `inv`; attributes `warehouse` (e.g., `W1`–`W9`) and text quantity.  
    - Optional `<inv:recall>` – attributes `batch` and text reason.
- **Inventory**: `<inv:stock>` elements belong to the `inv` namespace and specify stock per warehouse.

#### Configuration
- No configurable attributes or elements beyond the static data shown.

#### Dependencies
- Namespace `urn:example:catalog` (default).
- Namespace `urn:example:inventory` (prefix `inv`).
- No external schema or XSD references present.
