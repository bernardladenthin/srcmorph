### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:17:20Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages shop products and customer orders, including adding order items while reserving inventory and updating order totals.

#### Purpose
- Defines the product, customer-order, and order-item data model.
- Provides `add_order_item` to add or increment an order line and synchronize stock and order totals.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and stock.
- **Order records:** `customer_order` stores customer, status, creation time, and total.
- **Order contents:** `order_item` stores per-product quantities and unit prices, linked to orders and products.
- **Item addition:** `add_order_item` validates and locks available stock, records the item, decrements inventory, and recalculates the total.

#### Key units
- `product` — product catalog and available stock.
- `customer_order` — customer order status and monetary total.
- `order_item` — order lines keyed by `(order_id, product_id)`.
- `add_order_item` — adds or increments an item, reserves stock, and updates the order total.

#### Data flow
- `add_order_item(p_order_id, p_sku, p_qty)` locks a matching `product` row with sufficient stock; if none is found, it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- The procedure inserts an `order_item` or increments its quantity, subtracts `p_qty` from product stock, then recalculates `customer_order.total_cents` from the order’s lines.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascading deletion; `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and updates all three tables.

#### Cross-cutting
- MySQL 8 with InnoDB and `utf8mb4`.
- Product selection uses `SELECT ... FOR UPDATE` to lock the matching inventory row.
- Order statuses are `NEW`, `PAID`, `SHIPPED`, and `CANCELLED`.
