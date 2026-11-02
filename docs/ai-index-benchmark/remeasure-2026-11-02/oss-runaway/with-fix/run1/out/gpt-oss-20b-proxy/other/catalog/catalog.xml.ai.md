### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:31:28Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A single XML catalog that lists thousands of products across many categories, each with SKU, title, EUR price, and per‑warehouse inventory stock, optionally including recall data.

#### Purpose
- Stores product inventory data for an e‑commerce catalog.

#### Type
- XML document  
- Root element: `catalog` (namespace `urn:example:catalog`)  
- Declares namespace prefix `inv` → `http://example.com/inventory` (or `urn:example:inventory` in some fragments)

#### Definitions
- **Categories**  
  - `<category>` elements with attributes `id` (e.g., `cat-001`) and `name` (e.g., `Category 1`).  
  - Total categories: 26 – 40 (as listed across parts).  
- **Products** (`<product>`):  
  - Attribute `sku` (e.g., `SKU-018-0091`).  
  - Child `<title>` – product name.  
  - Child `<price>` – contains `currency="EUR"` and numeric value.  
  - Child `<inv:stock>` – contains `warehouse` attribute (e.g., `W1`) and quantity text.  
  - Optional child `<inv:recall>` – contains `batch` attribute (e.g., `R-020-0001`) and text `supplier recall`.  
- **Recall batches** follow pattern `R-<category>-<number>`.

#### Configuration
- Prices are in `EUR`.  
- Stock quantities are integers.  
- Recall batches identified by `R-<category>-<number>`.

#### Dependencies
- Namespace prefix `inv` used for `<inv:stock>` and `<inv:recall>` elements.  
- No external schemas, XSDs, or XSLT templates referenced.
