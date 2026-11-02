### sql/shop
- H: 1.0
- C: 3A013002
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:38:51Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Manages shop product inventory and customer orders, including adding order items while reserving stock and recalculating order totals.

#### Purpose
- Stores products, customer orders, and order line items.
- Provides `add_order_item` to add or increment an item, decrement inventory, and refresh the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and stock; SKU is unique.
- **Order lifecycle and lookup:** `customer_order` stores customer, status, creation time, and total; indexes support customer and date lookup.
- **Order contents:** `order_item` records quantities and unit prices, keyed by order and product.
- **Order-item processing:** `add_order_item` validates SKU and stock, signals an error when unavailable, then updates items, inventory, and totals.

#### Key units
- `product` — catalog and inventory records.
- `customer_order` — customer orders with `NEW`, `PAID`, `SHIPPED`, or `CANCELLED` status.
- `order_item` — per-product quantities and unit prices within an order.
- `add_order_item(IN p_order_id, IN p_sku, IN p_qty)` — adds or increments an order item, reserves stock, and recalculates `total_cents`.

#### Data flow
- `add_order_item` finds and locks a matching `product` row whose stock covers the requested quantity; otherwise it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.
- It inserts the `(order_id, product_id)` line item or increments its quantity, subtracts the requested quantity from product stock, then sets the order total from summed line-item amounts.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascade delete.
- `order_item.product_id` references `product.product_id`.
- Product selection uses `product.sku`; order totals derive from `order_item.quantity` and `unit_price_cents`.

#### Cross-cutting
- MySQL 8 tables use InnoDB; keys and constraints enforce product SKU uniqueness and order-item relationships.
- `add_order_item` uses row locking for stock eligibility and raises a SQL exception when the SKU is unknown or inventory is insufficient.
