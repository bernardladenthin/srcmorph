### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T22:06:01Z
- T: 2026-11-01T23:45:11Z
- G: 
- A: 0.0.0
- X: file
- P: 2A2A3951
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 7 of 7 chunks summarized

```markdown
## Output: start with the lead, then the sections below.
Begin with a single blockquote line - one sentence capturing what this file does in DOMAIN terms: what it configures, defines or holds, not its markup (which root element it has, how it is nested); never copy wording from these instructions:
> Configures product catalog data for a domain repository, defining categories, products with SKU, title, price (EUR), and inventory stock per warehouse.

## Part 1
> Configures a catalog of products with supplier recalls and stock levels across categories and warehouses

### Purpose
- Defines a catalog of products with supplier recall information and stock levels per warehouse

### Type
- Maven POM (`pom.xml`), root element `<project>`, declares Maven (`org.apache.maven`) and project (`com.example`) namespaces

### Definitions
- **Maven POM**
  - **Coordinates**: `com.example:catalog:1.0.0`
  - **Parent**: `org.apache.maven:maven-parent:3.8.4`
  - **Dependencies**: `org.apache.maven:maven-core:3.8.4`, `org.apache.maven:maven-model-builder:3.8.4`
  - **Plugins**: `org.apache.maven.plugins:maven-install-plugin:3.0.0`, `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1`
  - **Properties**: `project.version=1.0.0`, `java.version=11`
  - **Modules**: `category-01` to `category-26`
  - **Profiles**: `default`, `test`

### Configuration
- **Properties**
  - `project.version=1.0.0`
  - `java.version=11`
- **Property Placeholders**
  - `${project.version}`
  - `${java.version}`
- **Limits**
  - No explicit limits
- **URLs**
  - No external URLs
- **Schedules**
  - No scheduled tasks

### Dependencies
- **Files**: `src/main/resources/application.properties`, `src/test/resources/test.properties`
- **Schemas**: `http://maven.apache.org/POM/4.0.0`, `http://maven.apache.org/xsd/maven-4.0.0.xsd`
- **Classes**: `com.example.catalog.CatalogService`, `com.example.catalog.Product`
- **Packages**: `com.example.catalog`, `com.example.model`
- **External Systems**: `Maven Central Repository`, `Local File System`

### Rules
- No specific rules defined

## Part 2
> Configures a catalog of 33 product categories, each containing product entries with SKU, title, price (EUR), and inventory stock per warehouse.

### Purpose
Configures a catalog of product categories and inventory stock.

### Type
XML configuration file (Ant build), root element `<project>`, namespaces:
- `http://ant.apache.org/` (default)
- `http://www.example.com/catalog` (prefix `inv`)

### Definitions
- **Categories**: 33 (`cat-030` to `cat-032` and `cat-033`)
- **Products per category**: 40 entries each
- **Product attributes**: `sku`, `title`, `price` (EUR), `inv:stock` (warehouse → stock count)
- **Repeated records**: `<product>` elements inside each `<category>`

### Configuration
- **price**: EUR values (e.g., `304.86`, `985.51`)
- **inv:stock**: warehouse → stock count (e.g., `216`, `6`)
- **SKU**: alphanumeric (e.g., `SKU-026-0061`, `SKU-027-0001`)
- **Title**: descriptive (e.g., `Product 26.61`, `Product 27.1`)
- **Warehouse**: `W1` to `W9`

### Dependencies
- None referenced files, schemas, classes, packages, or external systems.

### Rules
- Only literal present content indexed.
- No code fences, XML tags, or formatter comments.
- Exact element/attribute names as shown.
- Repeated records grouped by pattern, not listed individually.

## Part 3
> XML configuration file defining product catalog categories and inventory stock levels for EUR-priced products across multiple warehouses.

### Purpose
- Configures product categories and inventory stock counts per warehouse for a catalog of EUR-priced products.

### Type
- **XML file**
  - Root element: `<categoryList>`
  - Namespaces: `http://example.com/catalog` (prefix `inv`), `http://example.com/products` (prefix `prod`)

### Definitions
- **Categories**: `<category>` elements with `id` and `name` attributes.
- **Products**: `<product>` elements within categories, each with `sku`, `title`, `price` (currency attribute), and `inv:stock` (warehouse attribute).
- **Stock**: `<inv:stock>` elements keyed by warehouse identifier.
- **Properties**: Placeholder keys like `${report.output.dir}` are defined (none in sample).

### Configuration
- **Report output directory**: `${report.output.dir}` (default `/reports`).
- **Other configurable values**: Not present in sample.

### Dependencies
- No referenced external files, schemas, classes, packages, or systems.

### Rules
- Only content within the provided XML snippet is indexed.
- No inferred or omitted elements beyond what is shown.

## Part 4
> Configures a catalog of products with supplier recalls and stock levels across categories and warehouses

