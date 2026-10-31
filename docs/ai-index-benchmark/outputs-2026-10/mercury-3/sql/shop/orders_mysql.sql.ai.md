### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:32:57Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines a MySQL 8 web-shop schema for products, orders, order items, and a stored procedure that adds an order line while reserving stock and keeping the order total current.

#### Purpose
- Models product catalog, customer orders, and order line items.
- Stores an `add_order_item` procedure to add a line, check stock, update inventory, and recalculate order total.

#### Type
- Tables and procedure (MySQL dialect, MySQL 8, InnoDB, utf8mb4, DELIMITER usage).

#### Schema
- `product`
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` — PK
  - `sku VARCHAR(40) NOT NULL` — UNIQUE (`uk_product_sku`)
  - `name VARCHAR(200) NOT NULL`
  - `price_cents INT UNSIGNED NOT NULL`
  - `stock INT NOT NULL DEFAULT 0`
- `customer_order`
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` — PK
  - `customer_id BIGINT UNSIGNED NOT NULL` — indexed with `created_at` (`idx_order_customer`)
  - `status ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'`
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
- `order_item`
  - `order_id BIGINT UNSIGNED NOT NULL` — PK part, FK to `customer_order`
  - `product_id INT UNSIGNED NOT NULL` — PK part, FK to `product`
  - `quantity SMALLINT UNSIGNED NOT NULL`
  - `unit_price_cents INT UNSIGNED NOT NULL`
  - PK `(order_id, product_id)`
  - `fk_item_order` FK cascade delete, `fk_item_product` FK

#### Reads
- Procedure `add_order_item` reads:
  - `product.sku`, `product.product_id`, `product.price_cents`, `product.stock`
  - `order_item.order_id`, `order_item.quantity`, `order_item.unit_price_cents` (SUM subquery)

#### Writes
- Procedure `add_order_item` writes:
  - `order_item` — INSERT with `ON DUPLICATE KEY UPDATE` (`quantity = quantity + p_qty`)
  - `product.stock` — UPDATE (`stock = stock - p_qty`)
  - `customer_order.total_cents` — UPDATE (recomputed SUM)

#### Relationships
- `order_item.order_id -> customer_order.order_id` (FK `fk_item_order`, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (FK `fk_item_product`)
- `customer_order.customer_id` — indexed, no FK target table shown
- Procedure join: `product.sku` lookup by `p_sku`; `order_item` linked to `customer_order` via `order_id`

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
  - Declares `v_product_id`, `v_price`
  - SELECTs `product_id`, `price_cents` from `product` WHERE `sku = p_sku` AND `stock >= p_qty` FOR UPDATE
  - If `v_product_id IS NULL`, SIGNAL SQLSTATE `'45000'` with message `'unknown SKU or not enough stock'`
  - INSERTs into `order_item`; on duplicate key updates `quantity = quantity + p_qty`
  - UPDATEs `product.stock = stock - p_qty`
  - UPDATEs `customer_order.total_cents` to SUM(`quantity * unit_price_cents`) for the order

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- No other views, functions, sequences, or triggers referenced
