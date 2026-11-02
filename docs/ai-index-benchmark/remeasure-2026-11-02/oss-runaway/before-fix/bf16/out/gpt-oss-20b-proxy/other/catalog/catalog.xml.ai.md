### catalog.xml
- H: 1.0
- C: CCF59A7E
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:13:32Z
- G: 
- A: 0.0.0
- X: file
- P: 80AA1478
- N: C0493A8E
---
**Facts (exact, whole file):** elements: 20291; namespace declarations: 2; id and name attributes: 80; XSD type definitions: 0; XSLT templates: 0; Spring beans: 0

**Coverage:** 5 of 5 chunks summarized

> A structured XML catalog that lists products by category, each with a SKU, title, EUR price, and per‑warehouse inventory stock.

#### Purpose
- Stores product listings for an e‑commerce catalog.  
- Provides inventory levels per warehouse.

#### Type
- XML data file  
  Root element: `catalog`  
  Namespace declarations:  
  - `xmlns="urn:example:catalog"`  
  - `xmlns:inv="urn:example:inventory"`  
  Attribute: `version="3"`

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
  - `cat-010` – “Category 10”  
  - `cat-011` – “Category 11”  
  - `cat-012` – “Category 12”  
  - `cat-013` – “Category 13”  
  - `cat-014` – “Category 14”  
  - `cat-015` – “Category 15”  
  - `cat-016` – “Category 16”  
  - `cat-017` – “Category 17”  
  - `cat-018` – “Category 18”  
  - `cat-019` – “Category 19”  
  - `cat-020` – “Category 20”  
  - `cat-021` – “Category 21”  
  - `cat-022` – “Category 22”  
  - `cat-023` – “Category 23”  
  - `cat-024` – “Category 24”  
  - `cat-025` – “Category 25”  
- **Products**  
  Each `<product>` element has:  
  - Attribute `sku` (e.g., `SKU-018-0091`)  
  - Child `<title>` – product name  
  - Child `<price currency="EUR">` – numeric value  
  - Child `<inv:stock warehouse="W#">` – quantity value  
  SKU pattern: `SKU-<cat>-<sequence>` (e.g., `SKU-010-0001`).

#### Configuration
- Prices are in `EUR`.  
- Inventory warehouses: `W1`–`W9`.  
- No other configurable parameters.

#### Dependencies
- Namespace prefix `inv` bound to `urn:example:inventory`.  
- No external schemas or references.
