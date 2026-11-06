# Map-reduce answers of other/catalog/catalog.xml

## Chunk 1 of 5

> A catalog XML file listing products grouped by category, each with title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Stores product inventory data for a multi‑warehouse e‑commerce system.

#### Type
- XML catalog file  
  - Root element: `catalog` (namespace `urn:example:catalog`)  
  - Namespace prefix: `inv` → `urn:example:inventory`  
  - Attribute: `version="3"`

#### Definitions
- **Categories**: 9 (`cat-001` … `cat-009`)  
  - Each `<category>` has attributes `id` and `name`.  
  - Contains a sequence of `<product>` elements.  
- **Products**: 1250 total (≈140 per category)  
  - `<product>` attributes: `sku` (e.g., `SKU-001-0001`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price currency="EUR">` – numeric value.  
    - `<inv:stock warehouse="W#">` – integer quantity.  
- **Warehouses**: W1–W9, referenced in `inv:stock` elements.

#### Configuration
- No configurable values; all data is literal.

#### Dependencies
- Namespace declarations:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`  
- No external references or includes.

## Chunk 2 of 5

> A catalog fragment listing products with titles, prices (EUR), and warehouse stock levels across multiple categories.

#### Purpose
- Provides product inventory data for a catalog.

#### Type
- XML data file; root element `<catalog>` (not shown in chunk).  
- Namespace `inv` declared for `<inv:stock>` elements.

#### Definitions
- **Categories**: `cat-009` to `cat-018` (partial).  
- **Products**: Each `<product>` has attributes:
  - `sku` (e.g., `SKU-009-0090`).
  - Child `<title>` (e.g., `Product 9.90`).
  - Child `<price>` with `currency="EUR"` and numeric value.
  - Child `<inv:stock>` with `warehouse` attribute and quantity.

#### Configuration
- No configurable properties or placeholders in this fragment.

#### Dependencies
- Namespace prefix `inv` refers to an inventory schema (URL not shown).

## Chunk 3 of 5

> A catalog fragment listing products, their prices, stock levels, and recall flags across multiple categories.

#### Purpose
- Provides inventory data for a retail catalog.

#### Type
- XML data file (catalog), root element `<catalog>`.
- Declares namespace `inv` for inventory elements.

#### Definitions
- **Categories**: `cat-018` to `cat-026` (9 categories in this chunk).
- **Products**: Each `<product>` has attributes `sku`, child `<title>`, `<price currency="EUR">`, `<inv:stock warehouse="...">`.  
  - Category 20 products additionally contain `<inv:recall batch="...">`.
- **Namespaces**: `inv` prefix bound to `http://example.com/inventory`.

#### Configuration
- No configurable properties; all values are literal data.

#### Dependencies
- Namespace `inv` (inventory) referenced in `<inv:stock>` and `<inv:recall>` elements.

## Chunk 4 of 5

> A catalog XML fragment listing products with titles, prices, and warehouse stock levels.

#### Purpose
- Holds product inventory data for multiple categories.

#### Type
- XML data file; root element `<catalog>` (not shown in chunk).  
- No namespaces declared in this fragment.

#### Definitions
- **Categories**: `cat-026` to `cat-033`.  
- **Products**: Each `<product>` has attributes `sku`, child `<title>`, `<price currency="EUR">`, and `<inv:stock warehouse="W#">`.  
- **Warehouses**: `W1`–`W9`.  
- **Product counts**:  
  - `cat-026`: 25 products (SKU-026-0023 to SKU-026-0050).  
  - `cat-027`: 50 products (SKU-027-0001 to SKU-027-0050).  
  - `cat-028`: 50 products (SKU-028-0001 to SKU-028-0050).  
  - `cat-029`: 50 products (SKU-029-0001 to SKU-029-0050).  
  - `cat-030`: 50 products (SKU-030-0001 to SKU-030-0050).  
  - `cat-031`: 50 products (SKU-031-0001 to SKU-031-0050).  
  - `cat-032`: 50 products (SKU-032-0001 to SKU-032-0050).  
  - `cat-033`: 50 products (SKU-033-0001 to SKU-033-0050).  

#### Configuration
- Prices are in EUR.  
- Stock quantities are integers; some warehouses have zero stock (e.g., `SKU-032-0070` in `W1`).  

#### Dependencies
- No external references or schemas are present in this chunk.

## Chunk 5 of 5

> A catalog XML file listing products grouped by category, each with SKU, title, price (EUR), and inventory stock per warehouse.

#### Purpose
- Provides product inventory data for an e‑commerce system.

#### Type
- XML catalog (`<catalog>` root).  
- Namespace prefix `inv` used for `<inv:stock>` elements (target namespace not shown).

#### Definitions
- **Categories**  
  - `cat-035` – 100 products (`SKU-035-0001` to `SKU-035-0100`).  
  - `cat-036` – 100 products (`SKU-036-0001` to `SKU-036-0100`).  
  - `cat-037` – 125 products (`SKU-037-0001` to `SKU-037-0125`).  
  - `cat-038` – 125 products (`SKU-038-0001` to `SKU-038-0125`).  
  - `cat-039` – 125 products (`SKU-039-0001` to `SKU-039-0125`).  
  - `cat-040` – 125 products (`SKU-040-0001` to `SKU-040-0125`).

- **Products** (sample pattern)  
  - `sku`: unique identifier (`SKU-XXX-YYYY`).  
  - `<title>`: human‑readable name.  
  - `<price currency="EUR">`: decimal value.  
  - `<inv:stock warehouse="Wn">`: integer quantity per warehouse (`W1`–`W9`).

#### Configuration
- None.

#### Dependencies
- None.

## Reduce round 1, batch 1 of 1 (5 parts)

> A comprehensive product inventory catalog for a multi‑warehouse e‑commerce platform, organized by category and containing detailed pricing and stock information.

#### Purpose
- Stores product inventory data for a multi‑warehouse e‑commerce system.

#### Type
- XML catalog file  
  - Root element: `catalog` (namespace `urn:example:catalog`)  
  - Namespace prefix: `inv` → `urn:example:inventory` (or `http://example.com/inventory` in later fragments)  
  - Attribute: `version="3"`

#### Definitions
- **Categories**: 40 (`cat-001` … `cat-040`)  
  - Each `<category>` has attributes `id` and `name`.  
  - Contains a sequence of `<product>` elements.  
- **Products**: 1250 total (≈140 per category)  
  - `<product>` attributes: `sku` (e.g., `SKU-001-0001`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price currency="EUR">` – numeric value.  
    - `<inv:stock warehouse="W#">` – integer quantity per warehouse (`W1`–`W9`).  
    - Optional `<inv:recall batch="...">` for recalled items.  
- **Warehouses**: `W1`–`W9`, referenced in `inv:stock` elements.

#### Configuration
- No configurable values; all data is literal.

#### Dependencies
- Namespace declarations:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"` (or `http://example.com/inventory` in some fragments)  
- No external references or includes.

