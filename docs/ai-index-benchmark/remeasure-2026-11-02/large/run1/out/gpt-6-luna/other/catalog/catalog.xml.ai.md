### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:36:07Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 3 of 3 chunks summarized

> Defines a 40-category product catalog with EUR pricing, warehouse inventory, and recall details for products in two categories.

#### Purpose
- Stores categorized product records with titles, prices, and warehouse stock.
- Marks recalled products in Categories 20 and 21.

#### Type
- XML data file; root element `catalog`.
- Default namespace: `urn:example:catalog`; `inv` namespace: `urn:example:inventory`.

#### Definitions
- `catalog` has `version="3"` and contains 40 `category` records, from `cat-001` / `Category 1` through `cat-040` / `Category 40`.
- Each `category` contains 125 `product` records; 5,000 products total, from `SKU-001-0001` through `SKU-040-0125`.
- Each `product` has a `sku` attribute and contains `title`, `price` with a `currency` attribute, and `inv:stock` with a `warehouse` attribute and quantity.
- Products in `cat-020` and `cat-021` also contain `inv:recall` with a `batch` identifier and text `supplier recall`: 250 recall records, from `R-020-0001` through `R-021-0125`.

#### Configuration
- `version`: `3`.
- All `price` records use `currency="EUR"`.

#### Dependencies
- Namespace reference: `urn:example:inventory`.
