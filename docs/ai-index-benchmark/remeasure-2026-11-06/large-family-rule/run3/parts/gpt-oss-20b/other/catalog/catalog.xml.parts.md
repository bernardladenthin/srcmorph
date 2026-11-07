# Map-reduce answers of other/catalog/catalog.xml

## Chunk 1 of 5

> A catalog XML file that lists product categories, each containing many products with pricing and inventory details.

#### Purpose
- Defines a product catalog for an e‑commerce system.
- Provides product metadata (SKU, title, price, inventory) grouped by category.

#### Type
- XML document  
  - Root element: `<catalog>`  
  - Namespace declarations:  
    - `xmlns="urn:example:catalog"`  
    - `xmlns:inv="urn:example:inventory"`  
  - Attribute: `version="3"`

#### Definitions
- **Categories** (`<category>`)  
  - Attributes: `id`, `name`  
  - 9 categories (`cat-001` to `cat-009`), each containing 125 `<product>` entries.
- **Products** (`<product>`)  
  - Attribute: `sku` (e.g., `SKU-001-0001`)  
  - Child elements:  
    - `<title>` – product name  
    - `<price>` – contains `currency="EUR"` and numeric value  
    - `<inv:stock>` – contains `warehouse` attribute (e.g., `W8`) and quantity value.
- **Inventory namespace** (`inv`) provides `<stock>` elements with warehouse identifiers.

#### Configuration
- `version="3"` on the `<catalog>` element.

#### Dependencies
- None (no external schemas, XSDs, or referenced files).

## Chunk 2 of 5

> A catalog XML fragment that lists product items with SKU, title, price (EUR) and inventory stock per warehouse.

#### Purpose
- Stores product inventory data for a retail catalog.

#### Type
- Data XML file (catalog).  
- Root element: `<catalog>` (not shown in this chunk).  
- Declares namespace prefix `inv` (e.g., `xmlns:inv="http://example.com/inventory"`).

#### Definitions
- **Product**: `<product sku="...">`  
  - `sku`: unique identifier.  
  - Child elements:  
    - `<title>`: product name.  
    - `<price currency="EUR">`: price value.  
    - `<inv:stock warehouse="W#">`: quantity in a specific warehouse.  
- **Warehouse**: identified by `warehouse` attribute (`W1`–`W9`).  
- **Categories**: `<category id="cat-009" name="Category 9">` through `<category id="cat-018" name="Category 18">`.  
  - Each category contains 125 `<product>` entries (pattern `SKU-<cat>-<4-digit>`).

#### Configuration
- Currency fixed to `EUR`.  
- Warehouse identifiers are `W1`–`W9`.  
- No dynamic placeholders or external references in this chunk.

#### Dependencies
- Namespace `inv` (inventory) must be defined in the root element.  
- No external files or schemas referenced within this fragment.

## Chunk 3 of 5

> A fragment of a large catalog XML containing product listings for categories 18‑25, each with SKU, title, price, inventory stock, and optional recall data.

#### Purpose
- Stores product inventory data for a retail catalog.

#### Type
- XML data file (catalog).  
- Root element: `<catalog>` (not shown in chunk).  
- Declares namespace `inv` for inventory elements.

#### Definitions
- **Categories** (`<category id="cat-018" …>` to `<category id="cat-025" …>`): each holds a list of `<product>` elements.  
- **Products** (`<product sku="SKU-XXX-XXXX">`):  
  - `sku` attribute: unique product identifier.  
  - Child elements:  
    - `<title>`: product name.  
    - `<price currency="EUR">` – price value.  
    - `<inv:stock warehouse="W#">` – quantity in specified warehouse.  
    - Optional `<inv:recall batch="R-XXX-XXXX">` – recall batch identifier.  
- **Inventory namespace**: `inv` (prefix) used for `<stock>` and `<recall>` elements.

#### Configuration
- No configurable parameters in this chunk; values are literal data.

#### Dependencies
- Relies on the `inv` namespace definition (prefix `inv`) declared in the root `<catalog>` element.  
- No external references or includes.

## Chunk 4 of 5

> A catalog XML file that lists product items grouped by category, each with SKU, title, price and inventory stock per warehouse.

#### Purpose
- Stores product catalog data for an e‑commerce system.

#### Type
- XML data file  
  - Root element: `<catalog>` (not shown in this chunk)  
  - Declares namespace prefix `inv` for inventory data.

#### Definitions
- **Categories**: nine `<category>` elements (`cat-026` to `cat-034`), each containing 125 `<product>` entries.  
- **Product**:  
  - `sku` attribute (e.g., `SKU-026-0020`)  
  - Child `<title>` (product name)  
  - Child `<price>` with `currency="EUR"` attribute and numeric value  
  - Child `<inv:stock>` with `warehouse` attribute and quantity value.  
- **Inventory namespace**: `inv` (prefix) used for `<inv:stock>` elements.

#### Configuration
- None (no properties or placeholders in this chunk).

#### Dependencies
- None (no external references or schema declarations shown).

## Chunk 5 of 5

> A catalog XML listing products with prices and inventory stock per warehouse.

#### Purpose
- Stores product inventory data for multiple categories.

#### Type
- XML catalog file; root element `<catalog>`.
- Declares namespace `inv` for inventory elements.

#### Definitions
- **Categories**: `cat-035` to `cat-040` (6 categories).
- **Products**: Each `<product>` has attributes:
  - `sku` (e.g., `SKU-035-0001`).
  - Child elements:
    - `<title>`: product name.
    - `<price currency="EUR">` with numeric value.
    - `<inv:stock warehouse="W#">` with quantity.
- **Warehouses**: `W1` to `W9` referenced in stock elements.

#### Configuration
- No configurable attributes or placeholders present in this chunk.

#### Dependencies
- Namespace prefix `inv` mapped to an inventory schema (not shown).

## Reduce round 1, batch 1 of 1 (5 parts)

> A single XML catalog that defines 40 product categories, each with 125 products, including pricing, multi‑warehouse inventory, and optional recall data.

#### Purpose
- Stores a complete e‑commerce product catalog.
- Provides SKU, title, price (EUR), and inventory per warehouse.
- Includes optional recall batch information for certain products.

#### Type
- XML document  
  - Root element: `<catalog>`  
  - Namespace declarations:  
    - `xmlns="urn:example:catalog"`  
    - `xmlns:inv="urn:example:inventory"`  
  - Attribute: `version="3"`

#### Definitions
- **Categories** (`<category>`)  
  - Attributes: `id`, `name`  
  - 40 categories: `cat-001`–`cat-040` (9, 9, 8, 9, 6 respectively).  
  - Each category contains 125 `<product>` entries.
- **Products** (`<product>`)  
  - Attribute: `sku` (e.g., `SKU-001-0001`)  
  - Child elements:  
    - `<title>` – product name  
    - `<price currency="EUR">` – numeric value  
    - `<inv:stock warehouse="W#">` – quantity per warehouse (`W1`–`W9`)  
    - Optional `<inv:recall batch="R-XXX-XXXX">` – recall batch identifier (present only in categories 18–25).
- **Inventory namespace** (`inv`) supplies `<stock>` and `<recall>` elements.

#### Configuration
- `version="3"` on the `<catalog>` element.
- Currency fixed to `EUR`.
- Warehouse identifiers `W1`–`W9`.

#### Dependencies
- Namespace prefix `inv` mapped to `urn:example:inventory` in the root `<catalog>` element.  
- No external schemas, XSDs, or referenced files.

