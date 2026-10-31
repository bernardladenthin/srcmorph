### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:35:57Z
- G: 
- A: 0.0.0
- X: file
---
> MySQL 8 web shop schema for products, customer orders and order lines, plus a stored procedure that adds an item to an order, reserves stock and recalculates the order total.

#### Purpose
- Models a web shop catalog (`product`), customer orders (`customer_order`) and order line items (`order_item`).
- Provides `add_order_item` to place a SKU into an order with stock reservation and total recalculation.

#### Type
- Multiple objects: 3 tables and 1 stored procedure.
- Dialect: MySQL 8 (InnoDB, utf8mb4, `DELIMITER //`, `SIGNAL SQLSTATE`, `ON DUPLICATE KEY UPDATE`).

#### Schema
- `product`
  - `product_id INT UNSIGNED NOT NULL AUTO_INCREMENT` — PK
  - `sku VARCHAR(40) NOT NULL` — UNIQUE key `uk_product_sku`
  - `name VARCHAR(200) NOT NULL`
  - `price_cents INT UNSIGNED NOT NULL`
  - `stock INT NOT NULL DEFAULT 0`
- `customer_order`
  - `order_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT` — PK
  - `customer_id BIGINT UNSIGNED NOT NULL` — indexed via `idx_order_customer (customer_id, created_at)`
  - `status ENUM('NEW','PAID','SHIPPED','CANCELLED') NOT NULL DEFAULT 'NEW'`
  - `created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `total_cents INT UNSIGNED NOT NULL DEFAULT 0`
- `order_item`
  - `order_id BIGINT UNSIGNED NOT NULL` — composite PK part, FK
  - `product_id INT UNSIGNED NOT NULL` — composite PK part, FK
  - `quantity SMALLINT UNSIGNED NOT NULL`
  - `unit_price_cents INT UNSIGNED NOT NULL`
  - PK `(order_id, product_id)`

#### Reads
- `product.product_id`, `product.sku`, `product.price_cents`, `product.stock` (lookup with `FOR UPDATE` lock in `add_order_item`)
- `order_item.quantity`, `order_item.unit_price_cents`, `order_item.order_id` (SUM subquery for total)

#### Writes
- `order_item` — INSERT (`order_id`, `product_id`, `quantity`, `unit_price_cents`); ON DUPLICATE KEY UPDATE `quantity = quantity + p_qty`
- `product.stock` — UPDATE `stock = stock - p_qty`
- `customer_order.total_cents` — UPDATE to sum of order line `quantity * unit_price_cents`

#### Relationships
- `order_item.order_id -> customer_order.order_id` (`fk_item_order`, ON DELETE CASCADE)
- `order_item.product_id -> product.product_id` (`fk_item_product`)

#### Routine logic
- `add_order_item(IN p_order_id BIGINT UNSIGNED, IN p_sku VARCHAR(40), IN p_qty SMALLINT UNSIGNED)`
  - Selects `product_id` and `price_cents` from `product` where `sku = p_sku AND stock >= p_qty`, row-locked by `FOR UPDATE`.
  - If no row matched (`v_product_id IS NULL`), raises SQLSTATE `45000` with message "unknown SKU or not enough stock".
  - Inserts into `order_item` with the looked-up price; on duplicate `(order_id, product_id)` adds `p_qty` to existing `quantity`.
  - Decrements `product.stock` by `p_qty` for the product.
  - Recomputes `customer_order.total_cents` as the sum of `quantity * unit_price_cents` over `order_item` for `p_order_id`.
  - Effect: one order line added or merged, stock reserved, order total refreshed. No return value.

#### Dependencies
- Tables: `product`, `customer_order`, `order_item`
- Constraints: `uk_product_sku`, `idx_order_customer`, `fk_item_order`, `fk_item_product`
- Procedure: `add_order_item`
