### catalog.xml
- H: 1.0
- C: 754E8810
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:25:28Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20041; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

> Describes a product catalog grouped by category, with EUR prices and warehouse-specific inventory quantities.

#### Purpose
- Summarizes catalog records across categories 1–40, including product identifiers, titles, prices, and stock.
- The supplied content is three textual summaries, not the underlying catalog XML.

#### Type
- Markdown-like text, not parseable XML; no XML root element or namespace declarations are present.
- Contains sections labeled `Part 1`, `Part 2`, and `Part 3`, each with `Purpose`, `Type`, `Definitions`, `Configuration`, and `Dependencies`.

#### Definitions
- The summaries describe a catalog structure of `catalog` → `category` → `product`, with product fields `title`, `price`, and `inv:stock`.
- Described category identifiers span `cat-001`–`cat-040`; the summaries note partial records around categories 19, 37, and 40.
- Described product identifiers use `sku`, with examples such as `SKU-001-0001`; titles follow the stated pattern `Product <category>.<number>`.
- The summaries describe `price` records with a `currency` attribute and `inv:stock` records with a `warehouse` attribute.

#### Configuration
- The summaries report `catalog` attribute `version="3"` and `price` attribute `currency="EUR"`.
- Reported warehouse identifiers range from `W1` to `W9`; stock quantities are described as numeric text, including zero.

#### Dependencies
- The summaries mention the `inv` prefix and inventory namespace URI `urn:example:inventory` in Part 1; Parts 2 and 3 state that its binding is not visible.
- No referenced files, schemas, classes, packages, or external systems are described.
