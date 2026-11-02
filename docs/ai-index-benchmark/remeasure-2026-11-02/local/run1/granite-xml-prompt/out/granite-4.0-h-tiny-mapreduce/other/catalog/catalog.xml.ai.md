### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:28:07Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 7 of 7 chunks summarized (1 part summary cut at the output budget)

> <configuration of a catalog XML file in the urn:example:catalog namespace>

#### Purpose
- Configures a product catalog with categories, products, and inventory stock per warehouse.

#### Type
- **XML file**: `catalog.xml`
- **Root element**: `<catalog>`
- **Declared namespaces**:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Category**: `<category id="cat-X" name="Category X">` – groups multiple `<product>` entries.
- **Product**: `<product sku="SKU-X-XXXX">` – contains:
  - `<title>` (product name)  
  - `<price currency="EUR">` (price in EUR)  
  - `<inv:stock warehouse="WY">` (inventory count per warehouse)

#### Configuration
- **Version**: `3` (catalog version)  
- **No other configurable values** (e.g., URLs, thresholds) are present.

#### Dependencies
- No referenced external files, schemas, classes, packages, or systems are listed.

#### Notes
- The excerpt shows only a subset of categories and products; the full file would contain all defined entries.
