### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:23Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Models shop products, customer orders, and order lines, with a procedure that adds an item while reserving stock and recalculating the order total.

#### Purpose
- Stores product inventory, customer order status and totals, and order line quantities and prices.
- `add_order_item` adds or increments an order line, decrements product stock, and updates the order total.

#### Type
- MySQL 8: tables `product`, `customer_order`, `order_item`; procedure `add_order_item`.
- Enum `customer_order.status`: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE via `uk_product_sku`.
  - `name VARCHAR(200)` — NOT NULL.
  - `price_cents INT UNSIGNED` — NOT NULL.
  - `stock INT` — NOT NULL, DEFAULT `0`.
  - Index: `uk_product_sku` — UNIQUE on `sku`.
- `customer_order`
  - `order_id BIGINT UNSIGNED` — NOT NULL, AUTO_INCREMENT, PRIMARY KEY.
  - `customer_id BIGINT UNSIGNED` — NOT NULL.
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`.
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`.
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`.
  - Index: `idx_order_customer` — on `(customer_id, created_at)`.
- `order_item`
  - `order_id BIGINT UNSIGNED` — NOT NULL, part of PRIMARY KEY `(order_id, product_id)`.
  - `product_id INT UNSIGNED` — NOT NULL, part of PRIMARY KEY `(order_id, product_id)`.
  - `quantity SMALLINT UNSIGNED` — NOT NULL.
  - `unit_price_cents INT UNSIGNED` — NOT NULL.
  - Constraint `fk_item_order` — FOREIGN KEY (`order_id`) REFERENCES `customer_order` (`order_id`) ON DELETE CASCADE.
  - Constraint `fk_item_product` — FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`).

#### Reads
- `product`: `product_id`, `price_cents`, `sku`, `stock` — SKU lookup, stock eligibility check, and row lock.
- `order_item`: `quantity`, `unit_price_cents` — summed to recalculate an order total.

#### Writes
- `order_item`: INSERT one line per procedure call; on duplicate key, increments `quantity`.
- `product`: UPDATE `stock` by subtracting the requested quantity.
- `customer_order`: UPDATE `total_cents` from the sum of its order lines.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
- Looks up and locks the matching product if stock is sufficient; signals SQLSTATE `45000` for an unknown SKU or insufficient stock.
- Inserts an order line using the product’s current price, or increments its quantity if the order/product key already exists.
- Decrements product stock and recalculates the order’s `total_cents` as the sum of line quantities times unit prices.

#### Dependencies
- Tables: `product`, `order_item`, `customer_order`.
