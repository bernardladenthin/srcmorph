# Map-reduce answers of other/catalog/catalog.xml

## Chunk 1 of 5

> A catalog of products grouped by category, each product with title, price and inventory stock per warehouse.

#### Purpose
- Provides product data for an e‑commerce catalog.

#### Type
- XML file, root element `<catalog>` in namespace `urn:example:catalog` with prefix `inv` for `urn:example:inventory`.  
- Declares `xmlns="urn:example:catalog"` and `xmlns:inv="urn:example:inventory"`.

#### Definitions
- **Categories**:  
  - `cat-001` – “Category 1”  
  - `cat-002` – “Category 2”  
  - `cat-003` – “Category 3”  
  - `cat-004` – “Category 4”  
  - `cat-005` – “Category 5”  
  - `cat-006` – “Category 6”  
  - `cat-007` – “Category 7”  
  - `cat-008` – “Category 8”  
  - `cat-009` – “Category 9”  
  - `cat-010` – “Category 10” (not shown in this chunk)
- **Products**: each `<product>` has attributes:  
  - `sku` – unique product identifier (e.g., `SKU-001-0001`).  
  - Child elements:  
    - `<title>` – product name.  
    - `<price>` – contains `currency="EUR"` and numeric value.  
    - `<inv:stock>` – contains `warehouse` attribute (e.g., `W1`) and quantity value.  
- **Inventory**: each `<inv:stock>` element indicates stock level for a specific warehouse.

#### Configuration
- `catalog` element has attribute `version="3"`.  
- No other configurable values in this chunk.

#### Dependencies
- Namespace `urn:example:catalog` (default).  
- Namespace `urn:example:inventory` (prefix `inv`).  
- No external files or classes referenced.

## Chunk 2 of 5

> A catalog XML fragment listing product SKUs, titles, prices (EUR), and inventory stock per warehouse across categories 9–18.

#### Type
XML catalog file; root element `<catalog>` (not shown); namespace `inv` declared with prefix `inv` for inventory elements.

#### Definitions
- **Categories**: `cat-009` to `cat-018` (10 categories).  
- **Products**: each `<product>` has attributes `sku` (e.g. `SKU-009-0090`), child `<title>`, `<price currency="EUR">`, and `<inv:stock warehouse="W#">` with quantity.  
- **Warehouse**: `W1`–`W9`; each product references one warehouse.  
- **Price**: all prices are in `EUR`.  

#### Configuration
- `inv:stock` namespace prefix: `inv`.  
- No other configurable values in this chunk.

#### Dependencies
- Namespace `inv` (no external schema referenced in this chunk).

## Chunk 3 of 5

> A catalog XML fragment listing product entries across multiple categories, each with SKU, title, price, and inventory stock (and recall data for Category 20).

#### Type
XML catalog file; root element `<catalog>` (not shown); namespaces: `inv` prefix bound to an inventory namespace.

#### Definitions
- **Category** elements: `id`, `name`.  
- **Product** elements: `sku` attribute; child elements `<title>`, `<price>` (attribute `currency`), `<inv:stock>` (attribute `warehouse`), and optionally `<inv:recall>` (attribute `batch`).  

#### Configuration
- Prices are in `EUR`.  
- Inventory stock values are integers.  
- Recall batches are strings like `R-020-0001`.

#### Dependencies
- Namespace `inv` (inventory).  
- No external references.

#### Product families (by SKU pattern)
| Category | SKU pattern | Count | First SKU | Last SKU |
|----------|-------------|-------|-----------|----------|
| cat‑018 | `SKU-018-00xx` | 89 | `SKU-018-0037` | `SKU-018-0125` |
| cat‑019 | `SKU-019-000x` | 105 | `SKU-019-0001` | `SKU-019-0105` |
| cat‑020 | `SKU-020-000x` | 106 | `SKU-020-0001` | `SKU-020-0106` |
| cat‑021 | `SKU-021-000x` | 105 | `SKU-021-0001` | `SKU-021-0105` |
| cat‑022 | `SKU-022-000x` | 105 | `SKU-022-0001` | `SKU-022-0105` |
| cat‑023 | `SKU-023-000x` | 105 | `SKU-023-0001` | `SKU-023-0105` |
| cat‑024 | `SKU-024-000x` | 105 | `SKU-024-0001` | `SKU-024-0105` |
| cat‑025 | `SKU-025-000x` | 105 | `SKU-025-0001` | `SKU-025-0105` |
| cat‑026 | `SKU-026-000x` | 105 | `SKU-026-0001` | `SKU-026-0105` |

#### Notable attributes
- `<price currency="EUR">` holds the monetary value.  
- `<inv:stock warehouse="W#">` holds the quantity in a specific warehouse.  
- Category 20 products include `<inv:recall batch="R-020-xxxx">` indicating supplier recall batches.

## Chunk 4 of 5

> A fragment of a catalog XML listing products with titles, prices, and warehouse stock levels across multiple categories.

#### Purpose
- Stores product inventory data for a catalog.

