### catalog.xml
- H: 1.0
- C: 754E8810
- D: 2026-10-31T21:24:26Z
- T: 2026-10-31T21:49:01Z
- G: 
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** elements: 20041; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Purpose**  
This XML file defines a product catalog, listing multiple product entries with titles, prices, and stock quantities for various warehouses.

**Type**  
XML (Document)

**Input**  
- No parameters or consumed fields specified.

**Output**  
- No return types or produced state specified.

**Core Logic**  
- The XML document contains a `<category>` element with an `id` attribute.
- Each `<product>` element within the category includes:
  - `<sku>`: Product SKU
  - `<title>`: Product title
  - `<price>`: Price in EUR
  - `<inv:stock>`: Stock quantity for a specific warehouse (`W1` to `W9`)

**Public API**  
- No explicit public API; the XML document is consumed by other applications or services.

**Constants and Configuration**  
- No named constants, macros, or configuration values defined in the XML.

**Dependencies**  
- No external dependencies or libraries referenced.

**Exceptions / Errors**  
- No exception handling or error conditions specified.

**Note**: The provided XML snippet is truncated and does not include the complete document.
