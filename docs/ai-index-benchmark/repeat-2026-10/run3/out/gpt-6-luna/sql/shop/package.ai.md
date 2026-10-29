### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:24:21Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains a web-shop product catalog and customer orders, reserving inventory and updating order totals as items are added.

#### Purpose
- Stores products, stock levels, customer orders, and order-line quantity and price snapshots.
- Provides `add_order_item` to add or increment an order line, reserve stock, and recalculate the order total.

#### Responsibilities
- **Catalog and order schema:** Define `product`, `customer_order`, and `order_item` tables with keys, indexes, and relationships.
- **Order-item processing:** Look up a product by SKU, validate available stock, upsert its order line, decrement inventory, and update the order total.
- **Failure signaling:** Raise SQLSTATE `45000` with `unknown SKU or not enough stock` when no eligible product is found.

#### Key units
- `product` — product catalog with SKU, price, and available stock.
- `customer_order` — customer order status, creation time, and total.
- `order_item` — per-order product quantity and unit-price snapshot; keyed by `(order_id, product_id)`.
- `add_order_item` — adds or increments an order line while reserving stock and recalculating the order total.

#### Data flow
- `add_order_item` finds and locks a SKU-matched product with sufficient stock, then reads its ID and price.
- It inserts or increments the matching `order_item`, decrements `product.stock`, and sets `customer_order.total_cents` from the order-line quantity and unit-price sum.

#### Dependencies
- MySQL 8 with InnoDB.
- `order_item.order_id` references `customer_order.order_id` with cascade deletion; `order_item.product_id` references `product.product_id`.
- `customer_order.customer_id` has no declared foreign key in this schema.

#### Cross-cutting
- Product lookup uses `FOR UPDATE` to lock the selected product row.
- `order_item` uses a composite primary key to support duplicate-key quantity increments.
