### sql/shop
- H: 1.0
- C: F029BDD3
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:28Z
- G: 
- A: 0.0.0
- X: package
- F: [orders_mysql.sql](orders_mysql.sql.ai.md)
---
> Maintains webshop product inventory and customer order totals as items are added to orders.

#### Purpose
- Defines the product catalog, customer orders, and order-line data.
- Provides `add_order_item` to add or increase an order line, reserve stock, and recalculate the order total.

#### Responsibilities
- **Catalog and inventory:** `product` stores SKU, name, price, and stock.
- **Order records:** `customer_order` stores customer, status, creation time, and total.
- **Order contents:** `order_item` links products to orders with quantity and unit price.
- **Item addition:** `add_order_item` validates SKU and stock, updates the line and inventory, and recalculates the total.

#### Key units
- `product` — product catalog and inventory table, keyed by `product_id` with unique `sku`.
- `customer_order` — customer order table with `NEW`, `PAID`, `SHIPPED`, and `CANCELLED` statuses.
- `order_item` — order-line table keyed by `(order_id, product_id)`.
- `add_order_item` — stored procedure that adds or increments an item, decrements stock, and updates `total_cents`.

#### Data flow
- `add_order_item` looks up `product` by SKU and locks the matching row when stock is sufficient.
- It inserts an `order_item` or increments its quantity, then decreases `product.stock`.
- It recalculates `customer_order.total_cents` from the order’s line quantities and unit prices.
- If the SKU is unknown or stock is insufficient, it signals SQLSTATE `45000` with `unknown SKU or not enough stock`.

#### Dependencies
- `order_item.order_id` references `customer_order.order_id` with cascading deletion; `order_item.product_id` references `product.product_id`.
- `add_order_item` reads and writes `product`, `order_item`, and `customer_order`.

#### Cross-cutting
- Uses MySQL 8 tables and a stored procedure; `add_order_item` returns no result set.
- Product-row locking protects the stock check and reservation; invalid SKU or insufficient stock uses a SQL exception signal.
