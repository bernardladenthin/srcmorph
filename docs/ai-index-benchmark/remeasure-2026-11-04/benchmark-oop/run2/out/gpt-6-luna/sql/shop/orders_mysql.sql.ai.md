### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:26Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Models shop products, customer orders, and order lines, with a procedure that adds an item while reserving stock and recalculating the order total.

#### Purpose
- Stores product prices and inventory, order status and totals, and per-product order quantities and prices.
- `add_order_item` adds or increments an order line, decrements stock, and refreshes the order total.

#### Type
- MySQL 8; tables and procedure.
- `customer_order.status` enum: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`.
  - `name VARCHAR(200)` — NOT NULL.
  - `price_cents INT UNSIGNED` — NOT NULL.
  - `stock INT` — NOT NULL, DEFAULT `0`.
  - Index `uk_product_sku`: UNIQUE on `sku`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `customer_id BIGINT UNSIGNED` — NOT NULL.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`.
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`.
  - Index `idx_order_customer`: `(customer_id, created_at)`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of PRIMARY KEY `(order_id, product_id)`.
  - `product_id INT UNSIGNED` — NOT NULL, part of PRIMARY KEY `(order_id, product_id)`.
  - `quantity SMALLINT UNSIGNED` — NOT NULL.
  - `unit_price_cents INT UNSIGNED` — NOT NULL.
  - Constraint `fk_item_order`: FOREIGN KEY (`order_id`) REFERENCES `customer_order` (`order_id`) ON DELETE CASCADE.
  - Constraint `fk_item_product`: FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`).

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`.
- `order_item`: `quantity`, `unit_price_cents`, `order_id`.

#### Writes
- `order_item`: INSERT `order_id`, `product_id`, `quantity`, `unit_price_cents`; on duplicate key, UPDATE `quantity`.
- `product`: UPDATE `stock`.
- `customer_order`: UPDATE `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Selects the matching product ID and price when stock is sufficient, locking the row with `FOR UPDATE`.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is selected.
- Inserts an order line or increments its existing quantity; decrements product stock and recalculates the order total from its lines.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
