### catalog.xml
- H: 1.0
- C: 754E8810
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:37:34Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20041; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

> Holds categorized product records with EUR prices and warehouse-specific inventory quantities.

#### Purpose
- Provides product identities, titles, prices, and stock quantities grouped by category.
- Combines catalog data with warehouse inventory records.

#### Type
- XML catalog data; root element `catalog`.
- Default namespace: `urn:example:catalog`; prefix `inv`: `urn:example:inventory` (declared in Part 1; later excerpts omit the declarations).

#### Definitions
- `catalog` contains repeated `category` elements; visible category identifiers span `cat-001`–`cat-040`.
- Each `category` has `id` and `name` attributes and contains repeated `product` elements.
- Each `product` has a `sku` attribute and contains `title`, `price`, and `inv:stock`.
- `price` has a `currency` attribute; `inv:stock` has a `warehouse` attribute and holds a quantity.
- Visible SKU patterns include `SKU-NNN-NNNN`; the final visible record is incomplete at `SKU-019-0052`, and later excerpts also end within a product record.

#### Configuration
- `catalog` attribute `version`: `3`.
- Visible prices use `currency="EUR"`.
- Visible stock records use warehouse identifiers `W1`–`W9`; quantities include zero values.

#### Dependencies
- Catalog namespace: `urn:example:catalog`.
- Inventory namespace: `urn:example:inventory`, used by `inv:stock`.
