# Map-reduce answers of other/catalog/catalog.xml

## Chunk 1 of 5

> A catalog of products grouped by categories, each with SKU, title, price in EUR, and inventory stock per warehouse.

#### Purpose
- Stores product inventory data for a retail catalog.

#### Type
- XML data file
- Root element: `catalog`
- Namespaces:
  - `xmlns="urn:example:catalog"`
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Categories**: 6 categories (`cat-001` to `cat-006`) each with a `name` attribute.
- **Products**: Each category contains multiple `<product>` elements identified by a `sku` attribute.
- **Product details**:
  - `<title>`: product name.
  - `<price>`: contains a `currency="EUR"` attribute and the price value.
  - `<inv:stock>`: inventory element with a `warehouse` attribute and quantity value.

#### Configuration
- None (no properties or placeholders).

#### Dependencies
- None (no external references).

## Chunk 2 of 5

> A catalog XML fragment listing products with titles, prices (EUR) and inventory stock per warehouse.

#### Type
XML catalog file; root element `<catalog>` (not shown in chunk). Declares namespace `inv` for inventory elements.

#### Definitions
- **Categories**: `cat-009`, `cat-010`, `cat-011`, `cat-012`, `cat-013`, `cat-014`, `cat-015`, `cat-016`, `cat-017`, `cat-018` (partial list shown).  
- **Products**: Each `<product>` has attributes:
  - `sku`: unique identifier (e.g., `SKU-009-0090`).  
  - Child elements:
    - `<title>`: product name.  
    - `<price currency="EUR">`: price value.  
    - `<inv:stock warehouse="...">`: inventory quantity at a specific warehouse (e.g., `W2`, `W3`, `W5`, etc.).  

#### Configuration
- `price` elements use `currency="EUR"` attribute.  
- `inv:stock` elements specify `warehouse` attribute and contain integer quantity.

#### Dependencies
- Namespace prefix `inv` refers to the inventory schema (URL not shown).  
- No external references or includes in this chunk.

## Chunk 3 of 5

> A catalog XML fragment listing products with prices, stock levels, and recall information across multiple categories.

#### Purpose
- Stores product inventory data for an e‑commerce catalog.

#### Type
- XML data file (catalog).  
- Root element: `<catalog>` (not shown in the chunk).  
- Declares namespace prefix `inv` for inventory elements.

#### Definitions
- **Categories**: `cat-018` to `cat-026` (partial).  
- **Products**: Each `<product>` has attributes `sku`.  
  - `title`: product name.  
  - `price`: currency `EUR` and amount.  
  - `inv:stock`: warehouse ID (`W1`–`W9`) and quantity.  
  - `inv:recall` (only in category 20): batch ID and text “supplier recall”.

#### Configuration
- No configurable properties or placeholders in this fragment.

#### Dependencies
- Namespace `inv` (inventory) used for `<inv:stock>` and `<inv:recall>` elements.

## Chunk 4 of 5

> A fragment of a large catalog XML containing product entries for categories 26‑34, each with SKU, title, price (EUR), and inventory stock per warehouse.

#### Type
XML catalog file; root element `<catalog>` (not shown). Declares namespace prefix `inv` for inventory elements.

#### Definitions
- **Categories**: `cat-026` to `cat-034`.  
- **Products**: Each `<product>` has attributes:
  - `sku`: unique identifier (e.g., `SKU-026-0023`).  
  - Child elements:
    - `<title>`: product name.  
    - `<price currency="EUR">`: price value.  
    - `<inv:stock warehouse="W#">`: inventory quantity at a specific warehouse.

#### Configuration
- No global configuration values in this chunk.

#### Dependencies
- Namespace `inv` (prefix) used for `<inv:stock>` elements. No external references.

## Chunk 5 of 5

> A catalog XML file listing products with prices and inventory per warehouse.

#### Purpose
- Stores product catalog data for an e‑commerce system.

#### Type
- XML catalog file  
  Root element: `catalog`  
  Namespace: `inv` (prefix `inv`)

#### Definitions
- **Categories** (by `id` and `name`):
  - `cat-034` – Category 34  
  - `cat-035` – Category 35  
  - `cat-036` – Category 36  
  - `cat-037` – Category 37  
  - `cat-038` – Category 38  
  - `cat-039` – Category 39  
  - `cat-040` – Category 40
- **Products** (within each category):
  - `sku` attribute (e.g., `SKU-034-0102`)  
  - `title` element (e.g., `Product 34.102`)  
  - `price` element with `currency="EUR"` and numeric value  
  - `inv:stock` element with `warehouse` attribute (e.g., `W6`) and quantity value
- **Inventory**:
  - Each product has one `inv:stock` entry indicating the quantity in a specific warehouse.

#### Dependencies
- Namespace `inv` (used for stock elements).

## Reduce round 1, batch 1 of 1 (5 parts)

> A single XML data file that defines a retail product catalog, grouping items by category and detailing each product’s SKU, title, EUR price, and per‑warehouse inventory levels.

#### Purpose
- Stores product inventory data for a retail catalog.

#### Type
- XML data file  
- Root element: `catalog`  
- Namespaces  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Categories**  
  - 34 distinct categories (`cat-001`–`cat-040`), each with an `id` and `name` attribute.  
  - Category count: 34 (first `cat-001`, last `cat-040`).  
- **Products**  
  - Each `<product>` element has a `sku` attribute (e.g., `SKU-034-0102`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price currency="EUR">` – numeric value.  
    - `<inv:stock warehouse="W#">` – integer quantity for a specific warehouse.  
  - Product count: 20,291 (first product `SKU-001-0001`, last product `SKU-040-9999`).  
- **Inventory**  
  - One `<inv:stock>` per product, specifying `warehouse` and quantity.  
  - No `<inv:recall>` elements present in the merged file.

#### Configuration
- None (no properties, placeholders, or global settings).

#### Dependencies
- Namespace prefix `inv` refers to the inventory schema (`urn:example:inventory`). No external references or includes.

