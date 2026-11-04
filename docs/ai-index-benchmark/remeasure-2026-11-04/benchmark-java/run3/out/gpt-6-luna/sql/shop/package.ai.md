### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:47:02Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages shop inventory and customer orders, including adding order items while reserving stock and updating order totals.

#### Purpose
- Defines product inventory, customer orders, and order-line storage in MySQL.
- Provides `add_order_item` to add or increment a line, subtract stock, and recalculate the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and available stock.
- **Orders and lines:** `customer_order` tracks customer, status, creation time, and total; `order_item` stores quantities and unit prices per order and product.
- **Order item updates:** `add_order_item` validates SKU and stock, upserts the order line, reserves inventory, and refreshes the total.

#### Key units
- `product` — product catalog and stock inventory, with unique SKU.
- `customer_order` — customer order status (`NEW`, `PAID`, `SHIPPED`, `CANCELLED`) and total.
- `order_item` — order-product line items keyed by `(order_id, product_id)`.
- `add_order_item` — adds or increments an order line using the product’s current price and updates stock and order total.

#### Data flow
- `add_order_item` looks up and locks a product by SKU; signals SQLSTATE `45000` for an unknown SKU or insufficient stock.
- Inserts or increments the matching `order_item`, subtracts the requested quantity from `product.stock`, then recalculates `customer_order.total_cents` from line quantities and unit prices.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`; `order_item.product_id` references `product.product_id`.
- `add_order_item` reads `product` and `order_item`, and writes `order_item`, `product`, and `customer_order`.
