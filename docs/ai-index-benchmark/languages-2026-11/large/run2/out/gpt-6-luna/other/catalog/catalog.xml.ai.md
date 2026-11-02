### catalog.xml
- H: 1.0
- C: 754E8810
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:16:33Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20041; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

> Holds a versioned product catalog grouped by category, with EUR prices and warehouse-specific stock quantities.

#### Purpose
- Provides product titles, SKU identifiers, prices, and inventory records organized into categories.
- The supplied descriptions cover categories `cat-001`–`cat-040`; portions of the catalog are described as incomplete or fragmentary.

#### Type
- Catalog data XML; root element `catalog`.
- Default namespace: `urn:example:catalog`; prefix `inv`: `urn:example:inventory`.

#### Definitions
- `catalog` contains repeated `category` elements; each `category` contains repeated `product` elements.
- `category` attributes: `id`, `name`. Described category identifiers run from `cat-001` through `cat-040`, with names `Category 1` through `Category 40`.
- `product` attribute: `sku`; product records contain `title`, `price`, and `inv:stock`.
- `price` holds a numeric amount and has a `currency` attribute.
- `inv:stock` holds a quantity and has a `warehouse` attribute.
- Categories 1–18 are described as having 125 products each. Visible SKU ranges include `SKU-037-0075`–`SKU-037-0125` and `SKU-038-0001`–`SKU-040-0125`.
- The supplied descriptions report incomplete records or fragments around categories 19 and 37.

#### Configuration
- `catalog` attribute `version`: `3`.
- Visible prices use `currency="EUR"`.
- Warehouse codes range from `W1` to `W9`; stock quantities vary by product.

#### Dependencies
- Default namespace `urn:example:catalog`.
- Inventory namespace `urn:example:inventory`, used by `inv:stock`.
