### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:36:29Z
- G: 
- A: 0.0.0
- X: file
---
> Web shop order placement and inventory reservation logic.

#### Purpose
*   Defines product catalog and order transaction schema for a web shop.
*   Provides stored procedure to add items to orders with stock validation.

#### Type
*   Tables: `product`, `customer_order`, `order_item`.
*   Procedure: `add_order_item`.
*   Dialect: MySQL 8.

#### Schema
*   `product`: `product_id` INT UNSIGNED PK AUTO_INCREMENT, `sku` VARCHAR(40) UNIQUE NOT NULL, `name` VARCHAR(200) NOT NULL, `price_cents` INT UNSIGNED NOT NULL, `stock` INT NOT NULL DEFAULT 0.
*   `customer_order`: `order_id` BIGINT UNSIGNED PK AUTO_INCREMENT, `customer_id` BIGINT UNSIGNED NOT NULL, `status` ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW', `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, `total_cents` INT UNSIGNED NOT NULL DEFAULT 0. Index `idx_order_customer` on (`customer_id`, `created_at`).
*   `order_item`: `order_id` BIGINT UNSIGNED PK FK, `product_id` INT UNSIGNED PK FK, `quantity` SMALLINT UNSIGNED NOT NULL, `unit_price_cents` INT UNSIGNED NOT NULL. Composite PK (`order_id`, `product_id`).

#### Reads
*   `product` (columns: `sku`, `stock`, `product_id`, `price_cents`).
*   `order_item` (columns: `order_id`, `quantity`, `unit_price_cents`).
*   Procedure inputs: `p_order_id`, `p_sku`, `p_qty`.

#### Writes
*   `order_item` (INSERT/UPDATE `quantity`).
*   `product` (UPDATE `stock`).
*   `customer_order` (UPDATE `total_cents`).

#### Relationships
*   `order_item.order_id` -> `customer_order.order_id`.
*   `order_item.product_id` -> `product.product_id`.

#### Routine logic
*   Procedure `add_order_item` parameters: `p_order_id`, `p_sku`, `p_qty`.
*   Selects `product` row by `sku` with `FOR UPDATE` lock.
*   Signals error if `sku` unknown or `stock` insufficient.
*   Inserts or updates `order_item` record for `order_id` and `product_id`.
*   Decrements `product.stock` by `p_qty`.
*   Recalculates `customer_order.total_cents` via `SUM(quantity * unit_price_cents)`.

#### Dependencies
*   Tables: `product`, `customer_order`, `order_item`.
