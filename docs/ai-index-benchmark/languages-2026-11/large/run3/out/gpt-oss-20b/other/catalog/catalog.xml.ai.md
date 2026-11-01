### catalog.xml
- H: 1.0
- C: 754E8810
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:31:05Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20041; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

> Catalog of products organized by category, each product listing its SKU, title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Provides a structured inventory of products across multiple categories.
- Enables lookup of product details and stock levels by SKU or category.

#### Type
- XML data file  
- Root element: `catalog` (namespace `urn:example:catalog`)  
- Namespace declarations:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- `catalog`  
  - Attribute `version="3"`  
  - Contains multiple `category` elements.
- `category`  
  - Attributes: `id`, `name`.  
  - Contains one or more `product` elements.
- `product`  
  - Attribute `sku`.  
  - Child elements:  
    - `title` – product name.  
    - `price` – text value, attribute `currency="EUR"`.  
    - `inv:stock` – inventory element in the `inv` namespace, attribute `warehouse`, text quantity.
- `inv:stock`  
  - Namespace: `urn:example:inventory`  
  - Attribute `warehouse` identifies the warehouse.  
  - Text content is the stock quantity.

#### Configuration
- No configurable properties or placeholders are present in the file.

#### Dependencies
- References only the two XML namespaces defined above (`urn:example:catalog` and `urn:example:inventory`).
