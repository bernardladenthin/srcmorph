### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:51:46Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 3 of 3 chunks summarized

> Defines a 40-category product catalog with EUR pricing, warehouse inventory, and supplier recall notices for selected products.

#### Purpose
- Stores categorized product listings identified by SKU, with titles, prices, and stock quantities.
- Records supplier recall notices for products in Categories 20 and 21.

#### Type
- Catalog data XML; root element `catalog`.
- Default namespace: `urn:example:catalog`; `inv`: `urn:example:inventory`.

#### Definitions
- `catalog` contains 40 `category` records, IDs `cat-001`–`cat-040`; category names explicitly shown include `Category 1`–`Category 19` and `Category 37`–`Category 40`.
- Each `category` contains 125 `product` records, 5,000 total; SKU pattern `SKU-NNN-0001`–`SKU-NNN-0125`, from `SKU-001-0001` through `SKU-040-0125`.
- Each `product` has a `sku` attribute and contains `title`, `price` with a `currency` attribute, and `inv:stock` with a `warehouse` attribute and quantity text.
- Products in Categories 20 and 21 also contain `inv:recall` records with a `batch` attribute and `supplier recall` text; 250 recall records, from `R-020-0001` through `R-021-0125`.

#### Configuration
- `catalog` attribute `version`: `3`.
- Product price `currency`: `EUR`.
- Stock warehouse codes shown: `W1`–`W9`; quantities include zero.

#### Dependencies
- `urn:example:inventory` qualifies `inv:stock` and `inv:recall`.
