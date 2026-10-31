### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:32:57Z
- G: 
- A: 0.0.0
- X: file
---
> Models shop products, customer orders, and order lines, with a procedure that adds an item, reserves stock, and updates the order total.

#### Purpose
- Stores product catalog, customer order headers, and order items.
- `add_order_item` adds or increments an order line while decrementing product stock and recalculating the order total.

#### Type
- Tables: `product`, `customer_order`, `order_item`
- Procedure: `add_order_item`
- Dialect: MySQL 8

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku`
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
  - KEY `idx_order_customer` (`customer_id`, `created_at`)
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of PRIMARY KEY (`order_id`, `product_id`); FK `fk_item_order`
  - `product_id INT UNSIGNED` — NOT NULL, part of PRIMARY KEY (`order_id`, `product_id`); FK `fk_item_product`
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock`
- `order_item`: `quantity`, `unit_price_cents`, `order_id`

#### Writes
- `order_item`: inserts one value tuple per procedure call (`order_id`, `product_id`, `quantity`, `unit_price_cents`); on duplicate key, updates `quantity`.
- `product`: updates `stock`.
- `customer_order`: updates `total_cents`.

#### Relationships
- `order_item.order_id -> customer_order.order_id` — FK `fk_item_order`, `ON DELETE CASCADE`
- `order_item.product_id -> product.product_id` — FK `fk_item_product`

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Locks the matching product row where `sku = p_sku` and `stock >= p_qty`; retrieves `product_id` and `price_cents`.
- Signals SQLSTATE `45000` with message `unknown SKU or not enough stock` if no product is found.
- Inserts an order line or increments its existing quantity for the same order and product.
- Decrements product stock by `p_qty`.
- Recalculates `customer_order.total_cents` as the sum of `quantity * unit_price_cents` for the order.

#### Dependencies
- `product`
- `order_item`
- `customer_order`
