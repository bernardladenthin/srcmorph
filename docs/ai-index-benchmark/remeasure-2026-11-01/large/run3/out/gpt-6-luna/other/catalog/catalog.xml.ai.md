### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:12:11Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 3 of 3 chunks summarized

> Describes a versioned product catalog with categorized products, EUR pricing, warehouse stock, and supplier recall notices.

#### Purpose
- The supplied content describes catalog records organized by category and product.
- It reports product identifiers, titles, prices, stock quantities, and some recall details; no XML records are present in the supplied text.

#### Type
- The supplied content is prose, not XML; it contains no XML root element or namespace declarations.

#### Definitions
- Described category identifiers: `cat-001` through `cat-040`, with names `Category 1` through `Category 40`.
- Described product fields: `sku`, `title`, `price` with `currency`, and `inv:stock` with `warehouse`.
- Described optional recall field: `inv:recall` with `batch`.
- Described product ranges include `SKU-001-0001` onward, `SKU-020-0001` through `SKU-036-0116`, and partial `SKU-019-*` records. Category 19 is described through `SKU-019-0052`; the final described product has only title and price.
- The text describes categories 1–18 as having 125 products each, and categories 37–40 as having 125 products each.
- The final described record is incomplete at `SKU-036-0116`.

#### Configuration
- Described catalog version: `3`.
- Described price currency: `EUR`.
- Described warehouse identifiers: `W1` through `W9`.
- Described recall text: `supplier recall`.
- Described recall batch values include `R-020-0001` through `R-020-0125` and `R-021-0001` through `R-021-0125`.

#### Dependencies
- Described namespace URIs: `urn:example:catalog`, `urn:example:inventory`.
- Described namespaced elements: `inv:stock`, `inv:recall`.
