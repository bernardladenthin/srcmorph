### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:30:38Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
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
