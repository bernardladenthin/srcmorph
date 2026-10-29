### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:53:45Z
- G: 
- A: 0.0.0
- X: file
---
> Manages e-commerce product inventory, customer order tracking, and the transactional process of adding items to orders.

#### Purpose
* Track product catalog, pricing, and stock levels.
* Manage customer order headers and line-item details.
* Atomically handle order item insertion, stock reservation, and order total calculation.

#### Type
* Tables, Procedure (MySQL 8).

#### Schema
* table `product`:
    * `product_id` INT UNSIGNED (PK, AUTO_INCREMENT)
    * `sku` VARCHAR(40) (UNIQUE, NOT NULL)
    * `name` VARCHAR(200) (NOT NULL)
    * `price_cents` INT UNSIGNED (NOT NULL)
    * `stock` INT (NOT NULL, DEFAULT 0)
* table `customer_order`:
    * `order_id` BIGINT UNSIGNED (PK, AUTO_INCREMENT)
    * `customer_id` BIGINT UNSIGNED (NOT NULL)
    * `status` ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') (NOT NULL, DEFAULT 'NEW')
    * `created_at` DATETIME (NOT NULL, DEFAULT CURRENT_TIMESTAMP)
    * `total_cents` INT UNSIGNED (NOT NULL, DEFAULT 0)
    * Index `idx_order_customer` (customer_id, created_at)
* table `order_item`:
    * `order_id` BIGINT UNSIGNED (PK, FK)
    * `product_id` INT UNSIGNED (PK, FK)
    * `quantity` SMALLINT UNSIGNED (NOT NULL)
    * `unit_price_cents` INT UNSIGNED (NOT NULL)

#### Reads
* `product`: `product_id`, `price_cents`, `stock` (via `sku` lookup).
* `order_item`: `quantity`, `unit_price_cents` (via `order_id` for summation).

#### Writes
* `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents` (INSERT/UPDATE).
* `product`: `stock` (UPDATE).
* `customer_order`: `total_cents` (UPDATE).

#### Relationships
* `order_item.order_id` -> `customer_order.order_id` (CASCADE DELETE)
* `order_item.product_id` -> `product.product_id`

#### Routine logic
* procedure `add_order_item` (p_order_id, p_sku, p_qty):
    * Locks `product` row via `FOR UPDATE` to verify `sku` existence and sufficient `stock`.
    * Throws error '45000' if SKU is missing or stock is insufficient.
    * Inserts or increments `quantity` in `order_item` using `ON DUPLICATE KEY UPDATE`.
    * Decrements `product.stock` by `p_qty`.
    * Recalculates and updates `customer_order.total_cents` based on the sum of all items for that order.

#### Dependencies
* `product`
* `customer_order`
* `order_item`
