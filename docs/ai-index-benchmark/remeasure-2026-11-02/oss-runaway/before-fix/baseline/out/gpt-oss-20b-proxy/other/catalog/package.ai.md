### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:05:43Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A catalog of products grouped by category, each product listing its SKU, title, price in EUR, and inventory stock per warehouse.

#### Purpose
- Persist product inventory data for multiple categories in a single XML document.

#### Responsibilities
- **Catalog structure**: defines categories and products with attributes and child elements.
- **Pricing**: stores product price with fixed EUR currency.
- **Inventory**: tracks stock quantities per warehouse via namespaced `<inv:stock>` elements.

#### Key units
- **`catalog`** (root element, namespace `urn:example:catalog`): container for all categories.
- **`category`** (attributes `id`, `name`): logical grouping of products.
- **`product`** (attribute `sku`): individual item definition.
- **`title`**: product name.
- **`price`** (attribute `currency="EUR"`): monetary value.
- **`inv:stock`** (namespaced, attributes `warehouse`, text quantity): stock per warehouse.

#### Data flow
1. **Input**: XML file loaded by an application or service.
2. **Parsing**: XML parser reads `catalog` → `category` → `product` hierarchy.
3. **Extraction**: For each product, retrieve `sku`, `title`, `price`, and iterate over `inv:stock` to collect warehouse quantities.
4. **Output**: Structured data (e.g., objects or database rows) representing inventory levels per product and warehouse.

#### Dependencies
- Namespace `urn:example:inventory` for `<inv:stock>` elements.
- Fixed currency attribute `EUR` on `<price>` elements.

#### Cross-cutting
- Consistent use of attributes (`id`, `sku`, `currency`, `warehouse`) across elements.
- Uniform namespace prefix `inv` for inventory stock elements.
- All prices expressed in EUR, simplifying currency handling.
