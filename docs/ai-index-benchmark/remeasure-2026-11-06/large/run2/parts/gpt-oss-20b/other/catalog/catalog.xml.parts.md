# Map-reduce answers of other/catalog/catalog.xml

## Chunk 1 of 5

> A catalog XML file listing products grouped by category, each with title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Provides product data for an e‑commerce or inventory system.

#### Type
- XML catalog
- Root element: `catalog`
- Namespaces: `xmlns="urn:example:catalog"`, `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Categories**: 9 (`cat-001` to `cat-009`) each with a `name`.
- **Products**: 1250 total (≈140 per category). Each product has:
  - `sku` attribute (e.g., `SKU-001-0001`)
  - `<title>` element
  - `<price currency="EUR">` element
  - `<inv:stock warehouse="W#">` element (quantity)
- **Warehouses**: `W1`–`W9` referenced in stock elements.

#### Configuration
- No configurable attributes or placeholders in this chunk.

#### Dependencies
- Namespace `urn:example:catalog` defines catalog structure.
- Namespace `urn:example:inventory` defines stock elements.

## Chunk 2 of 5

> A catalog XML fragment listing products across multiple categories, each with a SKU, title, EUR price, and inventory stock per warehouse.

#### Purpose
- Stores product inventory data for a catalog.

#### Type
- XML data file; root element `<catalog>` (not shown in this chunk).  
- Namespace `inv` declared for `<inv:stock>` elements.

#### Definitions
- **Categories** (`<category id="cat-009" …>` to `<category id="cat-018" …>`).  
- **Products**: each `<product sku="...">` contains:
  - `<title>` – product name.
  - `<price currency="EUR">` – price value.
  - `<inv:stock warehouse="...">` – quantity in a specific warehouse.

#### Configuration
- No configurable attributes or placeholders in this chunk.

#### Dependencies
- Namespace prefix `inv` refers to the inventory schema (URI not shown).  
- Products reference warehouses `W1`–`W9`.

## Chunk 3 of 5

> A catalog XML file that lists products by category, each with a SKU, title, price (EUR), and inventory stock per warehouse, and in some cases a supplier recall batch.

#### Type
- XML data file
- Root element: `<catalog>`
- Namespace: `inv` (prefix `inv`) declared for inventory elements

#### Definitions
- **Categories**: `cat-018` to `cat-026` (each with `id` and `name` attributes)
- **Products**:  
  - Category 18: `SKU-018-0039` to `SKU-018-0125` (87 products)  
  - Category 19: `SKU-019-0001` to `SKU-019-0125` (125 products)  
  - Category 20: `SKU-020-0001` to `SKU-020-0125` (125 products)  
  - Category 21: `SKU-021-0001` to `SKU-021-0125` (125 products)  
  - Category 22: `SKU-022-0001` to `SKU-022-0125` (125 products)  
  - Category 23: `SKU-023-0001` to `SKU-023-0125` (125 products)  
  - Category 24: `SKU-024-0001` to `SKU-024-0125` (125 products)  
  - Category 25: `SKU-025-0001` to `SKU-025-0125` (125 products)  
  - Category 26: `SKU-026-0001` to `SKU-026-0034` (34 products shown in this chunk)
- **Product elements** contain:  
  - `<title>` – product name  
  - `<price currency="EUR">` – price value  
  - `<inv:stock warehouse="W#">` – quantity in a specific warehouse  
  - (Category 20 only) `<inv:recall batch="R-020-xxxx">` – recall batch identifier

#### Configuration
- All prices use the `currency="EUR"` attribute.
- Inventory stock is specified per `<inv:stock>` element with a `warehouse` attribute (`W1`–`W9`).
- Recall batches are identified by the `batch` attribute on `<inv:recall>` elements.

#### Dependencies
- Namespace `inv` (prefix `inv`) used for `<stock>` and `<recall>` elements.

## Chunk 4 of 5

> A fragment of a large catalog XML file listing products with SKU, title, price, and inventory stock per warehouse.

#### Type
XML catalog file; root element `<catalog>` (not shown in chunk). Declares namespace `inv` for inventory elements.

#### Definitions
- **Categories**: `cat-026`, `cat-027`, `cat-028`, `cat-029`, `cat-030`, `cat-031`, `cat-032`, `cat-033`, `cat-034` (partial).
- **Products**: Each `<product>` has attributes:
  - `sku`: unique identifier (e.g., `SKU-026-0023`).
  - Child elements:
    - `<title>`: product name.
    - `<price currency="EUR">`: price value.
    - `<inv:stock warehouse="W#">`: inventory quantity per warehouse.

#### Configuration
- Currency fixed to `EUR` for all prices.
- Inventory warehouses range from `W1` to `W9`.

#### Dependencies
- Namespace prefix `inv` mapped to an inventory schema (URL not shown).

## Chunk 5 of 5

> A fragment of a product catalog XML containing categories and their products with pricing and inventory data.

#### Purpose
- Stores product information for an e‑commerce catalog.

#### Type
- XML data file (catalog).  
- Root element: `catalog`.  
- Declares namespace prefix `inv` for inventory elements.

#### Definitions
- **Category** elements: `id`, `name`.  
- **Product** elements:  
  - `sku` attribute (e.g. `SKU-034-0102`).  
  - Child `title` element (e.g. `Product 34.102`).  
  - Child `price` element with `currency="EUR"` and numeric value.  
  - Child `inv:stock` element with `warehouse` attribute (e.g. `W6`) and quantity value.

#### Configuration
- No configurable values in this fragment.

#### Dependencies
- Namespace `inv` (used for `<inv:stock>`).

## Reduce round 1, batch 1 of 1 (5 parts)

> A comprehensive product catalog that groups items by category, lists each product’s SKU, title, EUR price, and per‑warehouse inventory, and occasionally records recall batch information.

#### Purpose
- Supplies product, pricing, and inventory data for an e‑commerce or warehouse management system.

#### Type
- XML catalog data file  
- Root element: `catalog`  
- Namespaces:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`

#### Definitions
- **Categories**: 26 (`cat-001` – `cat-026`) each with `id` and `name`.  
- **Products**: 1 250 total (≈140 per category).  
  - Each `<product>` has:  
    - `sku` attribute (e.g., `SKU-018-0039`)  
    - `<title>` element (product name)  
    - `<price currency="EUR">` element (numeric value)  
    - One or more `<inv:stock warehouse="W#">` elements (quantity per warehouse, `W1`–`W9`)  
    - (Category 20 only) optional `<inv:recall batch="R-020-xxxx">` element.  
- **Warehouses**: `W1` – `W9` referenced in stock elements.

#### Configuration
- All `<price>` elements use `currency="EUR"`.  
- Inventory quantities are specified in `<inv:stock>` with a `warehouse` attribute.  
- Recall batches are identified by the `batch` attribute on `<inv:recall>`.

#### Dependencies
- Namespace `urn:example:catalog` defines the catalog structure.  
- Namespace `urn:example:inventory` defines `<inv:stock>` and `<inv:recall>` elements.