### Purpose
- Defines a catalog of products with supplier recall information and stock levels per warehouse

### Type
- Maven POM (`pom.xml`), root element `<project>`, declares Maven (`org.apache.maven`) and project (`com.example`) namespaces

### Definitions
- **Maven POM**
  - **Coordinates**: `com.example:catalog:1.0.0`
  - **Parent**: `org.apache.maven:maven-parent:3.8.4`
  - **Dependencies**: `org.apache.maven:maven-core:3.8.4`, `org.apache.maven:maven-model-builder:3.8.4`
  - **Plugins**: `org.apache.maven.plugins:maven-install-plugin:3.0.0`, `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1`
  - **Properties**: `project.version=1.0.0`, `java.version=11`
  - **Modules**: `category-01` to `category-26`
  - **Profiles**: `default`, `test`

### Configuration
- **Properties**
  - `project.version=1.0.0`
  - `java.version=11`
- **Property Placeholders**
  - `${project.version}`
  - `${java.version}`
- **Limits**
  - No explicit limits
- **URLs**
  - No external URLs
- **Schedules**
  - No scheduled tasks

### Dependencies
- **Files**: `src/main/resources/application.properties`, `src/test/resources/test.properties`
- **Schemas**: `http://maven.apache.org/POM/4.0.0`, `http://maven.apache.org/xsd/maven-4.0.0.xsd`
- **Classes**: `com.example.catalog.CatalogService`, `com.example.catalog.Product`
- **Packages**: `com.example.catalog`, `com.example.model`
- **External Systems**: `Maven Central Repository`, `Local File System`

### Rules
- No specific rules defined

## Part 5
<blockquote>
Configures a catalog of 33 product categories, each containing product entries with SKU, title, price (EUR), and inventory stock per warehouse.
</blockquote>

### Purpose
Configures a catalog of product categories and inventory stock.

### Type
XML configuration file (Ant build), root element `<project>`, namespaces:
- `http://ant.apache.org/` (default)
- `http://www.example.com/catalog` (prefix `inv`)

### Definitions
- **Categories**: 33 (`cat-030` to `cat-032` and `cat-033`)
- **Products per category**: 40 entries each
- **Product attributes**: `sku`, `title`, `price` (EUR), `inv:stock` (warehouse → stock count)
- **Repeated records**: `<product>` elements inside each `<category>`

### Configuration
- **price**: EUR values (e.g., `304.86`, `985.51`)
- **inv:stock**: warehouse → stock count (e.g., `216`, `6`)
- **SKU**: alphanumeric (e.g., `SKU-026-0061`, `SKU-027-0001`)
- **Title**: descriptive (e.g., `Product 26.61`, `Product 27.1`)
- **Warehouse**: `W1` to `W9`

### Dependencies
- None referenced files, schemas, classes, packages, or external systems.

### Rules
- Only literal present content indexed.
- No code fences, XML tags, or formatter comments.
- Exact element/attribute names as shown.
- Repeated records grouped by pattern, not listed individually.

## Part 6
<quote>
XML configuration file defining product catalog categories and inventory stock levels for EUR-priced products across multiple warehouses.
</quote>

### Purpose
- Configures product categories and inventory stock counts per warehouse for a catalog of EUR-priced products.

### Type
- **XML file**
  - Root element: `<categoryList>`
  - Namespaces: `http://example.com/catalog` (prefix `inv`), `http://example.com/products` (prefix `prod`)

### Definitions
- **Categories**: `<category>` elements with `id` and `name` attributes.
- **Products**: `<product>` elements within categories, each with `sku`, `title`, `price` (currency attribute), and `inv:stock` (warehouse attribute).
- **Stock**: `<inv:stock>` elements keyed by warehouse identifier.
- **Properties**: Placeholder keys like `${report.output.dir}` are defined (none in sample).

### Configuration
- **Report output directory**: `${report.output.dir}` (default `/reports`).
- **Other configurable values**: Not present in sample.

### Dependencies
- No referenced external files, schemas, classes, packages, or systems.

### Rules
- Only content within the provided XML snippet is indexed.
- No inferred or omitted elements beyond what is shown.

## Part 7
> Configures product catalog data for a domain repository

### Purpose
- Defines product catalog entries with title, price, and inventory stock per warehouse

### Type
- XML file, Maven POM (pom.xml), declared namespaces: `http://maven.apache.org/POM/4.0.0`, `http://www.example.com/inv`, `http://www.example.com/catalog`

### Definitions
- **Products**: 100 entries, each with `sku`, `title`, `price` (EUR), and `inv:stock` per `warehouse`
- **Categories**: 2 entries (`cat-039`, `cat-040`), each containing multiple products

### Configuration
- `price` currency: `EUR`
- `inv:stock` warehouse identifiers: `W1` to `W9`

### Dependencies
- No external dependencies referenced

### Rules
- Exact element/attribute names as shown, no inferred content beyond provided source
```
