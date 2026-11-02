### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:48:47Z
- G: 
- A: 0.0.0
- X: file
- P: 6047603A
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 7 of 7 chunks summarized (1 part summary cut at the output budget)

**Purpose**  
Provides a complete inventory catalog for products in categories `cat-039` and `cat-040`, listing each product's SKU, title, price, and stock levels per warehouse.

**Type**  
XML file (catalog.xml) – schema defined by `<product>` and `<category>` elements; static data, not compiled code.

**Input**  
- XML structure: product SKUs, titles, prices, and warehouse‑specific stock counts  
- No external files or runtime parameters are consumed

**Output**  
- Structured XML output representing the inventory data  
- No return values or side effects; purely data generation

**Core logic**  
- Iterates over `<category>` elements  
- For each category, iterates over `<product>` children  
- Extracts SKU, title, price, and warehouse‑specific `<inv:stock>` values  
- Outputs XML preserving hierarchy

**Public API**  
N/A – static data file; no public interface

**Constants and configuration**  
- None defined or read

**Dependencies**  
- XML parser library (e.g., DOM, SAX) for reading XML

**Exceptions / Errors**  
- None handled; assumes well‑formed XML input
