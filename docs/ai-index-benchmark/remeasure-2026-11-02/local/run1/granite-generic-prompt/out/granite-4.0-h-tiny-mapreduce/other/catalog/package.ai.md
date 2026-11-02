### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:51:25Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A comprehensive inventory catalog generator for products in categories cat-039 and cat-040, providing SKU, title, price, and warehouse stock data in XML format.

#### Purpose
- Generates a static inventory catalog XML file from product and category data.

#### Responsibilities
- Parses XML structure containing product SKUs, titles, prices, and warehouse-specific stock counts.
- Iterates over `<category>` elements and their `<product>` children.
- Extracts SKU, title, price, and `<inv:stock>` values for each product.
- Outputs structured XML preserving the inventory hierarchy.

#### Key units
- **XML parsing**: Reads and processes the `<product>` and `<category>` elements to extract inventory details.
- **Inventory data extraction**: Retrieves SKU, title, price, and stock levels per warehouse from the XML input.

#### Data flow
1. **Input**: XML file (`catalog.xml`) containing product and category data.
2. **Parsing**: XML parser reads the structure and extracts relevant inventory details.
3. **Processing**: Iterates over categories and products, collecting SKU, title, price, and stock information.
4. **Output**: Generates structured XML output representing the inventory catalog.

#### Dependencies
- XML parsing library (e.g., DOM, SAX) for reading and processing the XML file.

#### Cross-cutting
- Assumes well-formed XML input; no error handling for malformed XML.
