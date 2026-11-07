# Map-reduce answers of other/catalog/catalog.xml

## Chunk 1 of 5

> A catalog XML file listing 9 product categories, each with 125 products, including titles, prices (EUR), and inventory stock per warehouse.

#### Purpose
- Stores product catalog data for an e‑commerce system.

#### Type
- XML catalog file  
  - Root element: `<catalog>`  
  - Default namespace: `urn:example:catalog`  
  - Inventory namespace: `urn:example:inventory` (prefix `inv`)  
  - Attribute: `version="3"`

#### Definitions
- **Categories** (`<category>`)  
  - Attributes: `id` (e.g., `cat-001`), `name` (e.g., `Category 1`)  
  - 9 categories: `cat-001` … `cat-009`
- **Products** (`<product>`)  
  - Attribute: `sku` (pattern `SKU-<cat>-<NNNN>`, e.g., `SKU-001-0001`)  
  - Child elements:  
    - `<title>` – product name  
    - `<price>` – contains numeric value, attribute `currency="EUR"`  
    - `<inv:stock>` – inventory element in `inv` namespace, attributes: `warehouse` (e.g., `W1`–`W9`), text content is quantity
- **SKU pattern**: `SKU-<cat>-<0001‑0125>` (125 products per category)
- **Warehouse identifiers**: `W1` through `W9`
- **Price currency**: always `EUR`

#### Configuration
- No configurable properties or placeholders in this chunk.

#### Dependencies
- None (self‑contained XML data).

## Chunk 2 of 5

> A catalog fragment listing 42 products in Category 9, each with a SKU, title, EUR price, and warehouse‑specific stock count.

#### Purpose
- Provides product data for Category 9 in the catalog.

#### Type
- XML data file (catalog).  
- Root element: `<category>` (declared with `id="cat-009"` and `name="Category 9"`).  
- Namespace: `inv` (used for `<inv:stock>` elements).

#### Definitions
- **Category**:  
  - `id="cat-009"`  
  - `name="Category 9"`  
  - Contains 42 `<product>` elements.
- **Product** (repeated 42 times):  
  - Attribute `sku` follows pattern `SKU-009-xxxx`.  
  - Child `<title>`: product name.  
  - Child `<price>`: has attribute `currency="EUR"` and a numeric value.  
  - Child `<inv:stock>`: has attribute `warehouse="W1"–"W9"` and a numeric quantity.
- **First product**: `SKU-009-0084`, title *Product 9.84*, price 81.42 EUR, stock 478 in warehouse W1.  
- **Last product**: `SKU-009-0125`, title *Product 9.125*, price 172.99 EUR, stock 494 in warehouse W3.

#### Configuration
- No configurable values or placeholders appear in this chunk.

#### Dependencies
- Uses the `inv` namespace prefix (URI defined elsewhere in the file).  
- No external references or included schemas are present in this fragment.

## Chunk 3 of 5

> A catalog XML fragment listing products for Category 18, each with SKU, title, price, and inventory stock.

#### Purpose
- Provides product data for Category 18 in the catalog.

#### Type
- XML data file (catalog).  
- Root element: `<catalog>` (not shown in this chunk).  
- Namespace: `inv` prefix bound to an inventory namespace (URI not shown).

#### Definitions
- **Product entries**: repeated `<product>` elements with attributes:
  - `sku`: unique product identifier (e.g., `SKU-018-0037`).  
  - Child elements:
    - `<title>`: product name.  
    - `<price currency="EUR">`: price value.  
    - `<inv:stock warehouse="W#">`: inventory quantity at a specific warehouse.  
- No other element types appear in this chunk.

#### Configuration
- No configurable values or placeholders are present in this fragment.

## Chunk 4 of 5

> A fragment of a large catalog XML file containing product entries grouped by category, each product with SKU, title, price (EUR), and inventory stock per warehouse.

#### Type
XML data file – catalog of products. Root element: `<catalog>`. Declares namespace `inv` with prefix `inv` for inventory elements.

#### Definitions
- **Categories**: `<category id="cat-026" name="Category 26">`, `<category id="cat-027" name="Category 27">`, `<category id="cat-028" name="Category 28">`, `<category id="cat-029" name="Category 29">`, `<category id="cat-030" name="Category 30">`, `<category id="cat-031" name="Category 31">`, `<category id="cat-032" name="Category 32">`, `<category id="cat-033" name="Category 33">`, `<category id="cat-034" name="Category 34">`.
- **Products**: Each `<product>` has attribute `sku`. Inside:
  - `<title>` – product name.
  - `<price currency="EUR">` – price value.
  - `<inv:stock warehouse="W#">` – inventory quantity for a warehouse (namespace `inv`).
- **Namespace**: `inv` prefix bound to `http://example.com/inventory` (not shown in chunk but implied by usage).

#### Configuration
No configuration elements present in this chunk.

#### Dependencies
None. The file only contains data records.

## Chunk 5 of 5

> A fragment of a product catalog XML listing detailed inventory for categories 35‑40, including SKU, title, price, and warehouse stock.

#### Purpose
- Stores product inventory data for categories 35‑40.

#### Type
- XML data file (catalog). Root element: `catalog`. Declares namespace `inv` for inventory stock elements.

#### Definitions
- **Categories**  
  - `id` and `name` attributes: `cat-035` to `cat-040`.  
- **Products** (within each category)  
  - `sku` attribute (e.g., `SKU-035-0001`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price>` – contains `currency="EUR"` and numeric value.  
    - `<inv:stock>` – contains `warehouse` attribute (e.g., `W4`) and quantity value.

#### Configuration
- No configurable properties; all values are explicit data entries.

#### Dependencies
- Namespace `inv` (prefix `inv`) for `<inv:stock>` elements. No external references.

## Reduce round 1, batch 1 of 1 (5 parts)

> A single XML catalog file that defines a hierarchical product inventory for an e‑commerce platform, organizing products into numbered categories, each product with a SKU, title, EUR price, and per‑warehouse stock counts.

#### Purpose
- Stores product catalog and inventory data for an e‑commerce system.

#### Type
- XML catalog file  
  - Root element: `<catalog>`  
  - Default namespace: `urn:example:catalog`  
  - Inventory namespace: `urn:example:inventory` (prefix `inv`)  
  - Attribute: `version="3"`

#### Definitions
- **Categories** (`<category>`)  
  - Attributes: `id` (`cat-001` … `cat-040`), `name` (`Category 1` … `Category 40`)  
  - 40 category elements, each containing a variable number of `<product>` children (e.g., 9 categories with 125 products each, Category 9 with 42 products, Category 18 with an unspecified count, Categories 26‑34 each with a full set, Categories 35‑40 with detailed inventory).  
- **Products** (`<product>`)  
  - Attribute: `sku` (`SKU-<cat>-<NNNN>`, e.g., `SKU-009-0084`)  
  - Child elements:  
    - `<title>` – product name  
    - `<price>` – numeric value, attribute `currency="EUR"`  
    - `<inv:stock>` – inventory element in `inv` namespace, attributes: `warehouse` (`W1`–`W9`), text content is quantity  
- **SKU pattern**: `SKU-<cat>-<0001‑0125>` (125 products per category in the main dataset)  
- **Warehouse identifiers**: `W1` through `W9`  
- **Price currency**: always `EUR`

#### Configuration
- No configurable properties or placeholders are present in the file.

#### Dependencies
- Uses the `inv` namespace prefix bound to `urn:example:inventory`.  
- No external schemas, XSDs, or included files are referenced.

