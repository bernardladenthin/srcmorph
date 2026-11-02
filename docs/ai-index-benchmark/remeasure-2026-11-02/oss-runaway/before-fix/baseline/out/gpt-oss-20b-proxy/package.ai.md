### ai
- H: 1.0
- C: 7299836A
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:05:56Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A unified catalog system that defines platform-wide error codes and stores product inventory data in a single XML format.

#### Purpose
- Centralize immutable error codes for cross‑service use.
- Persist product, pricing, and inventory information in a single XML document.

#### Responsibilities
- **Error handling**: expose a fixed set of error codes with numeric IDs and messages.
- **Catalog persistence**: define XML schema for categories, products, prices, and per‑warehouse stock.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants with `int number` and `String message`; getters `getNumber()`, `getMessage()`.
- **`BY_NUMBER` map** – static `HashMap<Integer, PlatformErrorCode>` for reverse lookup.
- **`byNumber(int)`** – static lookup method returning the enum or `null`.
- **XML elements**: `catalog` (root, namespace `urn:example:catalog`), `category` (`id`, `name`), `product` (`sku`), `title`, `price` (`currency="EUR"`), `inv:stock` (`warehouse`, quantity).

#### Data flow
1. **Error codes**: class loads → static block populates `BY_NUMBER`; clients call `byNumber(code)` → obtain enum → use getters.
2. **Catalog XML**: application loads XML → parser traverses `catalog` → `category` → `product`; extracts `sku`, `title`, `price`, and iterates `inv:stock` for warehouse quantities → produces structured inventory data.

#### Dependencies
- Java standard library (`java.util.Map`, `java.util.HashMap`, `java.lang.Enum`).
- XML namespace `urn:example:catalog` for catalog structure.
- XML namespace `urn:example:inventory` for `<inv:stock>` elements.
- Fixed currency `EUR` on `<price>` elements.

#### Cross-cutting
- Immutable, thread‑safe access to error codes after initialization.
- Consistent attribute usage (`id`, `sku`, `currency`, `warehouse`) across XML elements.
- Uniform namespace prefix `inv` for inventory stock elements.
