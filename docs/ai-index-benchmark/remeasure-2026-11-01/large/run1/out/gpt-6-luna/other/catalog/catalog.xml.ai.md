### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:33:26Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 3 of 3 chunks summarized

> Holds a versioned product catalog with category-grouped products, EUR pricing, warehouse stock, and supplier-recall details.

#### Purpose
- Stores product listings grouped by numbered category, with SKU, title, price, and inventory.
- Includes supplier-recall records for products in Categories 20 and 21; the supplied content describes truncated excerpts.

#### Type
- Product catalog data XML; root element `catalog`.
- Default namespace: `urn:example:catalog`; prefix `inv`: `urn:example:inventory` (declared in Part 1; declarations are not visible in Parts 2–3).

#### Definitions
- `catalog` contains repeated `category` records and has a `version` attribute.
- `category` uses `id` and `name` attributes and contains repeated `product` records.
- `product` uses a `sku` attribute and contains `title`, `price`, and `inv:stock`; some products also contain `inv:recall`.
- `price` holds an amount and uses a `currency` attribute. `inv:stock` holds a quantity and uses a `warehouse` attribute. `inv:recall` has a `batch` identifier and recall text.
- Categories are described as `cat-001` through `cat-040`, named `Category 1` through `Category 40`; visible category records are not complete in every excerpt.
- Product SKU pattern includes identifiers such as `SKU-001-0001`. Excerpts include partial records for `SKU-019-0052` and `SKU-036-0116`; Part 3 also describes a partial tail of Category 36.

#### Configuration
- `version`: `3`.
- `price/@currency`: `EUR`.
- `inv:stock/@warehouse`: `W1` through `W9`.
- `inv:recall` text: `supplier recall` on visible products in Categories 20 and 21.
- Stock quantities include zero-valued entries.

#### Dependencies
- Namespace `urn:example:catalog` (default namespace in Part 1).
- Namespace prefix `inv` refers to `urn:example:inventory` in Part 1 and is used by `inv:stock` and `inv:recall`.
