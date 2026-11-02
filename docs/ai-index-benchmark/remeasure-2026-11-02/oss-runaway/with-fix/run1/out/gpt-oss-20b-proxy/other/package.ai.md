### other
- H: 1.0
- C: A9F55380
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:32:17Z
- G: 
- A: 0.0.0
- X: package
- F: [catalog/](catalog/package.ai.md)
---
> A single XML catalog that centralises product definitions, inventory levels, and recall data for an e‑commerce platform.

#### Purpose
- Store product metadata, pricing, stock, and recall information in one XML document.

#### Responsibilities
- **Catalog definition** – categories and products with SKU, title, price, and stock.
- **Inventory tracking** – per‑warehouse stock via `<inv:stock>`.
- **Recall management** – optional recall batch data via `<inv:recall>`.

#### Key units
- **`catalog`** (root, `urn:example:catalog`) – holds all categories and products.
- **`category`** – attributes `id`, `name`; groups products.
- **`product`** – attribute `sku`; children `<title>`, `<price>`, `<inv:stock>`, optional `<inv:recall>`.
- **`price`** – attribute `currency="EUR"`; numeric value.
- **`inv:stock`** – namespace `http://example.com/inventory`; attributes `warehouse`; quantity text.
- **`inv:recall`** – namespace `http://example.com/inventory`; attributes `batch`; recall text.

#### Data flow
- E‑commerce system reads catalog XML → parses categories and products → extracts price and per‑warehouse stock → flags products with recall data for downstream handling.

#### Dependencies
- Namespace `urn:example:catalog` for catalog structure.
- Namespace `http://example.com/inventory` (or `urn:example:inventory`) for inventory elements.
- No external schemas, XSDs, or XSLT templates referenced.

#### Cross-cutting
- SKU is the unique product identifier.
- Prices are always in EUR.
- Stock quantities are integers.
- Recall batch IDs follow `R-<category>-<number>` pattern.
