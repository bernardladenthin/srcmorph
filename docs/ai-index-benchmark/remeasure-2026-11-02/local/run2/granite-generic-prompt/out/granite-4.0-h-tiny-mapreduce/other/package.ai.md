### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T01:33:25Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A comprehensive inventory catalog generator for products in categories cat-039 and cat-040, providing SKU, title, price, and warehouse stock data in XML format.

#### Purpose
- Generates a structured inventory catalog XML file from product and category data.

#### Responsibilities
- Reads product and category XML data.
- Extracts SKU, title, price, and warehouse-specific stock information.
- Outputs XML preserving the hierarchical structure of categories and products.

#### Key units
- **XML parsing**: Reads and processes `<category>` and `<product>` elements.
- **Data extraction**: Retrieves SKU, title, price, and stock levels for each product.
- **XML generation**: Constructs and outputs the inventory catalog XML.

#### Data flow
1. **Input**: XML file containing product and category data.
2. **Processing**: Iterates over `<category>` elements, then iterates over `<product>` children within each category.
3. **Output**: Structured XML representing the inventory catalog.

#### Dependencies
- XML parsing library (e.g., DOM, SAX) for reading and processing XML data.

#### Cross-cutting
- Assumes well-formed XML input; no error handling for malformed data.
