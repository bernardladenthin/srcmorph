### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:23:47Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains a shop catalog and inventory while recording customer order items and keeping order totals in sync.

#### Purpose
- Defines product, customer-order, and order-line storage for shop purchases.
- Provides `add_order_item` to add or increment an order line, reserve stock, and recalculate the order total.

#### Responsibilities
- Catalog and inventory: store product SKUs, prices, and stock; enforce unique SKUs.
- Order records: track customer, status, creation time, and total.
- Order lines: store quantities and unit prices, linked to orders and products.
- Item addition: validate SKU and available stock, update the line and inventory, and refresh the order total.

#### Key units
- `product` — product catalog and available stock.
- `customer_order` — customer order metadata and calculated total.
- `order_item` — per-order product quantities and unit prices, keyed by order and product.
- `add_order_item` — adds or increments a line, decrements stock, and recalculates the order total.

#### Data flow
- `add_order_item` looks up and locks a product by SKU when stock is sufficient; otherwise signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- It inserts an `order_item` row or increments its quantity on duplicate `(order_id, product_id)`, then subtracts the requested quantity from product stock.
- It sums the order’s line totals and writes the result to `customer_order.total_cents`; it returns no result set.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`.
- `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- Uses MySQL 8 tables, foreign keys, primary keys, and indexes; `product.sku` has a unique constraint.
- Product selection uses a lock during SKU and stock validation.
