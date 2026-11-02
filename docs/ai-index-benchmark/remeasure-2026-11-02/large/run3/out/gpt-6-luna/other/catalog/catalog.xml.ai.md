### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:06:58Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 3 of 3 chunks summarized

> Defines a 40-category product catalog with EUR pricing, warehouse inventory, and supplier-recall data.

#### Purpose
- Provides category-grouped product, pricing, and stock records.
- Marks recalled products in categories 20 and 21.

#### Type
- Data XML; root element `catalog`.
- Namespaces: default `urn:example:catalog`; `inv` = `urn:example:inventory`.

#### Definitions
- `catalog` contains 40 `category` records, from `cat-001` / `Category 1` through `cat-040` / `Category 40`.
- Each `category` has `id` and `name` attributes and contains 125 `product` records; the product family runs from `SKU-001-0001` through `SKU-040-0125`.
- Each `product` has a `sku` attribute and contains `title`, `price` with a `currency` attribute, and `inv:stock` with a `warehouse` attribute.
- Products in `cat-020` and `cat-021` also contain `inv:recall` with a `batch` attribute and supplier-recall text.

#### Configuration
- `catalog` attribute `version`: `3`.
- `price` attribute `currency`: `EUR`.
- `inv:stock` warehouse values: `W1`–`W9`.
- Recall batch identifiers follow `R-<category number>-<product sequence>`; range `R-020-0001`–`R-021-0125`.

#### Dependencies
- Inventory namespace `urn:example:inventory`, used by `inv:stock` and `inv:recall`.
