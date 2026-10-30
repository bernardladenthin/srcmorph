### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:46:45Z
- G: 
- A: 0.0.0
- X: file
---
> This file models the web shop’s product catalog, order records, and order-item fulfillment logic for placing and reconciling items in an e-commerce workflow.

#### Purpose
- Defines product inventory, customer orders, and line items for a web shop.
- Implements order-item insertion with stock reservation and order total recalculation.

#### Type
- MySQL 8 DDL/DML script.
- Objects: `product` table, `customer_order` table, `order_item` table, `add_order_item` procedure.

#### Schema
- `product`
  - `product_id INT UNSIGNED` — PK, NOT NULL, AUTO_INCREMENT
  - `sku VARCHAR(40)` — NOT NULL, UNIQUE KEY `uk_product_sku` (`sku`)
  - `name VARCHAR(200)` — NOT NULL
  - `price_cents INT UNSIGNED` — NOT NULL
  - `stock INT` — NOT NULL, DEFAULT `0`
  - Indexes: PK `product_id`; UNIQUE `uk_product_sku` (`sku`)
- `customer_order`
  - `order_id BIGINT UNSIGNED` — PK, NOT NULL, AUTO_INCREMENT
  - `customer_id BIGINT UNSIGNED` — NOT NULL
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED')` — NOT NULL, DEFAULT `'NEW'`
  - `created_at DATETIME` — NOT NULL, DEFAULT `CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED` — NOT NULL, DEFAULT `0`
  - Indexes: PK `order_id`; KEY `idx_order_customer` (`customer_id`, `created_at`)
- `order_item`
  - `order_id BIGINT UNSIGNED` — PK, NOT NULL, FK `fk_item_order` -> `customer_order.order_id`
  - `product_id INT UNSIGNED` — PK, NOT NULL, FK `fk_item_product` -> `product.product_id`
  - `quantity SMALLINT UNSIGNED` — NOT NULL
  - `unit_price_cents INT UNSIGNED` — NOT NULL
  - Constraints: PK (`order_id`, `product_id`); FK `fk_item_order` (`order_id`) REFERENCES `customer_order` (`order_id`) ON DELETE CASCADE; FK `fk_item_product` (`product_id`) REFERENCES `product` (`product_id`)

#### Reads
- `add_order_item` reads `product.product_id`, `product.price_cents`, `product.sku`, `product.stock` via `SELECT product_id, price_cents FROM product WHERE sku = p_sku AND stock >= p_qty FOR UPDATE`.
- `add_order_item` reads `order_item.quantity`, `order_item.unit_price_cents` in `SELECT SUM(quantity * unit_price_cents) FROM order_item WHERE order_id = p_order_id` to recalculate `customer_order.total_cents`.
- Procedure inputs: `p_order_id`, `p_sku`, `p_qty`.

#### Writes
- `add_order_item` writes `order_item.order_id`, `order_item.product_id`, `order_item.quantity`, `order_item.unit_price_cents` via `INSERT INTO order_item ... ON DUPLICATE KEY UPDATE quantity = quantity + p_qty`.
- `add_order_item` writes `product.stock` via `UPDATE product SET stock = stock - p_qty WHERE product_id = v_product_id`.
- `add_order_item` writes `customer_order.total_cents` via `UPDATE customer_order SET total_cents = (...) WHERE order_id = p_order_id`.
- `add_order_item` raises `SIGNAL SQLSTATE '45000'` with `MESSAGE_TEXT = 'unknown SKU or not enough stock'` when no product row matches.

#### Relationships
- `order_item.order_id -> customer_order.order_id`
- `order_item.product_id -> product.product_id`

#### Routine logic
- Parameters: `IN p_order_id BIGINT UNSIGNED`, `IN p_sku VARCHAR(40)`, `IN p_qty SMALLINT UNSIGNED`
- Locks a matching `product` row with `FOR UPDATE` and reads `product_id`, `price_cents` for `sku = p_sku AND stock >= p_qty`.
- If no eligible product row is found, sets `v_product_id IS NULL` and raises `SQLSTATE '45000'` with `MESSAGE_TEXT = 'unknown SKU or not enough stock'`.
- Inserts a row into `order_item` for `order_id`, `product_id`, `quantity`, `unit_price_cents`; if the key already exists, increments `order_item.quantity` by `p_qty`.
- Updates `product.stock = stock - p_qty` for the reserved item.
- Recomputes `customer_order.total_cents` as `SUM(quantity * unit_price_cents)` for that `order_id`.

#### Dependencies
- `product`
- `customer_order`
- `order_item`
- MySQL built-ins: `CURRENT_TIMESTAMP`, `FOR UPDATE`, `SUM()`, `SIGNAL`
