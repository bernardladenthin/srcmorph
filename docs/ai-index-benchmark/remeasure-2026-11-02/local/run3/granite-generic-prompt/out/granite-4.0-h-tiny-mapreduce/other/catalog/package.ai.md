### other/catalog
- H: 1.0
- C: 8C443AA2
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T02:15:12Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog.xml](catalog.xml.ai.md)
---
> A static inventory catalog generator for products in categories cat-039 and cat-040, listing SKUs, titles, prices, and warehouse stock levels.

#### Purpose
- Generates a structured XML inventory catalog from product data.

#### Responsibilities
- Reads product and category XML elements.
- Extracts SKU, title, price, and warehouse stock information.
- Outputs XML preserving the hierarchical structure.

#### Key units
- **Product data extraction**: Iterates over `<category>` and `<product>` elements to retrieve SKU, title, price, and stock levels.
- **XML generation**: Constructs XML output preserving the category and product hierarchy.

#### Data flow
1. Parse the `catalog.xml` file.
2. Iterate over `<category>` elements.
3. For each category, iterate over `<product>` children.
4. Extract SKU, title, price, and `<inv:stock>` values.
5. Output the structured XML preserving the hierarchy.

#### Dependencies
- XML parsing library (e.g., DOM, SAX) for reading the `catalog.xml` file.

#### Cross-cutting
- Assumes well-formed XML input; no error handling implemented.
