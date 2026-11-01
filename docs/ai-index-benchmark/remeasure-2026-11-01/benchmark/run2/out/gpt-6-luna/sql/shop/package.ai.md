### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:23Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains webshop product inventory and customer orders, including adding order items while reserving stock and updating order totals.

#### Purpose
- Defines product, order, and order-line storage for webshop purchasing.
- Provides `add_order_item` to add or increase an order line, reduce available stock, and recalculate the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price in cents, and stock.
- **Order tracking:** `customer_order` stores customer, status, creation time, and total in cents.
- **Order contents:** `order_item` records product quantities and unit prices per order.
- **Order-item updates:** `add_order_item` locks an eligible product, handles missing SKU or insufficient stock, updates the line and inventory, and refreshes the order total.

#### Key units
- `product` — catalog and stock table, with unique `sku`.
- `customer_order` — order header table with `NEW`, `PAID`, `SHIPPED`, and `CANCELLED` statuses.
- `order_item` — order-line table keyed by (`order_id`, `product_id`), storing quantity and unit price.
- `add_order_item` — MySQL procedure that accepts an order ID, SKU, and quantity; signals SQLSTATE `45000` when the SKU is unknown or stock is insufficient.

#### Data flow
- `add_order_item` finds and locks a product by SKU, requiring stock of at least the requested quantity.
- It inserts an order line or increments its existing quantity, then decrements product stock.
- It recalculates `customer_order.total_cents` from the order’s line quantities and unit prices.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with `ON DELETE CASCADE`.
- `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- Uses MySQL 8 tables and a stored procedure; monetary values are represented in cents.
- Product selection uses `FOR UPDATE`; order lines use a composite primary key to support duplicate-key quantity increments.
