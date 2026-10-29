### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:29:58Z
- G: 
- A: 0.0.0
- X: file
---
> Manages e-commerce product inventory, customer orders, and the atomic process of adding items to an order while updating stock and totals.

#### Purpose
* Track product catalog, pricing, and stock levels.
* Manage customer order headers and line items.
* Provide a transactional procedure to reserve stock and calculate order totals.

#### Type
* Tables, Procedure (MySQL 8).

#### Schema
* table `product`: `product_id` INT UNSIGNED (PK, AUTO_INCREMENT), `sku` VARCHAR(40) (UNIQUE, NOT NULL), `name` VARCHAR(200) (NOT NULL), `price_cents` INT UNSIGNED (NOT NULL), `stock` INT (NOT NULL, DEFAULT 0).
* table `customer_order`: `order_id` BIGINT UNSIGNED (PK, AUTO_INCREMENT), `customer_id` BIGINT UNSIGNED (NOT NULL), `status` ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') (NOT NULL, DEFAULT 'NEW'), `created_at` DATETIME (NOT NULL, DEFAULT CURRENT_TIMESTAMP), `total_cents` INT UNSIGNED (NOT NULL, DEFAULT 0). Index: `idx_order_customer` (customer_id, created_at).
* table `order_item`: `order_id` BIGINT UNSIGNED (PK, FK), `product_id` INT UNSIGNED (PK, FK), `quantity` SMALLINT UNSIGNED (NOT NULL), `unit_price_cents` INT UNSIGNED (NOT NULL).

#### Reads
* `product`: `product_id`, `price_cents`, `sku`, `stock`.
* `order_item`: `quantity`, `unit_price_cents` (via SUM for order total).

#### Writes
* `order_item`: `order_id`, `product_id`, `quantity`, `unit_price_cents`.
* `product`: `stock`.
* `customer_order`: `total_cents`.

#### Relationships
* `order_item.order_id` -> `customer_order.order_id` (ON DELETE CASCADE).
* `order_item.product_id` -> `product.product_id`.

#### Routine logic
* procedure `add_order_item`:
    * Inputs: `p_order_id`, `p_sku`, `p_qty`.
    * Locks `product` row via `FOR UPDATE` to verify `sku` existence and sufficient `stock`.
    * Inserts or updates (`ON DUPLICATE KEY UPDATE`) the `order_item` quantity.
    * Decrements `product.stock` by `p_qty`.
    * Recalculates and updates `customer_order.total_cents` based on the sum of all items for that order.
    * Throws error '45000' if SKU is missing or stock is insufficient.

#### Dependencies
* `product`
* `customer_order`
* `order_item`
