### catalog.xml
- H: 1.0
- C: 754E8810
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:22:40Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20041; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

> Catalog of products organized by category, each product with title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Defines a product catalog for an e‑commerce system.
- Organizes products into categories and tracks inventory per warehouse.

#### Type
- XML data file  
- Root element: `<catalog>`  
- Namespaces:  
  - `xmlns="urn:example:catalog"` (default)  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **`catalog`**  
  - Attribute: `version="3"`  
  - Contains multiple `<category>` elements.
- **`category`**  
  - Attributes: `id`, `name`.  
  - Contains multiple `<product>` elements.
- **`product`**  
  - Attribute: `sku`.  
  - Child elements:  
    - `<title>` – product name.  
    - `<price>` – attribute `currency="EUR"`.  
    - `<inv:stock>` – attribute `warehouse`; text is quantity.
- **`inv:stock`** (inventory namespace)  
  - Provides stock quantity per warehouse.

#### Configuration
- No configurable values; all data is literal.

#### Dependencies
- References the `inv` namespace (`urn:example:inventory`) for stock elements.  
- Uses the default catalog namespace (`urn:example:catalog`).