#### Type
- XML data file; root element `<catalog>` (not shown in chunk).  
- Namespace `inv` declared for `<inv:stock>` elements.

#### Definitions
- **Product entries**: `<product sku="...">` with child `<title>`, `<price currency="EUR">`, and `<inv:stock warehouse="...">`.  
- **SKU pattern**: `SKU-<cat>-<num>` where `<cat>` is a three‑digit category number and `<num>` is a four‑digit product number.  
- **Warehouse pattern**: `warehouse="W<digit>"` (W1–W9).  

#### Configuration
- No configurable attributes or placeholders in this chunk.

#### Dependencies
- Namespace prefix `inv` refers to an external inventory schema (not expanded here).

## Chunk 5 of 5

> A fragment of a product catalog XML containing categories 35‑40 with detailed product listings, prices, and inventory stock per warehouse.

#### Purpose
- Stores product information for categories 35‑40.
- Provides price and inventory data for each product.

#### Type
- XML catalog file.
- Root element: `<catalog>`.
- Declares namespace `inv` for inventory elements.

#### Definitions
- **Category**: `<category id="cat-XXX" name="Category X">`
  - `id`: unique identifier (e.g., `cat-035`).
  - `name`: human‑readable name.
- **Product**: `<product sku="SKU-XXX-YYYY">`
  - `sku`: unique product code.
  - Child elements:
    - `<title>`: product title.
    - `<price currency="EUR">` : price value.
    - `<inv:stock warehouse="Wn">` : stock quantity in warehouse `Wn`.

#### Configuration
- None.

#### Dependencies
- Namespace `inv` (used for `<inv:stock>` elements).

## Reduce round 1, batch 1 of 1 (5 parts)

> A catalog XML file that defines product data, pricing, and inventory stock per warehouse across many categories.

#### Purpose
- Provides product inventory data for an e‑commerce catalog.

#### Type
- XML data file, root element `<catalog>` in namespace `urn:example:catalog` (default).  
- Declares `xmlns="urn:example:catalog"` and `xmlns:inv="urn:example:inventory"` for inventory elements.

#### Definitions
- **Categories**  
  - `cat-001` – “Category 1”  
  - `cat-002` – “Category 2”  
  - `cat-003` – “Category 3”  
  - `cat-004` – “Category 4”  
  - `cat-005` – “Category 5”  
  - `cat-006` – “Category 6”  
  - `cat-007` – “Category 7”  
  - `cat-008` – “Category 8”  
  - `cat-009` – “Category 9”  
  - `cat-010` – “Category 10” (not shown in this chunk)  
  - `cat-011`–`cat-018` – categories 11–18 (not fully listed)  
  - `cat-019`–`cat-026` – categories 19–26 (product families detailed below)  
  - `cat-035`–`cat-040` – categories 35–40 (product listings shown in part 5)  
- **Products** – each `<product>` element has:  
  - `sku` attribute (e.g., `SKU-018-0037`).  
  - Child `<title>` – product name.  
  - Child `<price currency="EUR">` – numeric value.  
  - Child `<inv:stock warehouse="W#">` – integer quantity.  
  - Optional `<inv:recall batch="R-020-xxxx">` for Category 20 products.  
- **Inventory** – `<inv:stock>` elements indicate stock level for a specific warehouse (`W1`–`W9`).  
- **Recall** – `<inv:recall>` elements specify supplier recall batch identifiers.

#### Product families (by SKU pattern)
| Category | SKU pattern | Count | First SKU | Last SKU |
|----------|-------------|-------|-----------|----------|
| cat‑018 | `SKU-018-00xx` | 89 | `SKU-018-0037` | `SKU-018-0125` |
| cat‑019 | `SKU-019-000x` | 105 | `SKU-019-0001` | `SKU-019-0105` |
| cat‑020 | `SKU-020-000x` | 106 | `SKU-020-0001` | `SKU-020-0106` |
| cat‑021 | `SKU-021-000x` | 105 | `SKU-021-0001` | `SKU-021-0105` |
| cat‑022 | `SKU-022-000x` | 105 | `SKU-022-0001` | `SKU-022-0105` |
| cat‑023 | `SKU-023-000x` | 105 | `SKU-023-0001` | `SKU-023-0105` |
| cat‑024 | `SKU-024-000x` | 105 | `SKU-024-0001` | `SKU-024-0105` |
| cat‑025 | `SKU-025-000x` | 105 | `SKU-025-0001` | `SKU-025-0105` |
| cat‑026 | `SKU-026-000x` | 105 | `SKU-026-0001` | `SKU-026-0105` |

#### Configuration
- `catalog` element has attribute `version="3"`.  
- All `<price>` elements use `currency="EUR"`.  
- All `<inv:stock>` elements contain integer quantities.  
- Recall batches are strings like `R-020-xxxx`.

#### Dependencies
- Namespace `urn:example:catalog` (default).  
- Namespace `urn:example:inventory` (prefix `inv`).  
- No external schemas, classes, or files referenced.

