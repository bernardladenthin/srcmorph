### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:51:10Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 3 of 3 chunks summarized

> Holds a versioned product catalog with EUR pricing, warehouse stock levels, and supplier-recall details.

#### Purpose
- Stores products grouped into numbered categories, with SKU, title, price, and inventory data.
- Includes recall descriptions for products in categories `cat-020` and `cat-021`.

#### Type
- XML data file; root element `catalog`.
- Declared namespaces: default namespace `urn:example:catalog`; prefix `inv` maps to `urn:example:inventory`.

#### Definitions
- `catalog` contains repeated `category` elements, identified by `id` and `name`; its `version` is `3`.
- Each `category` contains repeated `product` elements, identified by `sku`.
- Products contain `title`, `price`, and `inv:stock`; products in `cat-020` and `cat-021` also contain `inv:recall`.
- Categories shown: `cat-001` through `cat-040`, named `Category 1` through `Category 40`. Categories `cat-001`–`cat-018` and `cat-037`–`cat-040` each show 125 products.
- Product SKUs follow `SKU-NNN-NNNN`; visible complete sequences include `SKU-NNN-0001` through `SKU-NNN-0125`.
- `price` has a `currency` attribute. `inv:stock` has a `warehouse` attribute and holds a quantity.
- `inv:recall` has a `batch` attribute and holds the text `supplier recall`; visible batches span `R-020-0001`–`R-020-0125` and `R-021-0001`–`R-021-0125`.

#### Configuration
- `catalog/@version`: `3`.
- Visible `price/@currency`: `EUR`.
- Visible `inv:stock/@warehouse` values: `W1` through `W9`; stock quantities include `0`.
- Visible recall batch IDs use category-specific `R-NNN-NNNN` values.

#### Dependencies
- `urn:example:catalog` default namespace and `urn:example:inventory` namespace for `inv:stock` and `inv:recall`.
- No referenced files, schemas, or external systems are shown.
