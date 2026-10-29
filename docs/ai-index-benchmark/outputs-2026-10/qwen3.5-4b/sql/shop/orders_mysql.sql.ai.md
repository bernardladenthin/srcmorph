### orders_mysql.sql
- H: 1.0
- C: CD8641B0
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:04:37Z
- G: 
- A: 0.0.0
- X: file
---
> Manages product inventory, customer orders, and order line items for a web shop, including stock reservation and order total calculation.

#### Purpose
*   Models product catalog, customer orders, and order line items for a web shop.
*   Provides a procedure to add order items, reserve stock, and update order totals.

#### Type
Table, Procedure (MySQL 8)

#### Schema
**product**
*   `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
*   `sku` VARCHAR(40) NOT NULL (UNIQUE)
*   `name` VARCHAR(200) NOT NULL
*   `price_cents` INT UNSIGNED NOT NULL
*   `stock` INT NOT NULL DEFAULT 0

**customer_order**
*   `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT (PK)
*   `customer_id` BIGINT UNSIGNED NOT NULL
*   `status` ENUM('NEW', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'NEW'
*   `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
*   `total_cents` INT UNSIGNED NOT NULL DEFAULT 0
*   Index: `idx_order_customer` (customer_id, created_at)

**order_item**
*   `order_id` BIGINT UNSIGNED NOT NULL
*   `product_id` INT UNSIGNED NOT NULL (PK with order_id)
*   `quantity` SMALLINT UNSIGNED NOT NULL
*   `unit_price_cents` INT UNSIGNED NOT NULL
*   FK: `order_id` -> `customer_order.order_id` (ON DELETE CASCADE)
*   FK: `product_id` -> `product.product_id`

#### Reads
*   `product` table (sku, stock, price_cents)
*   `customer_order` table (order_id)
*   `order_item` table (quantity, unit_price_cents)

#### Writes
*   `order_item` table (order_id, product_id, quantity, unit_price_cents)
*   `product` table (stock)
*   `customer_order` table (total_cents)

#### Relationships
*   `order_item.order_id` -> `customer_order.order_id`
*   `order_item.product_id` -> `product.product_id`

#### Routine logic
*   **add_order_item**: Takes `p_order_id`, `p_sku`, `p_qty`.
*   Reserves stock in `product` for the given `sku` and `p_qty`.
*   Inserts or updates a row in `order_item` for the order and product.
*   Decrements `stock` in `product`.
*   Recalculates `total_cents` in `customer_order` based on `order_item` sums.
*   Signals error if SKU is unknown or stock is insufficient.

#### Dependencies
*   `product`
*   `customer_order`
*   `order_item`
