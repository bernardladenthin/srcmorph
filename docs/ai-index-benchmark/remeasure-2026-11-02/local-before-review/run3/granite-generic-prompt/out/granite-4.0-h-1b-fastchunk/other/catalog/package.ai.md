### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T22:06:01Z
- T: 2026-11-02T00:21:20Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
**<one-sentence domain essence>**  
The package provides a Java class that parses a catalog XML file, extracting product details (SKU, title, price, and stock quantity per warehouse) and exposing them through a set of getter methods.

#### Purpose
The overall responsibility of this package is to parse a catalog XML file and expose the product data (SKU, title, price, and stock quantity per warehouse) in a convenient, object‑oriented form.

#### Responsibilities
- **Parsing XML**: Use `DocumentBuilderFactory`/`DocumentBuilder` to read the catalog XML file and create a `Document` object.
- **Extracting Data**: Iterate over `<product>` elements, retrieve `sku`, `title`, `price`, and `inv:stock` values.
- **Exposing Data**: Provide getter methods (`sku()`, `title()`, `price()`, `stock()`) that return the extracted values as strings or `EUR` price.

#### Key units
- `ProductParser`: A class that encapsulates the parsing logic and exposes the data via getter methods.

#### Data flow
- **Input**: A `Document` object representing the catalog XML.
- **Processing**: Iterate over `<product>` elements, extract `sku`, `title`, `price`, and `inv:stock`.
- **Output**: The package returns the extracted values through the getter methods.

#### Dependencies
- **External libraries**: `DocumentBuilderFactory`, `DocumentBuilder` (from `javax.xml.parsers`).
- **No direct dependencies on other classes or modules** within this package.

#### Cross-cutting
- **No shared state or configuration**; each call to the getter methods is independent.
- **No external configuration files**; the parser reads the XML directly.

#### Key concepts
- **Document parsing**: Utilizes `DocumentBuilderFactory` and `DocumentBuilder` to read XML.
- **Data extraction**: Iterates over XML elements, extracts specific attributes.
- **Object-oriented representation**: Returns product data as `String` or `EUR` price.

#### Rules
- Markdown bullets and concise clauses, no prose paragraphs.
- Weave type / module / table / domain names in as searchable terms.
- Base every statement only on the provided file summaries.
