### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:54:20Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> Catalog of products grouped by category, each with price and warehouse stock.

#### Purpose
- Stores product inventory data for an e‑commerce system.  
- Provides a structured list of categories, each containing multiple products with SKU, title, price (currency), and stock per warehouse.

#### Type
- XML data file  
  - Root element: `<catalog>`  
  - Default namespace: `xmlns="urn:example:catalog"`  
  - Prefix `inv` bound to `xmlns:inv="urn:example:inventory"`  
  - Attribute: `version="3"`

#### Definitions
- `catalog`  
  - Attribute: `version` (string)  
  - Child: `<category>` (0+)
- `category` (`id`, `name`)  
  - Child: `<product>` (0+)
- `product` (`sku`)  
  - `<title>` (string)  
  - `<price>` (`currency` attribute, numeric value)  
  - `<inv:stock>` (`warehouse` attribute, numeric quantity)
- `inv:stock` (namespace `urn:example:inventory`)  
  - Attribute: `warehouse` (string)  
  - Text: stock quantity (integer)

#### Configuration
- No configurable properties or placeholders; all values are literal data.

#### Dependencies
- External namespace: `urn:example:inventory` (prefix `inv`) used for stock elements.  
- No external files, schemas, or classes referenced.
